import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ExportService } from '../../export.service';
import { SiteService } from '../../site.service';
import { ToastService } from '../../toast.service';
import { SiteAppService } from '../../siteApp.service';
import { AppsService } from '../../apps.service';
import { SiteProductService } from '../../siteProduct.service';
import { faEye, faFile } from '@fortawesome/free-regular-svg-icons';
import { Cell, IColumnType, Settings } from 'angular2-smart-table';
import { LinkRendererComponent } from '../shared/link-renderer.component';

@Component({
  selector: 'site-details',
  templateUrl: './site-details.component.html'
})
export class SiteDetailsComponent implements OnInit {

  settings: Settings = {
    columns: {
      requestReferenceId: {
        title: '',
        classHeader: 'text-nowrap',
        filter: false,
        type: IColumnType.Custom,
        valuePrepareFunction: (cellValue: any, rowData: any, cell: Cell) => {
          return {
            routeInfo: [
              '/admin/product-support',
              rowData.siteCode,
              rowData.productCode,
            ]
          };
        },
        renderComponent: LinkRendererComponent,
      },
      productCode: {
        title: 'CODE',
        sortDirection: 'asc',
        placeholder: 'Filter by code...',
        classHeader: 'text-nowrap',
        classContent: 'text-nowrap',
      },
      dateSupported: {
        title: 'Supported',
        placeholder: 'Filter by date...'
      },
    },
    actions: false,
    attr: {
      class: 'table table-striped'
    },
    selectedRowIndex: -1
  };
  
  siteCode: string = "";
  site: any = {};
  stagingSiteApps: any[] = [];
  productionSiteApps: any[] = [];
  combinedSiteApps: any[] = [];
  productCategories: string[] = [];
  appToAddStaging: any = {};
  appToAddProduction: any = {};
  apps: any[] = [];
  siteProducts: any[] = [];
  faFile = faFile;
  faEye = faEye;
  
  constructor(private siteService: SiteService, 
    private route: ActivatedRoute,
    private toastService: ToastService,
    private siteAppService: SiteAppService,
    private appsService: AppsService,
    private siteProductService: SiteProductService,
    private exportService: ExportService,) {
    this.route.params.subscribe(params => {
      this.siteCode = params['code'];
    });
  }

  ngOnInit(): void {
    if(this.siteCode != ""){
      this.siteService.getSiteByCode(this.siteCode).subscribe(data => { 
        this.site = data;
        this.appsService.getApps().subscribe(data => {
          const categoryToCheck = this.site.category === 'DC' ? 'DC' : 'FACTORY';
          this.apps = data.filter((a: any) => a.category === categoryToCheck);
          this.apps.sort((a: any, b: any) => a.internalName < b.internalName ? -1 : 1);
        });
        if(this.site.productCategories){
          this.productCategories = this.site.productCategories.split(',');
        }
        this.siteProductService.getSiteProducts(this.site.id).subscribe((data: any) => {
          this.siteProducts = data;
          this.siteProducts.forEach((sp: any) => {
            sp.siteCode = sp.site.code;
            sp.productCode = sp.product.code;
          });
        });
        this.siteAppService.getSiteApps(this.site.id).subscribe((data: any) => {
          data.forEach((sa: any) => {
            sa.siteIdToSave = sa.site.id;
            sa.appIdToSave = sa.app.id;
          });
          this.combinedSiteApps = data;
          this.stagingSiteApps = data.filter((sa: any) => sa.environment == 'STAGING').sort((a: any, b: any) => a.app.internalName < b.app.internalName ? -1 : 1);
          this.productionSiteApps = data.filter((sa: any) => sa.environment == 'PRODUCTION').sort((a: any, b: any) => a.app.internalName < b.app.internalName ? -1 : 1);
        });
      });
    }
  }
  
  saveSite(){
    if(this.siteCode == ""){
      return; //do not auto-save for new product
    }
    if(this.site.code.indexOf("/") > -1){
      this.toastService.showToast("danger", "Invalid input", "Site code cannot contain /");
      return;
    }
    if(this.productCategories.length > 0){
      this.site.productCategories = this.productCategories.join(',');
    }
    this.siteService.updateSite(this.site).subscribe(
      (data) => {
        this.toastService.showToast("success", "Success", "Changes saved successfully");        
      },(error) => {
        console.error(error);
        this.toastService.showToast("danger", "Problem occured", "Unable to save changes");
      }
    );
  }

  addApp(isStaging: boolean) {
    var arrayToCheck = isStaging ? this.stagingSiteApps : this.productionSiteApps;
    var appToCheck = isStaging ? this.appToAddStaging : this.appToAddProduction;
    var exApp = arrayToCheck.filter((sa: any) => sa.appIdToSave == appToCheck.id).length > 0;
    if(exApp){
      this.toastService.showToast("danger", "Problem occured", "App already exists");
      return;
    }
    if(isStaging){
      const toAdd = {
        appNameToAdd: appToCheck.internalName,
        siteIdToSave: this.site.id,
        appIdToSave: appToCheck.id,
        environment: 'STAGING',
        vip: ''
      };
      this.stagingSiteApps.push(toAdd);
      this.combinedSiteApps.push(toAdd);
    }else{
      const toAdd = {
        appNameToAdd: appToCheck.internalName,
        siteIdToSave: this.site.id,
        appIdToSave: appToCheck.id,
        environment: 'PRODUCTION',
        v: ''
      };
      this.productionSiteApps.push(toAdd);
      this.combinedSiteApps.push(toAdd);
    }
  }

  saveSiteApps(){
    this.siteAppService.submitSiteApps(this.site.id, this.combinedSiteApps).subscribe(
      (data) => {
        this.toastService.showToast("success", "Success", "Changes saved successfully");        
        data.forEach((sa: any) => {
          sa.siteIdToSave = sa.site.id;
          sa.appIdToSave = sa.app.id;
        });
        this.combinedSiteApps = data;
        this.stagingSiteApps = data.filter((sa: any) => sa.environment == 'STAGING').sort((a: any, b: any) => a.app.internalName < b.app.internalName ? -1 : 1);
        this.productionSiteApps = data.filter((sa: any) => sa.environment == 'PRODUCTION').sort((a: any, b: any) => a.app.internalName < b.app.internalName ? -1 : 1);
      },(error) => {
        console.error(error);
        this.toastService.showToast("danger", "Problem occured", "Unable to save changes");
      }
    );
  }

  addSite(){
    if(this.site.code.indexOf("/") > -1){
      this.toastService.showToast("danger", "Invalid input", "Site code cannot contain /");
      return;
    }

    this.siteService.addSite(this.site).subscribe(
      (data) => {
        this.toastService.showToast("success", "Success", "Changes saved successfully"); 
        this.siteCode = data.code;
        this.site = data;
        this.ngOnInit();
      },(error) => {
        console.error(error);
        this.toastService.showToast("danger", "Invalid Request", error.error.message);
      }
    );
  }

  exportVips(siteCode: string, env: string, siteApps: any[]){
    const rows = siteApps.filter((sa: any) => sa.vip !== '-').map((sa: any) => {
      return {
        'SITE': sa.site.code,
        'APP': sa.app.externalName,
        'ENV': sa.environment === 'PRODUCTION' ? 'MP' : 'STAGING',
        'VIP': sa.vip
      };
    });
    this.exportService.exportToCsv(rows, `${siteCode}_${env}_VIPS`, ['SITE','APP','ENV','VIP']);
  }

  exportAggregatedVips(siteCode: string){
    let siteAppsAggregated = [];
    siteAppsAggregated.push(...this.stagingSiteApps);
    let stagingApps = this.stagingSiteApps.map((sa: any) => sa.app.externalName);
    let fillerMpApps = this.productionSiteApps.filter((sa: any) => !stagingApps.includes(sa.app.externalName) && sa.vip !== '-');
    siteAppsAggregated.push(...fillerMpApps);
    this.exportVips(siteCode, 'STAGING', siteAppsAggregated);
  }
}
