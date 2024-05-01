import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

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

  getProducts(){
    return this.http.get<any>(`${this.env.api}/products`, this.httpOptions);
  }
  getProductsDto(){
    return this.http.get<any>(`${this.env.api}/products-list-dto`, this.httpOptions);
  }
  getProductsBySiteCategory(siteId: number){
    return this.http.get<any>(`${this.env.api}/products-by-site-category/${siteId}`, this.httpOptions);
  }
  getProduct(id: number){
    return this.http.get<any>(`${this.env.api}/products/${id}`, this.httpOptions);
  }
  getProductSnapshots(id: number){
    return this.http.get<any>(`${this.env.api}/products/snapshots/${id}`, this.httpOptions);
  }
  getProductByCode(code: string){
    return this.http.get<any>(`${this.env.api}/products-by-code/${code}`, this.httpOptions);
  }
  addProduct(product: any){
    return this.http.post<any>(`${this.env.api}/products`, product, this.httpPostOptions);
  }
  updateProduct(product: any){
    return this.http.put<any>(`${this.env.api}/products/${product.id}`, product, this.httpPostOptions);
  }
  updateProductPhase(product: any){
    return this.http.put<any>(`${this.env.api}/products-update-phase/${product.id}`, product, this.httpPostOptions);
  }
  getActiveNpiProductsCount(){
    return this.http.get<any>(`${this.env.api}/products-active-count?status=NPI`, this.httpOptions);
  }
  getActiveMpProductsCount(){
    return this.http.get<any>(`${this.env.api}/products-active-count?status=MP`, this.httpOptions);
  }
}
