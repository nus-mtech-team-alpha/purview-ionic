import { Component } from '@angular/core';
import { FeatureService } from '../../feature.service';
import { Settings, IColumnType, Cell } from 'angular2-smart-table';
import { LinkRendererComponent } from '../shared/link-renderer.component';

@Component({
  selector: 'app-manage-features',
  templateUrl: './manage-features.component.html'
})
export class ManageFeaturesComponent {

  settings: Settings = {
    columns: {
      name: {
        title: 'NAME',
        sortDirection: 'asc',
        placeholder: 'Filter by name...',
        type: IColumnType.Custom,
        valuePrepareFunction: (cellValue: any, rowData: any, cell: Cell) => {
          return {
            routeInfo: [
              '/admin/feature-details',
              rowData.name
            ],
            identifier: rowData.name
          };
        },
        renderComponent: LinkRendererComponent,
      },
      rolloutPhase: {
        title: 'ROLLOUT PHASE',
        classHeader: 'text-nowrap',
        filter: {
          type: 'list',
          config: {
            selectText: 'Show only...',
            list: ['NEW','DECK','STAGING','PRODUCTION'].map(v =>({value: v, title: v}))
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

  features: any = [];

  constructor(private featureService: FeatureService) { 

  }
  ngOnInit(): void { 
    this.featureService.getFeaturesDto().subscribe((data: any) => {
      this.features = data;
    });
  }
}
