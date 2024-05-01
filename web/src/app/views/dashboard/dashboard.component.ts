import { Component, OnInit } from '@angular/core';
import { RequestService } from '../../request.service';

@Component({
  templateUrl: 'dashboard.component.html',
  styleUrls: ['dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  openRequestsCount: number = 0;
  inprogressRequestsCount: number = 0;
  rejectedRequestsCount: number = 0;
  completedRequestsCount: number = 0;
  
  constructor(private requestService: RequestService) { }

  ngOnInit(): void {
    this.requestService.getMyRequestsCountByStatus("OPEN").subscribe((data: any) => this.openRequestsCount = data);
    this.requestService.getMyRequestsCountByStatus("IN_PROGRESS").subscribe((data: any) => this.inprogressRequestsCount = data);
    this.requestService.getMyRequestsCountByStatus("REJECTED").subscribe((data: any) => this.rejectedRequestsCount = data);
    this.requestService.getMyRequestsCountByStatus("COMPLETED").subscribe((data: any) => this.completedRequestsCount = data);
  }

}