import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthCheck } from '../../auth-check.service';
import { AuthGuard } from '../../auth-guard.service';
import { AppDetailsComponent } from './app-details.component';
import { ManageAppsComponent } from './manage-apps.component';
import { ManageProductsComponent } from './manage-products.component';
import { ManageRequestComponent } from './manage-request.component';
import { ManageSitesComponent } from './manage-sites.component';
import { RequestDetailsComponent } from './request-details.component';
import { ProductDetailsComponent } from './product-details.component';
import { SiteDetailsComponent } from './site-details.component';
import { AdminDashboardComponent } from './admin-dashboard.component';
import { FeatureDetailsComponent } from './feature-details.component';
import { ManageFeaturesComponent } from './manage-features.component';
import { ManageUsersComponent } from './manage-users.component';
import { ManageFeatureRequestComponent } from './manage-feature-request.component';
import { ManageApprovalsComponent } from './manage-approvals.component';
import { ProductReadinessComponent } from './product-readiness.component';

const routes: Routes = [
  {
    path: '',
    canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
    data: {
      role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM',
      title: 'Admin',
    },
    children: [
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard',
      },
      {
        path: 'dashboard',
        component: AdminDashboardComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM,ROLE_APS',
          title: 'Dashboard',
        },
      },
      {
        path: 'site-product',
        component: ManageRequestComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM',
          title: 'Site-Product Support',
        },
      },
      {
        path: 'site-feature',
        component: ManageFeatureRequestComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM',
          title: 'Site-Feature Support',
        },
      },
      {
        path: 'request-details/:referenceId',
        component: RequestDetailsComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM',
          title: 'Request Details',
        },
      },
      {
        path: 'product-support/:siteCode/:productCode',
        component: RequestDetailsComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM',
          title: 'Support Details',
        },
      },
      {
        path: 'feature-support/:siteCode/:featureName',
        component: RequestDetailsComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM',
          title: 'Support Details',
        },
      },
      {
        path: 'sites',
        component: ManageSitesComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM,ROLE_APS',
          title: 'Manage Sites',
        },
      },
      {
        path: 'site-details/:code',
        component: SiteDetailsComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM,ROLE_APS',
          title: 'Site Details',
        },
      },
      {
        path: 'products',
        component: ManageProductsComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM',
          title: 'Manage Products',
        },
      },
      {
        path: 'product-details/:code',
        component: ProductDetailsComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM',
          title: 'Product Details',
        },
      },
      {
        path: 'apps',
        component: ManageAppsComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM,ROLE_APS',
          title: 'Manage Apps',
        },
      },
      {
        path: 'app-details/:name',
        component: AppDetailsComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM,ROLE_APS',
          title: 'App Details',
        },
      },
      {
        path: 'features',
        component: ManageFeaturesComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM',
          title: 'Manage Features',
        },
      },
      {
        path: 'feature-details/:name',
        component: FeatureDetailsComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM',
          title: 'Feature Details',
        },
      },
      {
        path: 'users',
        component: ManageUsersComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE',
          title: 'Manage Users',
        },
      },
      {
        path: 'approvals',
        component: ManageApprovalsComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE',
          title: 'Manage Approvals',
        },
      },
      {
        path: 'product-readiness',
        component: ProductReadinessComponent,
        canActivate: [AuthGuard], //canActivate: [AuthCheck, AuthGuard],
        data: {
          role: 'ROLE_SRE,ROLE_OPS,ROLE_EPM',
          title: 'Product Readiness',
        },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
