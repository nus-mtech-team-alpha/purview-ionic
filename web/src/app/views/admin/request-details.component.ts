import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Clipboard } from '@angular/cdk/clipboard';
import { RequestService } from '../../request.service';
import { ToastService } from '../../toast.service';
import { ProductAppService } from '../../productApp.service';
import { Observable, of, filter } from 'rxjs';
import { FeatureAppService } from '../../featureApp.service';
import { AuthService } from '../../auth.service';
import { faFilePdf, faFileText, faThumbsUp } from '@fortawesome/free-regular-svg-icons';
import { NgbDateParserFormatter, NgbDatepickerConfig } from '@ng-bootstrap/ng-bootstrap';
import { SiteAppService } from '../../siteApp.service';
import { ExportService } from '../../export.service';
import { faFile } from '@fortawesome/free-regular-svg-icons';
import { DefaultLayoutComponent } from '../../containers';
import { faCircleInfo, faRefresh } from '@fortawesome/free-solid-svg-icons';
import { AppsService } from '../../apps.service';

@Component({
  selector: 'app-request-details',
  templateUrl: './request-details.component.html'
})
export class RequestDetailsComponent implements OnInit {

  requestId: string = '';
  siteCode: string = '';
  productCode: string = '';
  featureName: string = '';

  request: any = {};
  splitPattern = /[,]/; 
  productApps: any = [];
  featureApps: any = [];
  isSre: boolean = false;
  printRecords: any[] = [];
  faFilePdf = faFilePdf;
  faFileText = faFileText;
  faLike = faThumbsUp;
  targetLink: string = '';
  siteApps: any[] = [];
  faFile = faFile;
  faCircleInfo = faCircleInfo;
  configWarningVisible: boolean = false;
  violatedPacs: any[] = [];
  showSreOwnAppsOnly: boolean = false;
  sreApps: any[] = [];
  faRefresh = faRefresh;

  constructor(private requestService: RequestService, 
    private route: ActivatedRoute,
    private toastService: ToastService,
    private productAppService: ProductAppService,
    private featureAppService: FeatureAppService,
    private clipboard: Clipboard,
    private authService: AuthService,
    private ngbDateParserFormatter: NgbDateParserFormatter,
    private datePickerConfig: NgbDatepickerConfig,
    private siteAppsService: SiteAppService,
    private exportService: ExportService,
    private defaultLayoutComponent: DefaultLayoutComponent,
    private appsService: AppsService) { 
      // Set minDate and hide unavailable dates on NgbDatePicker
      // TODO: minDate is currently based on local client date. If needed, set minDate based on server date
      const current = new Date();
      datePickerConfig.minDate = { 
        year: current.getFullYear(), 
        month: current.getMonth()+1, 
        day: current.getDate() };
      datePickerConfig.outsideDays = 'hidden';
      
      this.route.params.subscribe(params => {
        this.requestId = params['referenceId'];
        this.siteCode = params['siteCode'];
        this.productCode = params['productCode'];
        this.featureName = params['featureName'];
      });
  }

  ngOnInit(): void {
    this.showSreOwnAppsOnly = localStorage.getItem('show-sre-apps-only-flag') === 'true';
    // this.authService.getCurrentUser().subscribe((data: any) => {
    //   this.authService.currentUser = data;
    this.isSre = this.authService.isUserInRole(this.authService.currentUser.roles, 'ROLE_SRE');
    if(this.isSre){
      this.appsService.getAppsDtoBySre().subscribe((data: any) => {
        this.sreApps = data;
      });
    }
    // }); 

    if(this.productCode){
      this.requestService.getProductRequest(this.siteCode, this.productCode).subscribe((data: any) => {
        this.loadDetails(data);
      });
    }else if(this.featureName){
      this.requestService.getFeatureRequest(this.siteCode, this.featureName).subscribe((data: any) => {
        this.loadDetails(data);
      });
    }else if(this.requestId){
      this.requestService.getRequestByReferenceId(this.requestId).subscribe((data: any) => {
        this.loadDetails(data);
      });
    }
  }

  loadDetails(data: any){
    this.request = data;
    this.request.needByDateParsed = this.ngbDateParserFormatter.parse(this.request.needByDate);

    this.siteAppsService.getSiteApps(this.request.site.id).subscribe((data: any) => {
      this.siteApps = data;
    });

    if(this.request.product){
      this.productAppService.getProductApps(this.request.product.id).subscribe((data: any) => {
        this.productApps = data;
        this.loadActions();
      });
    }else if(this.request.feature){
      this.featureAppService.getFeatureApps(this.request.feature.id).subscribe((data: any) => {
        this.featureApps = data;
        this.loadActions();
      });
    }
  }

  loadActions(){
    this.request.actions.sort((a: any, b: any) => a.app.internalName < b.app.internalName ? -1 : 1)
                        .sort((a: any, b: any) => a.environment < b.environment ? -1 : 1);

    this.request.actions.forEach((action: any) => {
      action.crNumbersCsv = action.crNumbers ? action.crNumbers.split(",") : [];
      action.radarsCsv = action.radars ? action.radars.split(",") : [];
      this.printRecords.push({
        type: 'header',
        title: `${action.app.internalName.toUpperCase()} ${action.environment}`,
        action: action,
      });
      action.tasks.sort((a: any, b: any) => a.appConfig.name.toLowerCase() < b.appConfig.name.toLowerCase() ? -1 : 1);
      action.tasks.forEach((task: any) => {
        task.appConfig.values.sort((a: any, b: any) => b.id - a.id);
        task.selectedValue = task.appConfigSetValue;
        if(task.appConfig.multi){
          task.appConfig.valuesCsv = task.appConfig.values.map((cv: any) => cv.val);
          task.selectedValue = task.appConfigSetValue ? task.appConfigSetValue.split(",") : [];
          task.selectedValue.sort((a: any, b: any) => a.localeCompare(b, undefined, { numeric: true, sensitivity: 'base' }));
        }
        if(task.appConfig.recommend){
          task.minRequired = this.getMinRequired(action, task);
        }
        this.printRecords.push({
          type: 'content-task',
          title: task.appConfig.name,
          action: action,
          task: task,
        });
      });
      this.printRecords.push({
        type: 'content-approved',
        title: 'Approved By',
        action: action,
      });
      this.printRecords.push({
        type: 'footer'
      });
    });

    this.printRecords.pop();
  }
  
  validateSpecs(itemApps: any[]) {
    this.violatedPacs = [];

    this.request.actions.forEach((action: any) => {
      action.tasks.forEach((task: any) => {
        let matchingItemApps = itemApps.filter((ga: any) => ga.app.id == action.app.id);
        matchingItemApps.forEach((ga: any) => {
          let matchingConfigs = this.request.product ? ga.productAppConfigs : ga.featureAppConfigs;
          let matchingConfigFiltered = matchingConfigs.filter((gc: any) => gc.name == task.appConfig.name);
          if(matchingConfigFiltered.length == 1 && matchingConfigFiltered[0].val !== ""){
            let matchingConfig = matchingConfigFiltered[0];
            if(matchingConfig.multi){
              let matchingPacsVals = matchingConfig.val.split(",");
              let selectedVals = task.selectedValue;
              let checker = (arr: any[], target: any[]) => target.every(v => arr.includes(v));
              if(!checker(selectedVals, matchingPacsVals)){
                this.violatedPacs.push({
                  appName: ga.app.internalName,
                  configName: matchingConfig.name,
                  configValues: matchingPacsVals,
                  selectedValues: selectedVals
                });
              }
            }else{
              if(matchingConfig.val.localeCompare(task.selectedValue, undefined, { numeric: true, sensitivity: 'base' }) == 1){
                this.violatedPacs.push({
                  appName: ga.app.internalName,
                  configName: matchingConfig.name,
                  configValues: matchingConfig.val,
                  selectedValues: task.selectedValue
                });
              }
            }
          }
        });
      });
    });
  }

  saveRequest(){

    this.request.product ? this.validateSpecs(this.productApps) : this.validateSpecs(this.featureApps);
    if(this.violatedPacs.length > 0){
      this.configWarningVisible = true;
      return;
    }else{
      this.proceedWithConfigChanges();
    }
    
  }

  updateAction(event: Event, action: any){
    var selected = (event.target as HTMLInputElement).checked;
    action.status = selected ? 'COMPLETED' : 'REVIEW';
    action.tasks.forEach((task: any) => {
      task.status = selected ? 'COMPLETED' : 'REVIEW';
    });
    this.saveRequest();
  }

  updateTask(event: Event, action:any, task: any){
    var selected = (event.target as HTMLInputElement).checked;
    task.status = selected ? 'COMPLETED' : 'REVIEW';
    task.completedDate = null;
    var completedTaskCount = action.tasks.filter((t: any) => t.status == 'COMPLETED').length;
    if(completedTaskCount > 0 && completedTaskCount == action.tasks.length){
      action.status = 'COMPLETED';
    }else{
      action.status = 'REVIEW';
    }
    this.saveRequest();
  }

  public validateCrInput(tag: any): Observable<any> {
    const pattern = /^CHG\d{9}$/;
    const compliant = pattern.test(tag);  
    return of(tag)
        .pipe(filter(() => {
            if(!compliant){
              alert("Invalid CR ID Format. Must use the pattern CHG{9-digit}");
            }
            return compliant;
        }));
  }

  public validateRadarInput(tag: any): Observable<any> {
    const pattern = /^\d{9}$/;
    const compliant = pattern.test(tag);  
    return of(tag)
        .pipe(filter(() => {
            if(!compliant){
              alert("Invalid Radar ID Format. Must use the pattern {9-digit}");
            }
            return compliant;
        }));
  }

  copyToClipboard(item: any){
    this.clipboard.copy(item);
    this.toastService.showToast("info", "Copied to clipboard");
  }

  viewRadar(radarId: any){
    this.copyToClipboard(radarId);
    this.targetLink = `rdar://${radarId}`;
    setTimeout(() => {
      document.getElementById("targetLinkTag")?.click();
    },500);
  }

  viewCr(crId: any){
    this.copyToClipboard(crId);
    this.targetLink = `https://hcl.apple.com/tkt.do?tkt=${crId}`;
    setTimeout(() => {
      document.getElementById("targetLinkTag")?.click();
    },500);
  }

  approveAction(action: any){

    if(action.toIgnore){
      this.toastService.showToast("danger", "Unable to approve", "Action is set to be ignored");
      return;
    }

    action.verified = true;
    this.requestService.updateActionVerification(action).subscribe(
      (data) => {
        this.toastService.showToast("success", "Success", "Action verified successfully");
        action.verifier = data.verifier;
        action.verifiedBy = data.verifiedBy;
        action.verifiedDate = data.verifiedDate;
      },(error) => {
        this.toastService.showToast("danger", "Unauthorized", "You are not the SRE DRI for this app");
        this.ngOnInit();
      }
    );
  }

  setActionIgnore(action: any){
    action.toIgnore = !action.toIgnore;
    this.requestService.updateActionIgnore(action).subscribe(
      (data) => {
        this.toastService.showToast("success", "Success", "Action updated successfully");
      },(error) => {
        this.toastService.showToast("danger", "Unauthorized", "You are not the SRE DRI for this app");
        this.ngOnInit();
      }
    );
  }

  printDetails(){
    if(this.defaultLayoutComponent.darkMode){
      this.defaultLayoutComponent.darkMode = false;
      setTimeout(() => {
        window.print();
        this.defaultLayoutComponent.darkMode = true;
      },100);
    }else{
      window.print();
    }
  }

  incrementAndSaveRequest(){
    this.request.version++;
    this.saveRequest();
  }

  exportVips(){
    let siteAppsAggregated = [];
    let targetEnv = 'MP';
    let stagingSiteApps = this.siteApps.filter((sa: any) => sa.environment == 'STAGING');
    let productionSiteApps = this.siteApps.filter((sa: any) => sa.environment == 'PRODUCTION');
    let anyStaging = stagingSiteApps.length > 0;
    let stagingApps: any[] = [];
    if(anyStaging && (this.request.product && this.request.product.status == 'NPI')){
      targetEnv = 'STAGING';
      siteAppsAggregated.push(...stagingSiteApps);
      stagingApps = stagingSiteApps.map((sa: any) => sa.app.externalName);
    }

    let fillerMpApps = productionSiteApps.filter((sa: any) => !stagingApps.includes(sa.app.externalName) && sa.vip !== '-');
    siteAppsAggregated.push(...fillerMpApps);
    
    const rows = siteAppsAggregated.filter((sa: any) => sa.vip !== '-').map((sa: any) => {
      return {
        'SITE': sa.site.code,
        'APP': sa.app.externalName,
        'ENV': sa.environment === 'PRODUCTION' ? 'MP' : 'STAGING',
        'VIP': sa.vip
      };
    });
    this.exportService.exportToCsv(rows, `${this.siteCode}_${targetEnv}_VIPS`, ['SITE','APP','ENV','VIP']);
  }

  cancelSaveDueToWarnings(){
    this.configWarningVisible = !this.configWarningVisible;
    this.ngOnInit();
  }

  proceedWithConfigChanges(){
    this.configWarningVisible = false;
    if(this.request.status == 'CANCELLED' || this.request.status == 'REJECTED'){
      this.toastService.showToast("danger", "Invalid Action", `Request is already ${this.request.status}`);
      this.ngOnInit();
      return;
    }
    let completedActionCount = this.request.actions.filter((a: any) => a.status == 'COMPLETED').length;
    let totalActionCount = this.request.actions.filter((a: any) => !a.toIgnore).length;
    if(completedActionCount > 0 && completedActionCount == totalActionCount){
      this.request.status = 'COMPLETED';
    }else{
      this.request.status = 'IN_PROGRESS';
    }

    this.request.actions.flatMap((a: any) => a.tasks).forEach((task: any) => {
      task.appConfigSetValue = task.appConfig.multi ? task.selectedValue.join(',') : task.selectedValue;
    });
    this.request.actions.forEach((a: any) => {
      a.crNumbers = a.crNumbersCsv ? a.crNumbersCsv.join(',') : '';
      a.radars = a.radarsCsv ? a.radarsCsv.join(',') : '';
    });

    this.request.needByDate = this.ngbDateParserFormatter.format(this.request.needByDateParsed);
    this.requestService.updateRequestActions(this.request).subscribe(
      (data) => {
        this.toastService.showToast("success", "Success", "Changes saved successfully");   
      },(error) => {
        console.error(error);
        this.toastService.showToast("danger", "Problem occured", "Unable to save changes");
      }
    );
  }

  filteredActions(){
    if(this.showSreOwnAppsOnly){
      return this.request.actions.filter((action: any) => this.sreApps.find((sreApp: any) => sreApp.id == action.app.id));
    }
    return this.request.actions;
  }

  updateFilterAppsFlag(){
    localStorage.setItem('show-sre-apps-only-flag', this.showSreOwnAppsOnly.toString());
  }

  getMinRequired(action: any, task: any){
    if(this.request.product){
      let matchingPa = this.productApps.find((pa: any) => pa.app.id == action.app.id);
      return matchingPa.productAppConfigs.find((pac: any) => pac.name == task.appConfig.name).val;
    }else if(this.request.feature){
      let matchingFa = this.featureApps.find((fa: any) => fa.app.id == action.app.id);
      return matchingFa.featureAppConfigs.find((fac: any) => fac.name == task.appConfig.name).val;
    }
  }

  refreshConfig(task: any, appConfig: any){
    let currentSelectedValue = task.selectedValue;
    this.appsService.getAppConfigLatestJmetAnsible(appConfig).subscribe((data: any) => {
      task.appConfig = data;
      task.appConfig.values.sort((a: any, b: any) => b.val.localeCompare(a.val, undefined, { numeric: true, sensitivity: 'base' }));
      let latest = task.appConfig.values[0];
      if(latest.val !== currentSelectedValue){
        task.selectedValue = latest.val;
        this.incrementAndSaveRequest();
      }else{
        this.toastService.showToast("info", "No updates", "Latest value is still the same");
      }
    });
  }

  exportJson(){
    if(this.productCode){
      this.requestService.getProductRequestDto(this.siteCode, this.productCode).subscribe((data: any) => {
        this.exportService.exportToTxt(JSON.stringify(data, null, 2), `${this.siteCode}_${data.productCode}_v${data.version}`);
      });
    }else if(this.featureName){
      this.requestService.getFeatureRequestDto(this.siteCode, this.featureName).subscribe((data: any) => {
        this.exportService.exportToTxt(JSON.stringify(data, null, 2), `${this.siteCode}_${data.featureName}_v${data.version}`);
      });
    }else if(this.requestId){
      this.requestService.getRequestDtoByReferenceId(this.requestId).subscribe((data: any) => {
        let target = data.productCode ? data.productCode : data.featureName;
        this.exportService.exportToTxt(JSON.stringify(data, null, 2), `${this.siteCode}_${target}_v${data.version}`);
      });
    }
  }
}
