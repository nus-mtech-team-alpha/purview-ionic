import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AppsService {

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

  getApps(){
    return this.http.get<any>(`${this.env.api}/apps`, this.httpOptions);
  }
  getAppsDto(){
    return this.http.get<any>(`${this.env.api}/apps-list-dto`, this.httpOptions);
  }
  getAppsDtoBySre(){
    return this.http.get<any>(`${this.env.api}/apps-list-dto-by-sre`, this.httpOptions);
  }
  getConfigTypes(){
    return this.http.get<any>(`${this.env.api}/config-types`, this.httpOptions);
  }
  getApp(id: number){
    return this.http.get<any>(`${this.env.api}/apps/${id}`, this.httpOptions);
  }

  getAppByName(name: string){
    return this.http.get<any>(`${this.env.api}/apps-by-name/${name}`, this.httpOptions);
  }

  updateApp(app: any){
    return this.http.put<any>(`${this.env.api}/apps/${app.id}`, app, this.httpPostOptions);
  }

  getActiveAppsCount(){
    return this.http.get<any>(`${this.env.api}/apps-active-count`, this.httpOptions);
  }
  addApp(app: any){
    return this.http.post<any>(`${this.env.api}/apps`, app, this.httpPostOptions);
  }
  getAppConfigLatestJmetAnsible(appConfig: any){
    return this.http.get<any>(`${this.env.api}/apps/get-config/latest-jmet-ansible-main-publish-version?appConfigId=${appConfig.id}`, this.httpOptions);
  }
}
