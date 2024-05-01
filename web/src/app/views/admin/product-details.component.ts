import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../auth.service';
import { ProductService } from '../../product.service';
import { ToastService } from '../../toast.service';
import { SiteService } from '../../site.service';
import { SiteProductService } from '../../siteProduct.service';
import { ProductAppService } from '../../productApp.service';
import { AppsService } from '../../apps.service';
import { RequestService } from '../../request.service';
import { SiteAppService } from '../../siteApp.service';
import { FeatureService } from '../../feature.service';
import { ProductFeatureService } from '../../productFeature.service';
import { FeatureAppService } from '../../featureApp.service';
import { Clipboard } from '@angular/cdk/clipboard';
import { NgbDateParserFormatter, NgbDatepickerConfig } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of, filter } from 'rxjs';
import { faEraser } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html'
})
export class ProductDetailsComponent implements OnInit {

  productCode: string = "";
  product: any = {};
  sites: any[] = [];
  possibleSites: any[] = [];
  possibleApps: any[] = [];
  appToAdd: any = {};
  productApps: any[] = [];
  request: any = {};
  currentUserId: number = 0;
  splitPattern = /[,]/;
  features: any[] = [];
  possibleFeatures: any[] = [];
  selectedFeatureApps: any[] = [];
  needByDateVisible: boolean = false;
  configWarningVisible: boolean = false;
  needByDateParsed: any = null;
  targetSite: any = {};
  violatedFeatures: any[] = [];
  targetLink: string = '';
  isSre: boolean = false;
  showSreOwnAppsOnly: boolean = false;
  sreApps: any[] = [];
  faEraser = faEraser;
  
  constructor(private productService: ProductService, 
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private toastService: ToastService,
    private siteService: SiteService,
    private siteProductService: SiteProductService,
    private appsService: AppsService,
    private productAppService: ProductAppService,
    private requestService: RequestService,
    private siteAppService: SiteAppService,
    private featureService: FeatureService,
    private productFeatureService: ProductFeatureService,
    private featureAppService: FeatureAppService,
    private clipboard: Clipboard,
    private ngbDateParserFormatter: NgbDateParserFormatter,
    private datePickerConfig: NgbDatepickerConfig) {
      const current = new Date();
      datePickerConfig.minDate = { 
        year: current.getFullYear(), 
        month: current.getMonth()+1, 
        day: current.getDate() };
      datePickerConfig.outsideDays = 'hidden';

      this.route.params.subscribe(params => {
        this.productCode = params['code'];
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
    if(this.productCode !== ""){
      this.productService.getProductByCode(this.productCode).subscribe(data => { 
        this.product = data;
        this.product.radarsCsv = data.radars ? data.radars.split(",") : [];
        this.siteService.getSitesByCategory(this.product.category).subscribe(data => { 
          this.possibleSites = data.sort((a: any, b: any) => a.code < b.code ? -1 : 1);
          this.possibleSites.forEach((site: any) => {
            site.value = site.id;
            site.display = site.code;
          });
        });
        this.sites = [];
        this.siteProductService.getSitesByProduct(this.product.id).subscribe(data => {
          data.forEach((sp: any) => {
            sp.site.value = sp.site.id;
            sp.site.display = sp.site.code;
            this.sites.push(sp.site);
          });
        });
        
        this.productAppService.getProductApps(this.product.id).subscribe((data: any) => {
          data.forEach((pa: any) => {
            pa.productIdToSave = pa.product.id;
            pa.appIdToSave = pa.app.id;
            if(!pa.productAppConfigs){
              pa.productAppConfigs = [];
            }
          });
          this.productApps = data;

          this.appsService.getApps().subscribe(data => {
            this.possibleApps = data;
            this.possibleApps.forEach((app: any) => {

              app.configure = false;

              app.radarsCsv = [];
              let foundPa = this.productApps.find((pa: any) => pa.appIdToSave == app.id);
              if(foundPa && foundPa.radars){
                app.configure = true;
                app.radarsCsv = foundPa.radars.split(",");
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
                let foundPac = this.getMatchingProductAppConfig(app.id, ac.name);
                if(foundPac){
                  app.configure = true;
                  if(ac.multi && foundPac.val !== ""){
                    ac.selectedValue = foundPac.val.split(",");
                    ac.selectedValue.sort((a: any, b: any) => a.localeCompare(b, undefined, { numeric: true, sensitivity: 'base' }));
                  }else{
                    ac.selectedValue = foundPac.val;
                  }
                }
              });
            });
            this.possibleApps.sort((a: any, b: any) => a.internalName < b.internalName ? -1 : 1);
          });
        });

        this.featureService.getFeatures().subscribe(features => {
          this.possibleFeatures = features;
          this.possibleFeatures.forEach((feature: any) => {
            feature.value = feature.id;
            feature.display = feature.name;
          });
        });
        this.features = [];
        this.selectedFeatureApps = [];
        this.productFeatureService.getProductFeatures(this.product.id).subscribe((data: any) => {
          data.forEach((pf: any) => {
            pf.feature.value = pf.feature.id;
            pf.feature.display = pf.feature.name;
            this.features.push(pf.feature);
            this.featureAppService.getFeatureApps(pf.feature.id).subscribe((faData: any) => {
              this.selectedFeatureApps = this.selectedFeatureApps.concat(faData);
            });
          });
        });
      });
    }
  }
  
  saveProduct(){
    if(this.productCode == ""){
      return; //do not auto-save for new product
    }
    if(this.product.code.indexOf("/") > -1){
      this.toastService.showToast("danger", "Invalid input", "Product code cannot contain /");
      return;
    }
    this.product.radars = this.product.radarsCsv ? this.product.radarsCsv.join(',') : '';
    this.productService.updateProduct(this.product).subscribe(
      (data) => {
        this.toastService.showToast("success", "Success", "Changes saved successfully");        
      },(error) => {
        console.error(error);
        this.toastService.showToast("danger", "Problem occured", "Unable to save changes");
      }
    );
  }

  updateProductPhase(){
    if(this.productCode == ""){
      return; //do not auto-save for new product
    }
    if(this.product.status == "MP"){
      if(!confirm("Changing to MP phase will add MP actions on linked sites, confirm?")){
        this.ngOnInit();
        return;
      }
    }
    
    this.productService.updateProductPhase(this.product).subscribe(
      (data) => {
        this.toastService.showToast("success", "Success", "Product phase updated successfully");        
      },(error) => {
        console.error(error);
        this.toastService.showToast("danger", "Problem occured", "Unable to save changes");
      }
    );
  }

  validateFeatureSpecs() {
    this.violatedFeatures = [];
    this.possibleApps.forEach((app: any) => {
      app.appConfigs.filter((ac: any) => ac.selectedValue).forEach((appConfig: any) => {
        let matchingFeatureApps = this.selectedFeatureApps.filter((fa: any) => fa.app.id == app.id);
        matchingFeatureApps.forEach((fa: any) => {
          let matchingFacs = fa.featureAppConfigs.filter((fac: any) => fac.name == appConfig.name);
          if(matchingFacs.length == 1 && matchingFacs[0].val !== ""){
            if(matchingFacs[0].multi){
              let matchingFacsVals = matchingFacs[0].val.split(",");
              let selectedVals = appConfig.selectedValue;
              let checker = (arr: any[], target: any[]) => target.every(v => arr.includes(v));
              if(!checker(selectedVals, matchingFacsVals)){
                this.violatedFeatures.push({
                  featureName: fa.feature.name,
                  configName: matchingFacs[0].name,
                  configValues: matchingFacsVals,
                  selectedValues: selectedVals
                });
              }
            }else{
              if(matchingFacs[0].val.localeCompare(appConfig.selectedValue, undefined, { numeric: true, sensitivity: 'base' }) == 1){   
                this.violatedFeatures.push({
                  featureName: fa.feature.name,
                  configName: matchingFacs[0].name,
                  configValues: matchingFacs[0].val,
                  selectedValues: appConfig.selectedValue
                });
              }
            }
          }
        });
      });
    });
  }

  saveProductApps(){

    this.validateFeatureSpecs();
    if(this.violatedFeatures.length > 0){
      this.configWarningVisible = true;
    }else{
      this.proceedWithConfigChanges();
    }
  }

  getMatchingProductAppConfig(appId: number, configName: string){
    let foundPas = this.productApps.filter((pa: any) => pa.appIdToSave == appId);
    if(foundPas.length == 1){
      let foundPa = foundPas[0];
      let foundPacs = foundPa.productAppConfigs.filter((pac: any) => pac.name == configName);
      if(foundPacs.length == 1){
        return foundPacs[0];
        
      }
    }
    return null;
  }

  addProduct(){
    if(this.product.code.indexOf("/") > -1){
      this.toastService.showToast("danger", "Invalid input", "Product code cannot contain /");
      return;
    }

    this.productService.addProduct(this.product).subscribe(
      (data) => {
        this.toastService.showToast("success", "Success", "Changes saved successfully"); 
        this.productCode = data.code;
        this.product = data;
        this.ngOnInit();
      },(error) => {
        console.error(error);
        this.toastService.showToast("danger", "Invalid Request", error.error.message);
      }
    );
  }

  saveProductSites(){

    if(!this.needByDateParsed){
      this.toastService.showToast("danger", "Cannot proceed", "Need-by-date is mandatory");
      return;
    }

    this.needByDateVisible = false;

    this.request.userId = this.currentUserId;
    this.request.siteIdToSave = this.targetSite.id;
    this.request.productIdToSave = this.product.id;
    this.request.needByDate = this.ngbDateParserFormatter.format(this.needByDateParsed);

    setTimeout(() => {
      this.requestService.submitRequest(this.request).subscribe(
        (data: any) => {
          this.request.radar = data.radar;
          this.request.referenceId = data.referenceId;

          this.siteProductService.addSiteProduct(
            {
              siteIdToAdd: this.targetSite.id,
              productIdToAdd: this.product.id,
              requestReferenceId: this.request.referenceId
            }
          ).subscribe(
            (data: any) => {
              console.log("SiteProduct saved successfully");
            },
            (error: any) => {
              this.showErrorAlert();
            });

          this.toastService.showToast("success", "Success", `Product Support details generated for: ${this.targetSite.code}`); 
          // this.showSuccess = true;
        },
        (error: any) => {
          // this.showSuccess = false;
          this.showErrorAlert();
        });
    },1000);
  }

  saveProductFeatures(){
    this.productFeatureService.submitProductFeatures(this.product.id, this.features
    ).subscribe(
      (data: any) => {
        this.toastService.showToast("success", "Success", "Feature successfully added to this product");
        this.ngOnInit();
      },
      (error: any) => {
        this.showErrorAlert();
      });
  }

  deleteProductFeature(feature: any){
    this.productFeatureService.deleteProductFeature(this.product.id, feature.id
    ).subscribe(
      (data: any) => {
        this.toastService.showToast("info", "Success", "Feature successfully removed to this product"); 
        this.ngOnInit();
      },
      (error: any) => {
        this.showErrorAlert();
      });
  }

  showProductHistory(){
    this.productService.getProductSnapshots(this.product.id).subscribe((data: any) => {
      console.log(data);
    });
  }

  showErrorAlert(){
    this.toastService.showToast('danger', 'Problem submitting data', 'Something went wrong, kindly contact admin');
  }

  copyToClipboard(item: any){
    this.clipboard.copy(item);
    this.toastService.showToast("info", "Copied to clipboard");
  }

  goToRequest(site: any){
    this.router.navigate(['/admin/product-support', site.code, this.productCode]);
  }
  
  deleteProductSite(site: any){
    if(confirm(`This will cancel Product Support for: ${site.code}, proceed?`)){
      this.requestService.cancelProductRequest(site.code, this.productCode).subscribe(
        (data: any) => {
          this.toastService.showToast("warning", "Success", `Product Support cancelled for: ${site.code}`); 
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

  cancelSaveDueToWarnings(){
    this.configWarningVisible = !this.configWarningVisible;
    this.ngOnInit();
  }

  proceedWithConfigChanges(){
    this.configWarningVisible = false;
    let changesDetected = false;
    this.possibleApps.forEach((app: any) => {
      let pa: any = {};
      let paConfigs: any[] = [];
      pa.productIdToSave = this.product.id;
      pa.appIdToSave = app.id;
      app.appConfigs.filter((ac: any) => ac.selectedValue && ac.selectedValue !== '').forEach((appConfig: any) => {
        paConfigs.push({
          name: appConfig.name,
          multi: appConfig.multi,
          val: appConfig.multi ? appConfig.selectedValue.join(',') : appConfig.selectedValue,
          toDelete: appConfig.toDelete
        });
      });
      pa.productAppConfigs = paConfigs;

      let foundPa = this.productApps.find((pa: any) => pa.appIdToSave == app.id);
      if(foundPa){
        //add new configs or update existing
        paConfigs.forEach((pac: any) => {
          let existingPac = foundPa.productAppConfigs.find((exPac: any) => exPac.name == pac.name);
          if(existingPac){
            if(existingPac.val !== pac.val){
              changesDetected = true;
              existingPac.val = pac.val;
            }else if(pac.toDelete){
              changesDetected = true;
              existingPac.toDelete = true;
            }
          }else{
            changesDetected = true;
            foundPa.productAppConfigs.push(pac);
          }
        });

        //delete unset apps
        if(!app.configure){
          changesDetected = true;
          foundPa.toRemove = true;
        }
      }else if(pa.productAppConfigs.length > 0){
        changesDetected = true;
        this.productApps.push(pa);
      }
    });

    if(changesDetected){
      this.productAppService.submitProductApps(this.product.id, this.productApps).subscribe(
        (data) => {
          this.toastService.showToast("success", "Success", "Changes saved successfully");
          this.ngOnInit();
        },(error) => {
          console.error(error);
          this.toastService.showToast("danger", "Problem occured", "Unable to save changes");
        }
      );
    }
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

  saveProductAppRadars(){
    this.possibleApps.forEach((app: any) => {
      let foundPa = this.productApps.find((pa: any) => pa.appIdToSave == app.id);
      if(foundPa && app.radarsCsv){
        foundPa.radars = app.radarsCsv.join(',');
      }else if(!foundPa && app.radarsCsv){
        this.productApps.push({
          productIdToSave: this.product.id,
          appIdToSave: app.id,
          productAppConfigs: [],
          radars: app.radarsCsv.join(',')
        });
      }
    });
    this.productAppService.updateProductAppsRadars(this.product.id, this.productApps).subscribe(
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

  clearConfigSetValue(appConfig: any){
    if(confirm(`This will clear ${appConfig.name} value in this product and delete matching tasks in requests, proceed?`)){
      appConfig.toDelete = true;
      this.saveProductApps();
    }
  }

  changeSetup(app: any){
    if(!app.configure){
      if(confirm(`This will remove all ${app.internalName} config values from this product and delete matching action in requests, proceed?`)){
        app.configure = false;
        this.saveProductApps();
      }else{
        this.ngOnInit();
      }
    }
    
  }
}
