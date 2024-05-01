import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SiteAppService {

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

  getSiteApps(siteId: number){
    return this.http.get<any>(`${this.env.api}/site-apps/${siteId}`, this.httpOptions);
  }
  getSiteAppsForNpi(siteId: number){
    return this.http.get<any>(`${this.env.api}/site-apps-for-npi/${siteId}`, this.httpOptions);
  }
  getSiteAppsEnv(siteId: number, env: string){
    return this.http.get<any>(`${this.env.api}/site-apps-env/${siteId}?env=${env}`, this.httpOptions);
  }
  submitSiteApps(siteId: number, siteApps: any[]){
    return this.http.post<any>(`${this.env.api}/site-apps/${siteId}`, siteApps, this.httpPostOptions);
  }
}
