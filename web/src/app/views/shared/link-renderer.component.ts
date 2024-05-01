import { Component, Input, OnInit } from '@angular/core';
import { Cell, ViewCell } from 'angular2-smart-table';
import { faEye } from '@fortawesome/free-regular-svg-icons';
@Component({
  selector: 'app-link-renderer',
  templateUrl: './link-renderer.component.html'
})
export class LinkRendererComponent implements ViewCell, OnInit {
  data: any;
  faEye = faEye;

  @Input() value!: string | number;
  @Input() rowData: any;

  ngOnInit() {
    this.data = this.value;
  }

}
