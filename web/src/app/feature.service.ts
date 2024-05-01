import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FeatureService {

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

  getFeatures(){
    return this.http.get<any>(`${this.env.api}/features`, this.httpOptions);
  }
  getFeaturesDto(){
    return this.http.get<any>(`${this.env.api}/features-list-dto`, this.httpOptions);
  }
  getFeature(id: number){
    return this.http.get<any>(`${this.env.api}/features/${id}`, this.httpOptions);
  }
  getFeatureByName(name: string){
    return this.http.get<any>(`${this.env.api}/features-by-name/${name}`, this.httpOptions);
  }
  addFeature(feature: any){
    return this.http.post<any>(`${this.env.api}/features`, feature, this.httpPostOptions);
  }
  updateFeature(feature: any){
    return this.http.put<any>(`${this.env.api}/features/${feature.id}`, feature, this.httpPostOptions);
  }

}
