import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ManageRequestComponent } from './manage-request.component';
import { RequestDetailsComponent } from './request-details.component';
import { AdminRoutingModule } from './admin-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { CardModule, GridModule, UtilitiesModule, NavModule, TabsModule, ButtonGroupModule, ButtonModule, DropdownModule, FormModule, ListGroupModule, SharedModule, ModalModule, AlertModule, TableModule, AccordionModule, BadgeModule, PopoverModule, TooltipModule } from '@coreui/angular';
import { IconModule } from '@coreui/icons-angular';
import { NgbAlertModule, NgbDatepickerModule, NgbPaginationModule, NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';
import { ManageSitesComponent } from './manage-sites.component';
import { ManageProductsComponent } from './manage-products.component';
import { ManageAppsComponent } from './manage-apps.component';
import { AppDetailsComponent } from './app-details.component';
import { SiteDetailsComponent } from './site-details.component';
import { ProductDetailsComponent } from './product-details.component';
import { TagInputModule } from 'ngx-chips';
import { AdminDashboardComponent } from './admin-dashboard.component';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { ManageFeaturesComponent } from './manage-features.component';
import { FeatureDetailsComponent } from './feature-details.component';
import { Angular2SmartTableModule } from 'angular2-smart-table';
import { ManageUsersComponent } from './manage-users.component';
import { ManageFeatureRequestComponent } from './manage-feature-request.component';
import { ManageApprovalsComponent } from './manage-approvals.component';
import { ReplacePipe } from '../../replace.pipe';
import { ProductReadinessComponent } from './product-readiness.component';

@NgModule({
  declarations: [
    ManageRequestComponent,
    RequestDetailsComponent,
    ManageSitesComponent,
    ManageProductsComponent,
    ManageAppsComponent,
    AppDetailsComponent,
    SiteDetailsComponent,
    ProductDetailsComponent,
    AdminDashboardComponent,
    ManageFeaturesComponent,
    FeatureDetailsComponent,
    ManageUsersComponent,
    ManageFeatureRequestComponent,
    ManageApprovalsComponent,
    ReplacePipe,
    ProductReadinessComponent,
  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    CardModule,
    GridModule,
    UtilitiesModule,
    IconModule,
    NavModule,
    TabsModule,
    ButtonGroupModule,
    ButtonModule,
    DropdownModule,
    FormModule,
    ListGroupModule,
    SharedModule,
    ModalModule,
    AlertModule,
    TableModule,
    AccordionModule,
    BadgeModule,
    PopoverModule,
    NgbAlertModule,
    NgbDatepickerModule,
    NgbPaginationModule, 
    NgbTypeaheadModule,
    TagInputModule,
    FontAwesomeModule,
    Angular2SmartTableModule,
    TooltipModule,
  ]
})
export class AdminModule { }
