import { Component, OnInit } from '@angular/core';
import { SiteProductService } from '../../siteProduct.service';
import { DashboardService } from '../../dashboard.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html'
})
export class AdminDashboardComponent implements OnInit {

  activeRequestsCount: number = 0;
  activeSitesCount: number = 0;
  activeNpiProductsCount: number = 0;
  activeMpProductsCount: number = 0;
  productCategories: string[] = ["SITE","IPHONE","IPAD","MAC","WATCH","AUDIO","TV","ACCESSORIES"];
  rows: any[] = [];
  records: Map<string, any[]> = new Map<string, any[]>();
  
  iconMap: { [id: string]: string; } = {
    "IPHONE": "cilScreenSmartphone",
    "IPAD": "cilTablet",
    "MAC": "cilLaptop",
    "WATCH": "cilWatch",
    "AUDIO": "cilHeadphones",
    "TV": "cilTv",
    "ACCESSORIES": "cilKeyboard"
  };

  mobile: boolean = false;

  constructor(private siteProductService: SiteProductService,
    private dashboardService: DashboardService) { }

  ngOnInit(): void {
    if (window.innerWidth <= 768) { // 768px portrait
      this.mobile = true;
    }
    this.dashboardService.getDashboardData().subscribe((data: any) => {
      this.activeRequestsCount = data.activeRequestsCount;
      this.activeSitesCount = data.activeSitesCount;
      this.activeNpiProductsCount = data.npiProductsCount;
      this.activeMpProductsCount = data.mpProductsCount;
    });
    this.siteProductService.getAllSiteProducts().subscribe((data: any) => {
      this.records = new Map<string, any[]>();
      data.forEach((sp: any) => {
        const siteCode = sp.site.code;
        if(!this.records.has(siteCode)){
          this.records.set(siteCode, []);
        }
        const record = this.records.get(siteCode);
        if(record){
          record.push(sp.product);
        }
      });
      this.records.forEach((value: any[], key: string) => {
        this.rows.push({
          "SITE": key,
          "IPHONE": value.filter((product: any) => product.category === "IPHONE"),
          "IPAD": value.filter((product: any) => product.category === "IPAD"),
          "MAC": value.filter((product: any) => product.category === "MAC"),
          "WATCH": value.filter((product: any) => product.category === "WATCH"),
          "AUDIO": value.filter((product: any) => product.category === "AUDIO"),
          "TV": value.filter((product: any) => product.category === "TV"),
          "ACCESSORIES": value.filter((product: any) => product.category === "ACCESSORIES")
        });
      });
    });
  }
}
