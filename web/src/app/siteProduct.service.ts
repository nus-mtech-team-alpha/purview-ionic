import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SiteProductService {

  env: any = environment
  httpOptions = {
    headers: new HttpHeaders({ 
      'content-type': 'text/json'
    })
  };
  httpPostOptions = {
    headers: new HttpHeaders({ 
      'content-type': 'application/json'
    })
  };
  
  constructor(private http: HttpClient) { }

  getSiteProducts(siteId: number){
    return this.http.get<any>(`${this.env.api}/site-products/${siteId}`, this.httpOptions);
  }

  getAllSiteProducts(){
    return this.http.get<any>(`${this.env.api}/site-products-all`, this.httpOptions);
  }

  getSitesByProduct(productId: number){
    return this.http.get<any>(`${this.env.api}/sites-by-product/${productId}`, this.httpOptions);
  }
  
  getSiteProductByRequestReferenceId(requestReferenceId: string){
    return this.http.get<any>(`${this.env.api}/site-product-by-request/${requestReferenceId}`, this.httpOptions);
  }

  addSiteProduct(siteProduct: any){
    return this.http.post<any>(`${this.env.api}/site-product-add`, siteProduct, this.httpPostOptions);
  }
}
