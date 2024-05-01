import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../auth.service';
import { FeatureService } from '../../feature.service';
import { ToastService } from '../../toast.service';
import { FeatureAppService } from '../../featureApp.service';
import { AppsService } from '../../apps.service';
import { ProductFeatureService } from '../../productFeature.service';
import { Clipboard } from '@angular/cdk/clipboard';
import { SiteService } from '../../site.service';
import { SiteFeatureService } from '../../siteFeature.service';
import { RequestService } from '../../request.service';
import { NgbDateParserFormatter, NgbDatepickerConfig } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of, filter } from 'rxjs';

@Component({
  selector: 'app-feature-details',
  templateUrl: './feature-details.component.html'
})
export class FeatureDetailsComponent implements OnInit {
  featureName: string = "";
  feature: any = {};
  possibleApps: any[] = [];
  appToAdd: any = {};
  featureApps: any[] = [];
  currentUserId: number = 0;
  productFeatures: any[] = [];
  splitPattern = /[,]/; 
  sites: any[] = [];
  possibleSites: any[] = [];
  request: any = {};
  needByDateVisible: boolean = false;
  needByDateParsed: any = null;
  targetSite: any = {};
  targetLink: string = '';
  isSre: boolean = false;
  showSreOwnAppsOnly: boolean = false;
  sreApps: any[] = [];

  constructor(private featureService: FeatureService, 
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private toastService: ToastService,
    private appsService: AppsService,
    private featureAppService: FeatureAppService,
    private productFeatureService: ProductFeatureService,
    private clipboard: Clipboard,
    private siteService: SiteService,
    private siteFeatureService: SiteFeatureService,
    private requestService: RequestService,
    private ngbDateParserFormatter: NgbDateParserFormatter,
    private datePickerConfig: NgbDatepickerConfig) {
      const current = new Date();
      datePickerConfig.minDate = { 
        year: current.getFullYear(), 
        month: current.getMonth()+1, 
        day: current.getDate() };
      datePickerConfig.outsideDays = 'hidden';

      this.route.params.subscribe(params => {
        this.featureName = params['name'];
      });
  }

  ngOnInit(): void {
    this.showSreOwnAppsOnly = localStorage.getItem('show-sre-apps-only-flag') === 'true';
    // this.authService.getCurrentUser().subscribe((data: any) => {
    //   this.currentUserId = data.id;
    //   this.authService.currentUser = data;
      this.isSre = this.authService.isUserInRole(this.authService.currentUser.roles, 'ROLE_SRE');
      if(this.isSre){
        this.appsService.getAppsDtoBySre().subscribe((data: any) => {
          this.sreApps = data;
        });
      }
    // }); 
    if(this.featureName != ""){
      this.featureService.getFeatureByName(this.featureName).subscribe(data => { 
        this.feature = data;   
        this.feature.radarsCsv = data.radars ? data.radars.split(",") : [];

        this.siteService.getSitesDto().subscribe(data => { 
          this.possibleSites = data;
          this.possibleSites.forEach((site: any) => {
            site.value = site.id;
            site.display = site.code;
          });
        });
        this.sites = [];
        this.siteFeatureService.getSitesByFeature(this.feature.id).subscribe(data => {
          data.forEach((sp: any) => {
            sp.site.value = sp.site.id;
            sp.site.display = sp.site.code;
            this.sites.push(sp.site);
          });
        });

        this.productFeatureService.getProductsByFeature(this.feature.id).subscribe((data: any) => {
          this.productFeatures = data;
        });
        this.featureAppService.getFeatureApps(this.feature.id).subscribe((data: any) => {
          data.forEach((fa: any) => {
            fa.featureIdToSave = fa.feature.id;
            fa.appIdToSave = fa.app.id;
            if(!fa.featureAppConfigs){
              fa.featureAppConfigs = [];
            }
          });
          this.featureApps = data;

          this.appsService.getApps().subscribe(data => {
            this.possibleApps = data;
            this.possibleApps.forEach((app: any) => {
              app.configure = false;

              app.radarsCsv = [];
              let foundFa = this.featureApps.find((fa: any) => fa.appIdToSave == app.id);
              if(foundFa && foundFa.radars){
                app.configure = true;
                app.radarsCsv = foundFa.radars.split(",");
              }

              app.appConfigs.sort((a: any, b: any) => a.name.toLowerCase() < b.name.toLowerCase() ? -1 : 1);
              app.appConfigs.forEach((ac: any) => {
                if(ac.multi){
                  ac.valuesCsv = ac.values.map((acv: any) => acv.val);
                }
                ac.values.forEach((acv: any) => {
                  acv.value = acv.val;
                  acv.display = acv.val;
                });
                ac.selectedValue = "";
                let foundFac = this.getMatchingFeatureAppConfig(app.id, ac.name);
                if(foundFac){
                  app.configure = true;
                  if(ac.multi && foundFac.val !== ""){
                    ac.selectedValue = foundFac.val.split(",");
                    ac.selectedValue.sort((a: any, b: any) => a.localeCompare(b, undefined, { numeric: true, sensitivity: 'base' }));
                  }else{
                    ac.selectedValue = foundFac.val;
                  }
                }
              });
            });
            this.possibleApps.sort((a: any, b: any) => a.internalName < b.internalName ? -1 : 1);
          });
        });
      });
    }
  }
  
  saveFeature(){
    if(!this.feature.id){
      return; //do not auto-save for new feature
    }
    if(this.feature.name.indexOf("/") > -1){
      this.toastService.showToast("danger", "Invalid input", "Feature name cannot contain /");
      return;
    }
    this.feature.radars = this.feature.radarsCsv ? this.feature.radarsCsv.join(',') : '';
    this.featureService.updateFeature(this.feature).subscribe(
      (data) => {
        this.toastService.showToast("success", "Success", "Changes saved successfully");        
      },(error) => {
        console.error(error);
        this.toastService.showToast("danger", "Problem occured", "Unable to save changes");
      }
    );
  }

  saveFeatureApps(){
    let changesDetected = false;

    this.possibleApps.forEach((app: any) => {
      let fa: any = {};
      let faConfigs: any[] = [];
      fa.featureIdToSave = this.feature.id;
      fa.appIdToSave = app.id;
      app.appConfigs.filter((ac: any) => ac.selectedValue && ac.selectedValue !== '').forEach((appConfig: any) => {
        faConfigs.push({
          name: appConfig.name,
          multi: appConfig.multi,
          val: appConfig.multi ? appConfig.selectedValue.join(',') : appConfig.selectedValue
        });
      });
      fa.featureAppConfigs = faConfigs;

      let foundFas = this.featureApps.filter((fa: any) => fa.appIdToSave == app.id);
      if(foundFas.length == 1){
        let foundFa = foundFas[0];

        //add new configs or update existing
        faConfigs.forEach((fac: any) => {
          let editedFacs = foundFa.featureAppConfigs.filter((existingFac: any) => existingFac.name == fac.name);
          if(editedFacs.length == 1){
            let editedFac = editedFacs[0];
            if(editedFac.val !== fac.val){
              changesDetected = true;
              editedFac.val = fac.val;
            }
          }else{
            changesDetected = true;
            foundFa.featureAppConfigs.push(fac);
          }
        });

        //delete unset configs
        if(foundFa.featureAppConfigs){
          foundFa.featureAppConfigs.forEach((existingFac: any) => {
            let foundFacs = faConfigs.filter((fac: any) => fac.name == existingFac.name);
            if(foundFacs.length == 0){
              changesDetected = true;
              foundFa.featureAppConfigs.splice(foundFa.featureAppConfigs.indexOf(existingFac), 1);
            }
          });
        }

        //delete unset apps
        if(!app.configure){
          changesDetected = true;
          foundFa.toRemove = true;
        }
      }else if(fa.featureAppConfigs.length > 0){
        changesDetected = true;
        this.featureApps.push(fa);
      }
    });

    if(changesDetected){
      this.featureAppService.submitFeatureApps(this.feature.id, this.featureApps).subscribe(
        (data) => {
          this.toastService.showToast("success", "Success", "Changes saved successfully");
        },(error) => {
          console.error(error);
          this.toastService.showToast("danger", "Problem occured", "Unable to save changes");
        }
      );
    }
    
  }

  getMatchingFeatureAppConfig(appId: number, configName: string){
    let foundFas = this.featureApps.filter((fa: any) => fa.appIdToSave == appId);
    if(foundFas.length == 1){
      let foundFa = foundFas[0];
      let foundFacs = foundFa.featureAppConfigs.filter((fac: any) => fac.name == configName);
      if(foundFacs.length == 1){
        return foundFacs[0];
        
      }
    }
    return null;
  }

  addFeature(){
    if(this.feature.name.indexOf("/") > -1){
      this.toastService.showToast("danger", "Invalid input", "Feature name cannot contain /");
      return;
    }

    this.featureService.addFeature(this.feature).subscribe(
      (data) => {
        this.toastService.showToast("success", "Success", "Changes saved successfully"); 
        this.featureName = data.name;
        this.feature = data;
        this.ngOnInit();
      },(error) => {
        console.error(error);
        this.toastService.showToast("danger", "Invalid Request", error.error.message);
      }
    );
  }

  showErrorAlert(){
    this.toastService.showToast('danger', 'Problem submitting data', 'Something went wrong, kindly contact admin');
  }

  copyToClipboard(item: any){
    this.clipboard.copy(item);
    this.toastService.showToast("info", "Copied to clipboard");
  }

  goToRequest(site: any){
    this.router.navigate(['/admin/feature-support', site.code, this.featureName]);
  }

  saveFeatureSites(){

    if(!this.needByDateParsed){
      this.toastService.showToast("danger", "Cannot proceed", "Need-by-date is mandatory");
      return;
    }

    this.needByDateVisible = false;

    this.request.userId = this.currentUserId;
    this.request.siteIdToSave = this.targetSite.id;
    this.request.featureIdToSave = this.feature.id;
    this.request.needByDate = this.ngbDateParserFormatter.format(this.needByDateParsed);
    
    setTimeout(() => {
      this.requestService.submitRequest(this.request).subscribe(
        (data: any) => {
          this.request.radar = data.radar;
          this.request.referenceId = data.referenceId;

          this.siteFeatureService.addSiteFeature(
            {
              siteIdToAdd: this.targetSite.id,
              featureIdToAdd: this.feature.id,
              requestReferenceId: this.request.referenceId
            }
          ).subscribe(
            (data: any) => {
              console.log("SiteFeature saved successfully");
            },
            (error: any) => {
              this.showErrorAlert();
            });

          this.toastService.showToast("success", "Success", `Feature Support details generated for: ${this.targetSite.code}`); 
          // this.showSuccess = true;
        },
        (error: any) => {
          // this.showSuccess = false;
          this.showErrorAlert();
        });
    },1000);
  }

  deleteFeatureSite(site: any){
    if(confirm(`This will cancel Feature Support for: ${site.code}, proceed?`)){
      this.requestService.cancelFeatureRequest(site.code, this.feature.name).subscribe(
        (data: any) => {
          this.toastService.showToast("warning", "Success", `Feature Support cancelled for: ${site.code}`); 
        },
        (error: any) => {
          this.showErrorAlert();
        });
    }else{
      this.ngOnInit();
    }
  }

  cancelNeedByDateModal() {
    this.needByDateVisible = !this.needByDateVisible;
    if(!this.needByDateVisible){
      this.ngOnInit();
    }
  }
  triggerAddSite(site: any){
    this.targetSite = site;
    this.needByDateParsed = '';
    this.needByDateVisible = true;
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

  viewRadar(radarId: any){
    this.copyToClipboard(radarId);
    this.targetLink = `rdar://${radarId}`;
    setTimeout(() => {
      document.getElementById("targetLinkTag")?.click();
    },500);
  }

  saveFeatureAppRadars(){
    this.possibleApps.forEach((app: any) => {
      let foundFa = this.featureApps.find((fa: any) => fa.appIdToSave == app.id);
      if(foundFa && app.radarsCsv){
        foundFa.radars = app.radarsCsv.join(',');
      }else if(!foundFa && app.radarsCsv){
        this.featureApps.push({
          featureIdToSave: this.feature.id,
          appIdToSave: app.id,
          featureAppConfigs: [],
          radars: app.radarsCsv.join(',')
        });
      }
    });
    this.featureAppService.updateFeatureAppsRadars(this.feature.id, this.featureApps).subscribe(
      (data) => {
        this.toastService.showToast("success", "Success", "Changes saved successfully");
      },(error) => {
        console.error(error);
        this.toastService.showToast("danger", "Problem occured", "Unable to save changes");
      }
    );
  }

  filteredApps(){
    if(this.showSreOwnAppsOnly){
      return this.possibleApps.filter((app: any) => this.sreApps.find((sreApp: any) => sreApp.id == app.id));
    }
    return this.possibleApps;
  }

  updateFilterAppsFlag(){
    localStorage.setItem('show-sre-apps-only-flag', this.showSreOwnAppsOnly.toString());
  }
}
