import { Component, OnInit } from '@angular/core';
import { Settings, IColumnType, Cell, Row } from 'angular2-smart-table';
import { RequestService } from 'src/app/request.service';
import { LinkRendererComponent } from '../shared/link-renderer.component';

@Component({
  selector: 'app-manage-request',
  templateUrl: './manage-request.component.html'
})
export class ManageRequestComponent implements OnInit {

  settings: Settings = {
    columns: {
      referenceId: {
        title: '',
        classHeader: 'text-nowrap',
        filter: false,
        type: IColumnType.Custom,
        valuePrepareFunction: (cellValue: any, rowData: any, cell: Cell) => {
          return {
            routeInfo: rowData.status === 'CANCELLED' || rowData.status === 'REJECTED' ? 
            [
              '/admin/request-details',
              rowData.referenceId
            ]
            :
            [
              '/admin/product-support',
              rowData.siteCode,
              rowData.productCode,
            ]
          };
        },
        renderComponent: LinkRendererComponent,
      },
      siteCode: {
        title: 'SITE',
        placeholder: 'Filter by site...',
      },
      productCode: {
        title: 'PRODUCT',
        placeholder: 'Filter by product...',
      },
      needByDate: {
        title: 'NEED-BY-DATE',
        placeholder: 'Filter by date...',
      },
      status: {
        title: 'STATUS',
        filter: {
          type: 'list',
          config: {
            selectText: 'Show only...',
            list: ['OPEN','PREPARE','REVIEW','IN_PROGRESS','REJECTED','CANCELLED','COMPLETED'].map(v =>({value: v, title: v}))
          }
        },
      },
    },
    actions: false,
    attr: {
      class: 'table table-striped'
    },
    selectedRowIndex: -1,
    rowClassFunction: (row: Row) => {
      if (row.getData().status === 'CANCELLED' || row.getData().status === 'REJECTED'){
        return 'blurred-row';
      }else if(row.getData().status !== 'COMPLETED'){
        const current = new Date();
        const seventhDayFromToday = new Date(current.setDate(current.getDate() + 7));
        const needBy = new Date(row.getData().needByDate);
        return seventhDayFromToday > needBy ? 'near-date' : '';
      }
      return '';
    }
  };

  requests: any = [];

  constructor(private requestService: RequestService) { 

  }
  ngOnInit(): void { 
    this.requestService.getAllProductRequestsDto().subscribe((data: any) => {
      this.requests = data;
    });
  }

}
