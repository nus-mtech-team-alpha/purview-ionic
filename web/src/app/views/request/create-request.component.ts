import { Component, OnInit } from '@angular/core';
import { NgbDatepickerConfig } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from '../../auth.service';
import { ProductService } from '../../product.service';
import { RequestService } from '../../request.service';
import { SiteService } from '../../site.service';
import { SiteAppService } from '../../siteApp.service';
import { SiteProductService } from '../../siteProduct.service';
import { ToastService } from '../../toast.service';

@Component({
  selector: 'app-create-request',
  templateUrl: './create-request.component.html',
  styleUrls: ['./create-request.component.scss']
})
export class CreateRequestComponent implements OnInit {
  request: any = {};
  sites: any = [];
  products: any = [];
  // siteProducts: any = [];
  siteApps: any = [];
  selectedSite: any = {};
  selectedSiteCode: string = "";
  selectedSiteCodeValid: boolean = true;
  selectedProduct: any = {};
  selectedProductCode: string = "";
  selectedProductCodeValid: boolean = true;
  selectedApps: any = [];
  selectedAppsCount: number = 0;
  apps: any = [];
  selectedServicesError: Boolean = false;
  errorOnSubmit: Boolean = false;
  showSuccess = false;
  currentUser: any = {};
  
  constructor(private siteService: SiteService, 
    private productService: ProductService, 
    private requestService: RequestService,
    private siteProductService: SiteProductService, 
    private siteAppService: SiteAppService, 
    private authService: AuthService,
    private toastService: ToastService,
    private datePickerConfig: NgbDatepickerConfig) { 
      // Set minDate and hide unavailable dates on NgbDatePicker
      // TODO: minDate is currently based on local client date. If needed, set minDate based on server date
      const current = new Date();
      datePickerConfig.minDate = { 
        year: current.getFullYear(), 
        month: current.getMonth()+1, 
        day: current.getDate() };
      datePickerConfig.outsideDays = 'hidden';
  }

  ngOnInit(): void {
    // this.authService.getCurrentUser().subscribe((data: any) => {
    //   this.currentUser = data;
    //   this.request.userId = this.currentUser.id;
    // }); 
    this.getSites();
  }

  getSites() {
    this.siteService.getSites().subscribe((data: any) => {
      this.sites = data;
    });
  }

  getAppsBySite(site: any) {
    this.selectedApps = [];
    this.selectedAppsCount = 0;

    if(this.selectedProduct.status == 'NPI'){
      this.request.environment = 'STAGING';
      this.siteAppService.getSiteAppsForNpi(site.id).subscribe((data: any) => {
        var isAnyStaging: boolean = false;
        data.forEach((item: any) => {
          item.app.selected = true;
          this.selectedApps.push(item.app);
          if(item.environment == 'STAGING'){
            isAnyStaging = true;
          }
        });
        this.selectedAppsCount = this.selectedApps.length;
        if(!isAnyStaging){
          this.request.environment = 'PRODUCTION';
        }
      });
    }else{
      this.request.environment = 'PRODUCTION';
      this.siteAppService.getSiteAppsEnv(site.id, 'PRODUCTION').subscribe((data: any) => {
        data.forEach((item: any) => {
          item.app.selected = true;
          this.selectedApps.push(item.app);
        });
        this.selectedAppsCount = this.selectedApps.length;
      });
    }
  }

  validateSiteCode() {
    this.selectedSiteCodeValid = true;
    this.selectedSite = this.sites.find((item: any) => item.code == this.selectedSiteCode);
    if(this.selectedSiteCode && !this.selectedSite){
      this.selectedSiteCodeValid = false;
    }
  }

  onChangeSite() {
    this.validateSiteCode();
    this.selectedProduct = null;
    delete this.request.productIdToSave;
    delete this.request.siteIdToSave;
    this.products = [];
    if(this.selectedSite){
      this.productService.getProductsBySiteCategory(this.selectedSite.id).subscribe((data: any) => {
        this.products = data;
      });
      this.request.siteIdToSave = this.selectedSite.id;
    }
  }

  validateProductCode() {
    this.selectedProductCodeValid = true;
    this.selectedProduct = this.products.find((item: any) => item.code == this.selectedProductCode);
    if(this.selectedProductCode && !this.selectedProduct){
      this.selectedProductCodeValid = false;
    }
  }

  onChangeProduct() {
    this.validateProductCode();
    delete this.request.productIdToSave;
    if(this.selectedProduct){
      this.request.productIdToSave = this.selectedProduct.id;
      this.getAppsBySite(this.selectedSite);
    }
  }

  updateSelectedApps(event: Event, app: any) {
    app.selected = (event.target as HTMLInputElement).checked;
    this.selectedAppsCount = this.selectedApps.filter((item: any) => item.selected).length;
  }

  submit() {
    var formattedDate = this.request.needByDate.year + "-" + String(this.request.needByDate.month).padStart(2, '0') + "-" + String(this.request.needByDate.day).padStart(2, '0');
    this.request.needByDate = formattedDate;
    this.request.appIds = this.selectedApps.filter((item: any) => item.selected).map((item: any) => item.id);
    this.requestService.submitRequest(this.request).subscribe(
      (data: any) => {
        this.request.radar = data.radar;
        this.request.referenceId = data.referenceId;
        this.showSuccess = true;
        this.reset();
      },
      (error: any) => {
        this.showSuccess = false;
        this.showErrorAlert();
      });
  }

  toggleModal() {
    this.showSuccess = !this.showSuccess;
  }

  handleModalChange(event: any) {
    this.showSuccess = event;
  }

  showErrorAlert(){
    this.toastService.showToast('danger', 'Problem submitting data', 'Something went wrong, kindly contact admin');
  }

  reset() {
    this.products = [];
    this.sites = [];
    this.getSites();
    var referenceId = this.request.referenceId;
    var radar = this.request.radar;
    this.request = {};
    this.request.userId = this.currentUser.id;
    this.request.referenceId = referenceId;
    this.request.radar = radar;
    this.selectedSite = {};
    this.selectedProduct = {};
    this.selectedApps = [];
    this.selectedAppsCount = 0;
  }
}
