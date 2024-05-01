import { Component, OnInit } from '@angular/core';

import { RequestService } from '../../request.service';
@Component({
  selector: 'app-search-request',
  templateUrl: './search-request.component.html'
})
export class SearchRequestComponent implements OnInit {

  searchTerm: string = "";
  requests: any = [];

  constructor(private requestService: RequestService) { 

  }
  ngOnInit(): void { 
    this.requestService.getMyRequestsDto().subscribe((data: any) => {
      this.requests = data;
    });
  }

}
