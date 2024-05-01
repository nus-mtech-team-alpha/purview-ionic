import { Component, Input } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { ClassToggleService, HeaderComponent } from '@coreui/angular';
import { AuthService } from '../../../auth.service';
import { DefaultLayoutComponent } from '../default-layout.component';
import { faMoon, faSun } from '@fortawesome/free-regular-svg-icons';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-default-header',
  templateUrl: './default-header.component.html',
})
export class DefaultHeaderComponent extends HeaderComponent {

  @Input() sidebarId: string = "sidebar";

  public newMessages = new Array(4)
  public newTasks = new Array(5)
  public newNotifications = new Array(5)

  currentRoles: string = "";
  currentUser: any = {};
  darkMode = true;

  faMoon = faMoon;
  faSun = faSun;

  constructor(private classToggler: ClassToggleService,
    private authService: AuthService, 
    private router: Router,
    private defaultLayoutComponent: DefaultLayoutComponent) {
    super();
    this.darkMode = this.defaultLayoutComponent.darkMode;
    if(localStorage.getItem('dark-mode-saved-state')) {
      this.darkMode = localStorage.getItem('dark-mode-saved-state') === 'dark-mode-on';
    }
    // this.authService.getCurrentUser().subscribe((data: any) => {
    //   this.currentUser = data;
    // });
    this.authService.getUser().then(user => {
      this.currentUser.firstName = user?.profile?.given_name;
      this.currentUser.lastName = user?.profile?.family_name;
      this.currentUser.groups = user?.profile?.['groups'];
      console.log('User Groups: ', this.currentUser.groups);
      this.currentUser.roles = environment.roles.filter((role: any) => this.currentUser.groups.includes(role.groupId));
    });
  }

  setDarkMode() {
    this.darkMode = true;
    this.defaultLayoutComponent.darkMode = true;
    localStorage.setItem('dark-mode-saved-state', 'dark-mode-on');
  }
  setLightMode() {
    this.darkMode = false;
    this.defaultLayoutComponent.darkMode = false;
    localStorage.setItem('dark-mode-saved-state', 'dark-mode-off');
  }
}
