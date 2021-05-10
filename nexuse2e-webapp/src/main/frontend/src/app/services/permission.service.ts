import { Injectable } from '@angular/core';
import { SessionService } from "../services/session.service";
import { DataService } from "./data.service";

@Injectable({
  providedIn: 'root'
})
export class PermissionService {

  constructor(private sessionService: SessionService, private dataService: DataService) {
  }

  async updatePermissions() {
    const permittedActions = await this.dataService.getPermittedActions();
    this.sessionService.setPermittedActions(permittedActions);
  }

  isUserPermitted(actionKey: string) {
    return this.sessionService.getPermittedActions().some(a => a === "*" || a === actionKey);
  }
}
