import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SiteService {

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

  getSites(){
    return this.http.get<any>(`${this.env.api}/sites`, this.httpOptions);
  }
  getSitesDto(){
    return this.http.get<any>(`${this.env.api}/sites-list-dto`, this.httpOptions);
  }
  getSitesByCategory(category: string){
    return this.http.get<any>(`${this.env.api}/sites-by-category?category=${category}`, this.httpOptions);
  }
  getSite(id: number){
    return this.http.get<any>(`${this.env.api}/sites/${id}`, this.httpOptions);
  }
  getSiteByCode(code: string){
    return this.http.get<any>(`${this.env.api}/sites-by-code/${code}`, this.httpOptions);
  }
  updateSite(site: any){
    return this.http.put<any>(`${this.env.api}/sites/${site.id}`, site, this.httpPostOptions);
  }
  getActiveSitesCount(){
    return this.http.get<any>(`${this.env.api}/sites-active-count`, this.httpOptions);
  }
  addSite(site: any){
    return this.http.post<any>(`${this.env.api}/sites`, site, this.httpPostOptions);
  }
}
