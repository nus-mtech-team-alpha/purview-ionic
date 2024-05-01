import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SiteFeatureService {

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

  getSiteFeatures(siteId: number){
    return this.http.get<any>(`${this.env.api}/site-features/${siteId}`, this.httpOptions);
  }

  getAllSiteFeatures(){
    return this.http.get<any>(`${this.env.api}/site-features-all`, this.httpOptions);
  }

  getSitesByFeature(featureId: number){
    return this.http.get<any>(`${this.env.api}/sites-by-feature/${featureId}`, this.httpOptions);
  }
  
  getSiteFeatureByRequestReferenceId(requestReferenceId: string){
    return this.http.get<any>(`${this.env.api}/site-feature-by-request/${requestReferenceId}`, this.httpOptions);
  }

  addSiteFeature(siteFeature: any){
    return this.http.post<any>(`${this.env.api}/site-feature-add`, siteFeature, this.httpPostOptions);
  }
}
