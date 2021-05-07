import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Action } from "../types";
import { PermissionService } from "../services/permission.service";
import { ActionService } from "../services/action.service";

@Component({
  selector: 'app-action-button',
  templateUrl: './action-button.component.html',
  styleUrls: ['./action-button.component.scss']
})
export class ActionButtonComponent implements OnInit {
  @Input() action!: Action;
  @Output() isLoading: EventEmitter<boolean> = new EventEmitter();
  isPermitted = false;

  constructor(private permissionService: PermissionService, private actionService: ActionService) {
  }

  async ngOnInit() {
    this.isPermitted = await this.permissionService.isUserPermitted(this.action.actionKey);
  }

  performAction(): void {
    this.isLoading.emit(true);
    switch (this.action.actionKey) {
      case "messages.stop":
        this.actionService.stopMessages();
        break;
    }
    this.isLoading.emit(false);
  }
}
