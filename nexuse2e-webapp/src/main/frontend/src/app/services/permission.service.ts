import { Injectable } from '@angular/core';
import { SessionService } from "../data/session.service";

@Injectable({
  providedIn: 'root'
})
export class PermissionService {

  constructor(private sessionService: SessionService) {
  }

  isUserPermitted(actionKey: string) {
    return this.sessionService.getPermittedActions().some(a => a === "*" || a === actionKey);
  }
}
