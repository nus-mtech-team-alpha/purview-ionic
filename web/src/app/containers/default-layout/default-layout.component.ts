import { Component, OnInit } from '@angular/core';

import { navItems } from './_nav';
import { AuthService } from '../../auth.service';
import { INavData } from '@coreui/angular';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-dashboard',
  templateUrl: './default-layout.component.html',
})
export class DefaultLayoutComponent implements OnInit {

  public navItems: INavData[] = [];
  public darkMode = true;
  currentUser: any = {};

  public perfectScrollbarConfig = {
    suppressScrollX: true,
  };

  constructor(private authService: AuthService) {
    
  }

  ngOnInit() {
    if(localStorage.getItem('dark-mode-saved-state')) {
      this.darkMode = localStorage.getItem('dark-mode-saved-state') === 'dark-mode-on';
    }

    this.authService.getUser().then(user => {
      this.currentUser.firstName = user?.profile?.given_name;
      this.currentUser.lastName = user?.profile?.family_name;
      this.currentUser.groups = user?.profile?.['groups'];
      this.currentUser.roles = environment.roles.filter((role: any) => this.currentUser.groups.includes(role.groupId));
      this.authService.currentUser = this.currentUser;
      const permittedNavs: any[] = [];
      for (let nav of navItems) {
        if (nav.attributes) {
          if (this.authService.isUserInRole(this.currentUser.roles, nav.attributes['roles'])) {
            permittedNavs.push(nav);
          }
        }
      }
      this.navItems = permittedNavs;
    });

  }
}
