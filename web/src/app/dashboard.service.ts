import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

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

  getDashboardData(){
    return this.http.get<any>(`${this.env.api}/dashboard/stats`, this.httpOptions);
  }
  
}
