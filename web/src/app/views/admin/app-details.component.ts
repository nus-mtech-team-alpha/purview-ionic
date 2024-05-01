import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AppsService } from '../../apps.service';
import { AuthService } from '../../auth.service';
import { ToastService } from '../../toast.service';
import { faInfoCircle, faTrashCan } from '@fortawesome/free-solid-svg-icons';
import { Clipboard } from '@angular/cdk/clipboard';

@Component({
  selector: 'app-app-details',
  templateUrl: './app-details.component.html',
  styleUrls: ['./app-details.component.scss']
})
export class AppDetailsComponent implements OnInit {

  appName: string = "";
  app: any = {};
  epms: any[] = [];
  devs: any[] = [];
  sres: any[] = [];
  configToAdd: any = {};
  customConfigToAdd: string = "";
  configTypes: any[] = [];
  faTrashCan = faTrashCan;
  possibleBackupSres: any[] = [];
  faInfoCircle = faInfoCircle;

  constructor(private appsService: AppsService, 
    private route: ActivatedRoute,
    private authService: AuthService,
    private toastService: ToastService,
    private clipboard: Clipboard) {
    this.route.params.subscribe(params => {
      this.appName = params['name'];
    });
  }

  ngOnInit(): void {
    if(this.appName != ""){
      this.appsService.getAppByName(this.appName).subscribe(data => { 
        this.app = data;
        if(this.app.backupSres){
          this.app.backupSres.forEach((sre: any) => {
            sre.value = sre.id;
            sre.display = `${sre.firstName} ${sre.lastName}`;
          });
        }
        this.authService.getUsersByTeam("EPM").subscribe(data => { 
          this.epms = data;
          if(this.app.epm){
            this.app.epm = this.epms.filter((e: any) => e.id == this.app.epm.id)[0];
          }
        });
        this.authService.getUsersByTeam("DEV").subscribe(data => {
          this.devs = data;
          if(this.app.dev){
            this.app.dev = this.devs.filter((d: any) => d.id == this.app.dev.id)[0];
          }        
        });
        this.authService.getUsersByTeam("SRE").subscribe(data => {
          this.sres = data;
          this.possibleBackupSres = data;
          if(this.app.sre){
            this.app.sre = this.sres.filter((s: any) => s.id == this.app.sre.id)[0];
            this.possibleBackupSres = this.possibleBackupSres.filter((s: any) => s.id != this.app.sre.id);
          }
          this.possibleBackupSres.forEach((sre: any) => {
            sre.value = sre.id;
            sre.display = `${sre.firstName} ${sre.lastName}`;
          });
        });
      });
    }else{
      this.authService.getUsersByTeam("EPM").subscribe(data => this.epms = data);
      this.authService.getUsersByTeam("DEV").subscribe(data => this.devs = data);
      this.authService.getUsersByTeam("SRE").subscribe(data => this.sres = data);
    }
    this.appsService.getConfigTypes().subscribe(data => this.configTypes = data);
  }

  addConfig() {
    var exConf = this.app.appConfigs.filter((c: any) => c.name == this.configToAdd.name)[0];
    if(exConf){
      this.toastService.showToast("danger", "Problem occured", "Config already exists");
      return;
    }
    if(!this.app.appConfigs){
      this.app.appConfigs = [];
    }
    this.app.appConfigs.push({
      name: this.configToAdd.name,
      values: []
    });
    this.toastService.showToast("info", "Info", "Config added below");
    this.saveApp(true);
  }

  setConfigValueOnText(val: any, config: any){
    config.valueToAdd = val;
  }

  addCustomConfig() {
    var exConf = this.app.appConfigs.filter((c: any) => c.name == this.customConfigToAdd)[0];
    if(exConf){
      this.toastService.showToast("danger", "Problem occured", "Config already exists");
      return;
    }
    if(!this.app.appConfigs){
      this.app.appConfigs = [];
    }
    this.app.appConfigs.push({
      name: this.customConfigToAdd,
      values: []
    });
    this.toastService.showToast("info", "Info", "Config added below");
    this.saveApp(true);
  }

  deleteConfig(config: any) {
    this.app.appConfigs.splice(this.app.appConfigs.indexOf(config), 1);
    this.toastService.showToast("info", "Info", "Config deleted");
    this.saveApp(true);
  }

  saveApp(silent: boolean = false){
    if(!this.app.id){
      return; //do not auto-save for new feature
    }
    if(this.app.internalName.indexOf("/") > -1){
      this.toastService.showToast("danger", "Invalid input", "App name cannot contain /");
      return;
    }
    this.app.devIdToSave = this.app.dev ? this.app.dev.id : 0;
    this.app.epmIdToSave = this.app.epm ? this.app.epm.id : 0;
    this.app.sreIdToSave = this.app.sre ? this.app.sre.id : 0;
    this.appsService.updateApp(this.app).subscribe(
      (data) => {
        if(!silent){
          this.toastService.showToast("success", "Success", "Changes saved successfully");
        }
        if(data.appConfigs){ //update saved IDs
          data.appConfigs.forEach((c: any) => {
            let appConf = this.app.appConfigs.filter((ac: any) => ac.name == c.name)[0];
            appConf.id = c.id;
            appConf.values.forEach((v: any) => {
              let confVal = c.values.filter((cv: any) => cv.val == v.val)[0];
              v.id = confVal.id;
              v.dateAdded = confVal.dateAdded;
            });
          });
        }        
      },(error) => {
        console.error(error);
        this.toastService.showToast("danger", "Problem occured", "Unable to save changes");
      }
    );
  }

  submitValue(config: any){
    if(config.valueToAdd == ""){
      return;
    }
    let exVal = config.values.filter((v: any) => v.val == config.valueToAdd)[0];
    if(exVal){
      this.toastService.showToast("danger", "Problem occured", "Config value already exists");
      return;
    }
    config.values.push({
      val: config.valueToAdd,
      rolloutPhase: "NEW",
      rolloutPhaseEnum: {
        value: "NEW",
        shortForm: "NEW"
      }
    });
    config.valueToAdd = "";
    this.saveApp();
  }

  deleteValue(config: any, value: any){
    if(value.rolloutPhase != "NEW"){
      this.toastService.showToast("danger", "Unable to delete config value", "Rollout phase is not NEW");
      return;
    }
    config.values.splice(config.values.indexOf(value), 1);
    this.saveApp();
  }

  addApp(){
    if(this.app.internalName.indexOf("/") > -1){
      this.toastService.showToast("danger", "Invalid input", "App name cannot contain /");
      return;
    }

    this.appsService.addApp(this.app).subscribe(
      (data) => {
        this.toastService.showToast("success", "Success", "Changes saved successfully"); 
        this.appName = data.internalName;
        this.app = data;
        this.ngOnInit();
      },(error) => {
        console.error(error);
        this.toastService.showToast("danger", "Invalid Request", error.error.message);
      }
    );
  }

  copyToClipboard(item: any){
    this.clipboard.copy(item);
    this.toastService.showToast("info", "Copied to clipboard");
  }

}
