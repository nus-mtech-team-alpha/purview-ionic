import { Component } from '@angular/core';
import { Settings } from 'angular2-smart-table';
import { AuthService } from '../../auth.service';

@Component({
  selector: 'app-manage-users',
  templateUrl: './manage-users.component.html'
})
export class ManageUsersComponent {

  settings: Settings = {
    columns: {
      firstName: {
        title: 'GIVEN',
        classHeader: 'text-nowrap',
        sortDirection: 'asc',
        placeholder: 'Filter by given...'
      },
      lastName: {
        title: 'FAMILY',
        placeholder: 'Filter by family...'
      },
      email: {
        title: 'EMAIL',
        placeholder: 'Filter by email...'
      },
      team: {
        title: 'TEAM',
        placeholder: 'Filter by team...'
      },
    },
    actions: false,
    attr: {
      class: 'table table-striped'
    },
    selectedRowIndex: -1
  };

  persons: any = [];

  constructor(private authService: AuthService) { 

  }
  ngOnInit(): void { 
    this.authService.getUsersDto().subscribe((data: any) => {
      this.persons = data;
    });
  }
}
