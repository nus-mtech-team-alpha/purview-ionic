import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';

import { IconSetService } from '@coreui/icons-angular';
import { iconSubset } from './icons/icon-subset';
import { Title } from '@angular/platform-browser';

import  { ToastService } from './toast.service';
import { ToasterPlacement } from '@coreui/angular';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  title = 'JMET | Purview';

  placement = ToasterPlacement.TopCenter;
  toasts: any[] = [];

  constructor(
    private router: Router,
    private titleService: Title,
    private iconSetService: IconSetService,
    private toastService: ToastService,
  ) {
    titleService.setTitle(this.title);
    // iconSet singleton
    iconSetService.icons = { ...iconSubset };
    this.toastService.getToast().subscribe(data => {
      this.toasts.push(data);
    });
  }

  ngOnInit(): void {
    this.router.events.subscribe((evt) => {
      if (!(evt instanceof NavigationEnd)) {
        return;
      }
    });
  }
}
