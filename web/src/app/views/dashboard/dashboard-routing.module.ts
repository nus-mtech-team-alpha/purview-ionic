import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashboardComponent } from './dashboard.component';
import { AuthCheck } from '../../auth-check.service';
import { AuthGuard } from '../../auth-guard.service';

const routes: Routes = [
  {
    path: '',
    component: DashboardComponent,
    canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
    data: {
      role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM',
      title: $localize`Dashboard`
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule {
}
