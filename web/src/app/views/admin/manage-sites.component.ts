import { Component, OnInit } from '@angular/core';
import { SiteService } from '../../site.service';
import { Cell, IColumnType, Settings } from 'angular2-smart-table';
import { SiteCodeRendererComponent } from '../shared/site-code-renderer.component';

@Component({
  selector: 'app-manage-sites',
  templateUrl: './manage-sites.component.html'
})
export class ManageSitesComponent implements OnInit {

  settings: Settings = {
    columns: {
      codeAndCategories: {
        title: 'CODE',
        sortDirection: 'asc',
        placeholder: 'Filter by code or category...',
        classHeader: 'text-nowrap',
        classContent: 'text-nowrap',
        sanitizer: {
          bypassHtml: true
        },
        type: IColumnType.Custom,
        valuePrepareFunction: (cellValue: any, rowData: any, cell: Cell) => {
          return {
            link: '/admin/site-details',
            identifier: rowData.code
          };
        },
        renderComponent: SiteCodeRendererComponent,
        filterFunction(cell?: any, search?: string): boolean {
          if(search){
            search = search.toUpperCase() === 'MI' ? 'SITE_MI' : search;
            if(cell.toLowerCase().indexOf(search.toLowerCase()) > -1){
              return true;
            }
            return false;
          }
          return true;
        }
      },
      company: {
        title: 'COMPANY',
        placeholder: 'Filter by company...'
      },
      location: {
        title: 'LOCATION',
        placeholder: 'Filter by location...',
        classContent: 'text-nowrap'
      },
    },
    actions: false,
    attr: {
      class: 'table table-striped'
    },
    selectedRowIndex: -1
  };

  sites: any = [];

  constructor(private siteService: SiteService) { 

  }
  ngOnInit(): void { 
    this.siteService.getSitesDto().subscribe((data: any) => {
      this.sites = data;
      this.sites.forEach((site: any) => {
        if(site.category === 'MI'){
          site.productCategories = 'SITE_MI';
        }
        site.location = `${site.city}, ${site.country}`;
        site.codeAndCategories = `${site.code} ${site.productCategories}`;
      });
    });
  }

}

