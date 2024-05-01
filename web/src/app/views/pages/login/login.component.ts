import { Component } from '@angular/core';

import { AuthService } from '../../../auth.service';
import { Router } from '@angular/router';
import jwtDecode, {JwtPayload} from 'jwt-decode';
import { ToastService } from '../../../toast.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  username: string = "";
  password: string = "";
  message: string = "";

  constructor(private authService: AuthService, 
    private router: Router,
    private toastService: ToastService) {
  }

  public login(): void {
    sessionStorage.removeItem("app.token");
  }
  
}
