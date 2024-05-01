import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  private toastSubject = new Subject<any>();

  getToast() {
    return this.toastSubject.asObservable();
  }

  showToast(type: string = 'info', subject: any = null, message: any = null) {
    this.toastSubject.next({ 
      message: message,
      subject: subject,
      type: type 
    });
  }

}
