import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductFeatureService {

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

  getProductFeatures(productId: number){
    return this.http.get<any>(`${this.env.api}/product-features/${productId}`, this.httpOptions);
  }
  getProductsByFeature(featureId: number){
    return this.http.get<any>(`${this.env.api}/product-features-by-feature/${featureId}`, this.httpOptions);
  }
  submitProductFeatures(productId: number, productFeatures: any[]){
    return this.http.post<any>(`${this.env.api}/product-features/${productId}`, productFeatures, this.httpPostOptions);
  }
  deleteProductFeature(productId: number, featureId: number){
    return this.http.delete<any>(`${this.env.api}/product-features/${productId}/${featureId}`, this.httpOptions);
  }

}
