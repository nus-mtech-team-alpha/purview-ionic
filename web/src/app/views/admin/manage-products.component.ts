import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../product.service';
import { Settings, IColumnType, Cell } from 'angular2-smart-table';
import { LinkRendererComponent } from '../shared/link-renderer.component';

@Component({
  selector: 'app-manage-products',
  templateUrl: './manage-products.component.html'
})
export class ManageProductsComponent implements OnInit {

  settings: Settings = {
    columns: {
      code: {
        title: 'CODE',
        sortDirection: 'asc',
        placeholder: 'Filter by code...',
        type: IColumnType.Custom,
        valuePrepareFunction: (cellValue: any, rowData: any, cell: Cell) => {
          return {
            routeInfo: [
              '/admin/product-details',
              rowData.code
            ],
            identifier: rowData.code
          };
        },
        renderComponent: LinkRendererComponent,
      },
      category: {
        title: 'CATEGORY',
        placeholder: 'Filter by category...',
      },
      yearStarted: {
        title: 'YEAR',
        placeholder: 'Filter by category...',
      },
      status: {
        title: 'STATUS',
        filter: {
          type: 'list',
          config: {
            selectText: 'Show only...',
            list: ['NPI', 'MP'].map(v =>({value: v, title: v}))
          }
        },
      },
    },
    actions: false,
    attr: {
      class: 'table table-striped'
    },
    selectedRowIndex: -1
  };

  products: any = [];

  constructor(private productService: ProductService) { 

  }
  ngOnInit(): void { 
    this.productService.getProductsDto().subscribe((data: any) => {
      this.products = data;
    });
  }

}
