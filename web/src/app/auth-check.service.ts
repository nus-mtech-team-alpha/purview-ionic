import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root',
})
export class AuthCheck implements CanActivate {

    constructor(private authService: AuthService) {
    }

    public canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        // this.authService.getCurrentUser().subscribe(data => {
        //     if(data){
        //         this.authService.currentUser = data;
        //     }else{
        //         console.error("Null user, refreshing...");
        //         // window.location.reload();
        //     }
        // },(error) => {
        //     console.error("Error getting user, refreshing...");
        //     // window.location.reload();
        // });
        return true;   
    }
}
