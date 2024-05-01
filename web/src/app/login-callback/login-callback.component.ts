import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login-callback',
  templateUrl: './login-callback.component.html'
})
export class LoginCallbackComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) { }

  private currentUser: any = {};

  ngOnInit(): void {
    this.authService.userManager.signinCallback().finally(() => {
      this.router.navigate(['']);
    });
  }

}
