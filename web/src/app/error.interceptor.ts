import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {catchError, Observable, throwError, EMPTY, from, switchMap} from 'rxjs';
import {Router} from '@angular/router';
import { ToastService } from './toast.service';
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})
export class ErrorInterceptor implements HttpInterceptor {

    constructor(private router: Router, private toastService: ToastService, private authService: AuthService) {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(
            catchError((error: HttpErrorResponse) => {
              if (error.status === 401) {
                this.authService.renewToken();
              }
              return throwError(error);
            })
          );
    }

}
