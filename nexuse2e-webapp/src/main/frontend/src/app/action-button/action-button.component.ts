import { Component, Input, OnInit } from '@angular/core';
import { Action } from "../types";
import { PermissionService } from "../services/permission.service";

@Component({
  selector: 'app-action-button',
  templateUrl: './action-button.component.html',
  styleUrls: ['./action-button.component.scss']
})
export class ActionButtonComponent implements OnInit {
  @Input() action!: Action;
  isPermitted = false;

  constructor(private permissionService: PermissionService) {
  }

  async ngOnInit() {
    this.isPermitted = await this.permissionService.isUserPermitted(this.action.actionKey);
  }

  performAction(): void {
    console.log("action button");
  }
}
