import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbAlertModule, NgbDatepickerModule, NgbPaginationModule, NgbTypeaheadModule } from '@ng-bootstrap/ng-bootstrap';

import {
  ButtonGroupModule,
  ButtonModule,
  CardModule,
  DropdownModule,
  FormModule,
  GridModule,
  ListGroupModule,
  SharedModule,
  NavModule,
  UtilitiesModule,
  TabsModule,
  ModalModule,
  AlertModule,
  TableModule,
} from '@coreui/angular';

import { IconModule } from '@coreui/icons-angular';

import { RequestRoutingModule } from './request-routing.module';
import { CreateRequestComponent } from './create-request.component';
import { SearchRequestComponent } from './search-request.component';
import { RequestDetailsComponent } from './request-details.component';


@NgModule({
  declarations: [
    CreateRequestComponent,
    SearchRequestComponent,
    RequestDetailsComponent
  ],
  imports: [
    CommonModule,
    RequestRoutingModule,
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
    NgbAlertModule,
    NgbDatepickerModule,
    NgbPaginationModule, 
    NgbTypeaheadModule,
  ]
})
export class RequestModule { }
