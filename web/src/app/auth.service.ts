import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { User, UserManager } from 'oidc-client-ts';

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    env: any = environment;
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
    
    // public currentUser: any;
    userManager: UserManager;
    public currentUser: any = {};

    constructor(private http: HttpClient) {
      const settings = {
        authority: environment.oidc.authority,
        client_id: environment.oidc.clientId,
        redirect_uri: environment.oidc.redirectUri,
        response_type: environment.oidc.responseType,
        scope: environment.oidc.scope,
        post_logout_redirect_uri: environment.oidc.postLogoutRedirectUri,
        automaticSilentRenew: environment.oidc.automaticSilentRenew,
        silent_redirect_uri: environment.oidc.silentRenewUrl
      };
  
      this.userManager = new UserManager(settings);
    }

    getCurrentUser(){
        return this.http.get<any>(`${this.env.api}/persons/current`, this.httpOptions);
    }

    getUsersByTeam(team: string){
        return this.http.get<any>(`${this.env.api}/persons-by-team/${team}`, this.httpOptions);
    }

    isUserInRole(userRoles: any[], requiredRoles: string): boolean {
      return userRoles.map((r: any) => r.code).some((role: any)=> requiredRoles.split(',').includes(role));
    }

    getUsersDto(){
      return this.http.get<any>(`${this.env.api}/persons-list-dto`, this.httpOptions);
    }

    getUser(): Promise<User | null> {
      return this.userManager.getUser();
    }
  
    login(): Promise<void> {
      console.log('Attempting to authenticate...');
      return this.userManager.signinRedirect();
    }
  
    renewToken(): Promise<User | null> {
      return this.userManager.signinSilent();
    }
  
    logout(): Promise<void> {
      return this.userManager.signoutRedirect();
    }
  
    getAccessToken(): Promise<string | null> {
      return this.userManager.getUser().then(user => {
        return user?.access_token || null;
      });
    }
}
