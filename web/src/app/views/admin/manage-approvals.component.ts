import { Component, OnInit } from '@angular/core';
import { RequestService } from '../../request.service';
import { ToastService } from '../../toast.service';
import { faEye } from '@fortawesome/free-regular-svg-icons';

@Component({
  selector: 'app-manage-approvals',
  templateUrl: './manage-approvals.component.html'
})
export class ManageApprovalsComponent implements OnInit {

  actions: any = [];
  faEye = faEye;

  constructor(private requestService: RequestService,
    private toastService: ToastService) { }

  ngOnInit() {
    this.requestService.getAllPendingActions().subscribe((data: any) => {
      this.actions = data;
    });
  }

  approveAction(action: any){

    if(action.toIgnore){
      this.toastService.showToast("danger", "Unable to approve", "Action is set to be ignored");
      return;
    }

    action.verified = true;
    this.requestService.updateActionVerification(action).subscribe(
      (data) => {
        this.toastService.showToast("success", "Success", "Action verified successfully");
        this.ngOnInit();
      },(error) => {
        this.toastService.showToast("danger", "Unauthorized", "You are not the SRE DRI for this app");
        this.ngOnInit();
      }
    );
  }

  setActionIgnore(action: any){
    action.toIgnore = !action.toIgnore;
    this.requestService.updateActionIgnore(action).subscribe(
      (data) => {
        this.toastService.showToast("success", "Success", "Action updated successfully");
      },(error) => {
        this.toastService.showToast("danger", "Unauthorized", "You are not the SRE DRI for this app");
        this.ngOnInit();
      }
    );
  }

}
