import { Component, Input, OnInit } from '@angular/core';
import { ViewCell } from 'angular2-smart-table';

@Component({
  selector: 'app-site-code-renderer',
  templateUrl: './site-code-renderer.component.html'
})
export class SiteCodeRendererComponent implements ViewCell, OnInit {
  data: any;

  iconMap: { [id: string]: string; } = {
    "IPHONE": "cilScreenSmartphone",
    "IPAD": "cilTablet",
    "MAC": "cilLaptop",
    "WATCH": "cilWatch",
    "AUDIO": "cilHeadphones",
    "TV": "cilTv",
    "ACCESSORIES": "cilKeyboard"
  };

  categories: string[] = [];
  isMiSite: boolean = false;

  @Input() value!: string | number;
  @Input() rowData: any;

  ngOnInit() {
    this.data = this.value;
    
    if(this.rowData.productCategories){
      this.categories = this.rowData.productCategories.split(",");
      this.isMiSite = this.categories.includes("SITE_MI");
      this.categories = this.categories.filter((category: string) => category !== "SITE_MI");
    }
  }
}
