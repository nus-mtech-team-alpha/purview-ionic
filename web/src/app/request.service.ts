import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RequestService {

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

  submitRequest(request: any){
    return this.http.post<any>(`${this.env.api}/requests`, request, this.httpPostOptions);
  }

  getAllRequests(){
    return this.http.get<any>(`${this.env.api}/requests`, this.httpOptions);
  }

  getMyRequests(){
    return this.http.get<any>(`${this.env.api}/requests/mine`, this.httpOptions);
  }

  getAllProductRequestsDto(){
    return this.http.get<any>(`${this.env.api}/requests-list-dto/products`, this.httpOptions);
  }

  getAllFeatureRequestsDto(){
    return this.http.get<any>(`${this.env.api}/requests-list-dto/features`, this.httpOptions);
  }

  getMyRequestsDto(){
    return this.http.get<any>(`${this.env.api}/requests-list-dto/mine`, this.httpOptions);
  }

  getRequestByReferenceId(referenceId: string){
    return this.http.get<any>(`${this.env.api}/requests/ref/${referenceId}`, this.httpOptions);
  }

  getProductRequest(siteCode: string, productCode: string){
    return this.http.get<any>(`${this.env.api}/request-by-details?siteCode=${siteCode}&productCode=${productCode}`, this.httpOptions);
  }

  getFeatureRequest(siteCode: string, featureName: string){
    return this.http.get<any>(`${this.env.api}/request-by-details?siteCode=${siteCode}&featureName=${featureName}`, this.httpOptions);
  }

  updateRequestActions(request: any){
    return this.http.put<any>(`${this.env.api}/requests-update-actions/${request.id}`, request, this.httpPostOptions);
  }

  updateActionVerification(action: any){
    return this.http.put<any>(`${this.env.api}/requests-update-action-verification/${action.id}`, action, this.httpPostOptions);
  }

  updateActionIgnore(action: any){
    return this.http.put<any>(`${this.env.api}/requests-update-action-ignore/${action.id}`, action, this.httpPostOptions);
  }

  getAllRequestsCountByStatus(status: string){
    return this.http.get<any>(`${this.env.api}/requests-status-count-all?status=${status}`, this.httpOptions);
  }

  getMyRequestsCountByStatus(status: string){
    return this.http.get<any>(`${this.env.api}/requests-status-count-mine?status=${status}`, this.httpOptions);
  }

  cancelProductRequest(siteCode: any, productCode: any){
    return this.http.put<any>(`${this.env.api}/requests-cancel?siteCode=${siteCode}&productCode=${productCode}`, this.httpPostOptions);
  }

  cancelFeatureRequest(siteCode: any, featureName: any){
    return this.http.put<any>(`${this.env.api}/requests-cancel?siteCode=${siteCode}&featureName=${featureName}`, this.httpPostOptions);
  }

  getAllPendingActions(){
    return this.http.get<any>(`${this.env.api}/requests-pending-actions`, this.httpOptions);
  }

  getRequestsByProducts(productCodes: string){
    return this.http.get<any>(`${this.env.api}/requests-by-products?productCodes=${productCodes}`, this.httpOptions);
  }

  getProductRequestDto(siteCode: string, productCode: string){
    return this.http.get<any>(`${this.env.api}/request-by-details-dto?siteCode=${siteCode}&productCode=${productCode}`, this.httpOptions);
  }

  getFeatureRequestDto(siteCode: string, featureName: string){
    return this.http.get<any>(`${this.env.api}/request-by-details-dto?siteCode=${siteCode}&featureName=${featureName}`, this.httpOptions);
  }

  getRequestDtoByReferenceId(referenceId: string){
    return this.http.get<any>(`${this.env.api}/requests-dto/ref/${referenceId}`, this.httpOptions);
  }
  
}
