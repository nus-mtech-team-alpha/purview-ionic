import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class FeatureAppService {

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

  getFeatureApps(featureId: number){
    return this.http.get<any>(`${this.env.api}/feature-apps/${featureId}`, this.httpOptions);
  }
  submitFeatureApps(featureId: number, featureApps: any[]){
    return this.http.post<any>(`${this.env.api}/feature-apps/${featureId}`, featureApps, this.httpPostOptions);
  }
  updateFeatureAppsRadars(featureId: number, featureApps: any[]){
    return this.http.put<any>(`${this.env.api}/feature-apps-radars/${featureId}`, featureApps, this.httpPostOptions);
  }

}
