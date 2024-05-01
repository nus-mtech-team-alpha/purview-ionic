import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RequestService } from '../../request.service';
import { SiteProductService } from '../../siteProduct.service';
import { SiteAppService } from '../../siteApp.service';

@Component({
  selector: 'app-request-details',
  templateUrl: './request-details.component.html',
  styleUrls: ['./request-details.component.scss']
})
export class RequestDetailsComponent implements OnInit {

  requestRefId: string = "";
  request: any = {};
  siteProduct: any = {};
  siteApps: any[] = [];

  constructor(private requestService: RequestService, 
    private route: ActivatedRoute, 
    private router: Router,
    private siteProductService: SiteProductService,
    private siteAppService: SiteAppService) {
    this.route.params.subscribe(params => {
      this.requestRefId = params['referenceId'];
    });
  }

  ngOnInit(): void {
    this.requestService.getRequestByReferenceId(this.requestRefId).subscribe(
      (data: any) => {
        this.request = data;
        if(this.request.status == 'COMPLETED'){
          this.siteProductService.getSiteProductByRequestReferenceId(this.requestRefId).subscribe((data: any) => {  
            this.siteProduct = data;

            if(this.request.product.status == 'NPI'){
              this.siteAppService.getSiteAppsForNpi(this.request.site.id).subscribe((data: any) => {
                this.siteApps = data.filter((sa: any) => sa.vip);
              });
            }else{
              this.request.environment = 'PRODUCTION';
              this.siteAppService.getSiteAppsEnv(this.request.site.id, 'PRODUCTION').subscribe((data: any) => {
                this.siteApps = data.filter((sa: any) => sa.vip);
              });
            }
            
          });
        }
      },
      (error: any) => {
        this.router.navigateByUrl("/404");
      }
    );
  }
}
