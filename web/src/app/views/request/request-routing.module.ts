import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CreateRequestComponent } from './create-request.component';
import { RequestDetailsComponent } from './request-details.component';
import { SearchRequestComponent } from './search-request.component';
import { AuthCheck } from '../../auth-check.service';
import { AuthGuard } from '../../auth-guard.service';

const routes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
    data: {
      role: 'ROLE_OPM',
      title: 'Requests',
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'search',
      },
      {
        path: 'create',
        component: CreateRequestComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_OPM',
          title: 'Create Request',
        },
      },
      {
        path: 'search',
        component: SearchRequestComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_OPM',
          title: 'Search Request',
        },
      },
      {
        path: 'details/:referenceId',
        component: RequestDetailsComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_OPM',
          title: 'Request Details',
        },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RequestRoutingModule { }
