import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DefaultLayoutComponent } from './containers';
import { LoginComponent } from './views/pages/login/login.component';
import { Page404Component } from './views/pages/page404/page404.component';
import { Page500Component } from './views/pages/page500/page500.component';
import { Page403Component } from './views/pages/page403/page403.component';
import { RegisterComponent } from './views/pages/register/register.component';
import { LoginCallbackComponent } from './login-callback/login-callback.component';

const routes: Routes = [
  {
    path: 'login-callback',
    component: LoginCallbackComponent
  },
  {
    path: '',
    redirectTo: 'admin/dashboard',
    pathMatch: 'full'
  },
  {
    path: '',
    component: DefaultLayoutComponent,
    data: {
      title: 'Home'
    },
    children: [
      {
        path: 'dashboard',
        loadChildren: () =>
          import('./views/dashboard/dashboard.module').then((m) => m.DashboardModule)
      },
      {
        path: 'request',
        loadChildren: () =>
          import('./views/request/request.module').then((m) => m.RequestModule)
      },
      {
        path: 'admin',
        loadChildren: () =>
          import('./views/admin/admin.module').then((m) => m.AdminModule)
      },
      {
        path: 'pages',
        loadChildren: () =>
          import('./views/pages/pages.module').then((m) => m.PagesModule)
      },
    ]
  },
  {
    path: '404',
    component: Page404Component,
    data: {
      title: 'Page 404'
    }
  },
  {
    path: '500',
    component: Page500Component,
    data: {
      title: 'Page 500'
    }
  },
  {
    path: '403',
    component: Page403Component,
    data: {
      title: 'Page 403'
    }
  },
  {
    path: 'login',
    component: LoginComponent,
    data: {
      title: 'Login Page'
    }
  },
  {
    path: 'register',
    component: RegisterComponent,
    data: {
      title: 'Register Page'
    }
  },
  {path: '**', redirectTo: 'dashboard'}
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      scrollPositionRestoration: 'top',
      anchorScrolling: 'enabled',
      initialNavigation: 'enabledBlocking'
      // relativeLinkResolution: 'legacy'
    })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
