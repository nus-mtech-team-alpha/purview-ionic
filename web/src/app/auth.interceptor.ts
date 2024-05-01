import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {catchError, Observable, throwError, EMPTY, from, switchMap} from 'rxjs';
import {Router} from '@angular/router';
import { ToastService } from './toast.service';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})
export class AuthInterceptor implements HttpInterceptor {

    constructor(private router: Router, private toastService: ToastService, private authService: AuthService) {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return from(this.authService.getAccessToken()).pipe(
            switchMap(accessToken => {
                if (accessToken) {
                    request = request.clone({
                        setHeaders: {
                            Authorization: `Bearer ${accessToken}`
                        }
                    });
                }
                return next.handle(request);
            }),
            catchError((error: HttpErrorResponse) => this.handleErrorRes(error))
        );
    }

    private handleErrorRes(error: HttpErrorResponse): Observable<never> {
        if (error.status === 403) {
            this.toastService.showToast("danger", "Unauthorized", "Action is not permitted");
            return EMPTY;
        }
        return throwError(() => error);
    }

}
