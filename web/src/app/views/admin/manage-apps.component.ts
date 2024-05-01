import { Component, OnInit } from '@angular/core';

import { AppsService } from '../../apps.service';
import { Cell, IColumnType, Settings } from 'angular2-smart-table';
import { LinkRendererComponent } from '../shared/link-renderer.component';

@Component({
  selector: 'app-manage-apps',
  templateUrl: './manage-apps.component.html'
})
export class ManageAppsComponent implements OnInit {

  settings: Settings = {
    columns: {
      internalName: {
        title: 'INTERNAL',
        classHeader: 'text-nowrap',
        sortDirection: 'asc',
        placeholder: 'Filter by internal name...',
        type: IColumnType.Custom,
        valuePrepareFunction: (cellValue: any, rowData: any, cell: Cell) => {
          return {
            routeInfo: [
              '/admin/app-details',
              rowData.internalName
            ],
            identifier: rowData.internalName
          };
        },
        renderComponent: LinkRendererComponent,
      },
      externalName: {
        title: 'EXTERNAL',
        placeholder: 'Filter by external name...'
      },
      sreDri: {
        title: 'SRE DRI',
        placeholder: 'Filter by SRE name...'
      },
    },
    actions: false,
    attr: {
      class: 'table table-striped'
    },
    selectedRowIndex: -1
  };

  apps: any = [];

  constructor(private appsService: AppsService) { 

  }
  ngOnInit(): void { 
    this.appsService.getAppsDto().subscribe((data: any) => {
      this.apps = data;
      this.apps.forEach((app: any) => {
        if(app.sre){
          app.sreDri = `${app.sre.firstName} ${app.sre.lastName}`;
        }
      });
    });
  }

}