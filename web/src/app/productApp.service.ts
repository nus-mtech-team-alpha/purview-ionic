import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ProductAppService {

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

  getProductApps(productId: number){
    return this.http.get<any>(`${this.env.api}/product-apps/${productId}`, this.httpOptions);
  }
  submitProductApps(productId: number, productApps: any[]){
    return this.http.post<any>(`${this.env.api}/product-apps/${productId}`, productApps, this.httpPostOptions);
  }

  updateProductAppsRadars(productId: number, productApps: any[]){
    return this.http.put<any>(`${this.env.api}/product-apps-radars/${productId}`, productApps, this.httpPostOptions);
  }

}
