import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {AuthService} from './auth.service';
import { Observable } from 'rxjs';
import { ToastService } from './toast.service';
import { environment } from '../environments/environment';

@Injectable({
    providedIn: 'root',
})
export class AuthGuard implements CanActivate {

    private currentUser: any = {};

    constructor(private authService: AuthService, private router: Router, private toastService: ToastService) {
    }

    public canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> | boolean {
        return this.checkAuthorization(next);
    }

    checkAuthorization(route: ActivatedRouteSnapshot) {
        return this.authService.getUser().then(user => {
            if (user) {
                console.log('User Logged In');
                this.currentUser.groups = user?.profile?.['groups'];
                this.currentUser.roles = environment.roles.filter((role: any) => this.currentUser.groups.includes(role.groupId));
                return this.authService.isUserInRole(this.currentUser.roles, route.routeConfig?.data?.['role']);
            } else {
                console.log('User Not Logged In');
                this.authService.login();
                return false;
            }
        }).catch(err => {
            console.error(err);
            this.toastService.showToast("danger", "Unauthorized", "Action is not permitted");
            return false;
        });
    }

}
