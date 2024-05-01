import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../product.service';
import { RequestService } from '../../request.service';
import { AppsService } from '../../apps.service';

@Component({
  selector: 'app-product-readiness',
  templateUrl: './product-readiness.component.html'
})
export class ProductReadinessComponent implements OnInit {

  productsToSearch: any[] = [];
  appsToSearch: any[] = [];
  possibleProducts: any[] = [];
  possibleApps: any[] = [];
  filteredApps: any[] = [];
  requests: any[] = [];


  constructor(private productService: ProductService,
    private requestService: RequestService,
    private appsService: AppsService) { }

  ngOnInit() {
    this.appsService.getAppsDto().subscribe((data: any) => {
      data.forEach((a: any) => {
        a.value = a.id;
        a.display = a.internalName;
      });
      this.possibleApps = data;
      
      let savedAppSearch = localStorage.getItem('lastAppIds');
      if(savedAppSearch){
        let savedAppSearchArray = savedAppSearch.split(',');
        this.appsToSearch = this.possibleApps.filter(a => savedAppSearchArray.includes(a.id.toString()));
        this.filteredApps = this.appsToSearch;
      }else{
        this.filteredApps = this.possibleApps;
      }

      this.productService.getProducts().subscribe((data: any) => {
        data.forEach((p: any) => {
          p.value = p.code;
          p.display = p.code;
        });
        this.possibleProducts = data;
        
        let savedProductSearch = localStorage.getItem('lastProductCodes');
        if(savedProductSearch){
          let savedProductSearchArray = savedProductSearch.split(',');
          this.productsToSearch = this.possibleProducts.filter(p => savedProductSearchArray.includes(p.code));
          this.doSearch();
        }
      });

    });

  }

  doSearch(){
    if(this.productsToSearch.length > 0){
      let productCodes = this.productsToSearch.map(p => p.code).join(',');
      localStorage.setItem('lastProductCodes', productCodes);
      if(this.appsToSearch.length > 0){
        let appIds = this.appsToSearch.map(a => a.id).join(',');
        localStorage.setItem('lastAppIds', appIds);
        this.filteredApps = this.appsToSearch;
      }else{
        localStorage.removeItem('lastAppIds');
        this.filteredApps = this.possibleApps;
      }

      this.requestService.getRequestsByProducts(productCodes).subscribe((data: any) => {
        this.requests = data;
        this.requests.sort((a: any, b: any) => a.product.code < b.product.code ? -1 : 1)
                     .sort((a: any, b: any) => a.site.code < b.site.code ? -1 : 1);
      });
    }else{
      localStorage.removeItem('lastProductCodes');
      this.requests = [];
    }
  }

  getActions(request: any, app: any){
    return request.actions.filter((a: any) => a.app.id === app.id);
  }
}
