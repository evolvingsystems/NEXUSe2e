import { Injectable } from "@angular/core";
import { DataService } from "./data.service";
import { SelectionService } from "./selection.service";
import { Conversation, Message, NotificationItem } from "../types";
import { MatSnackBar } from "@angular/material/snack-bar";
import { NotificationComponent } from "../notification/notification.component";
import { MatDialog } from "@angular/material/dialog";
import { UserConfirmationDialogComponent } from "../user-confirmation-dialog/user-confirmation-dialog.component";

@Injectable({
  providedIn: "root",
})
export class ActionService {
  constructor(
    private dataService: DataService,
    private selectionService: SelectionService,
    private _snackBar: MatSnackBar,
    private _dialog: MatDialog
  ) {}

  async stopMessages() {
    const messages = this.selectionService.getSelectedItems("message");
    try {
      await this.dataService.stopMessages(
        messages.map((m) => (m as Message).messageId)
      );
    } catch {
      this._snackBar.openFromComponent(NotificationComponent, {
        duration: 5000,
        data: {
          snackType: "error",
          textLabel: "messageStatusError",
        } as NotificationItem,
      });
    }
  }

  async requeueMessages() {
    const messages = this.selectionService.getSelectedItems("message");
    try {
      await this.dataService.requeueMessages(
        messages.map((m) => (m as Message).messageId)
      );
    } catch {
      this._snackBar.openFromComponent(NotificationComponent, {
        duration: 5000,
        data: {
          snackType: "error",
          textLabel: "messageStatusError",
        } as NotificationItem,
      });
    }
  }

  async deleteConversations() {
    const conversations = this.selectionService.getSelectedItems(
      "conversation"
    );

    const dialogRef = this._dialog.open(UserConfirmationDialogComponent, {
      data: {
        notificationTitleLabel:
          conversations.length > 1
            ? "deleteConversationsTitle"
            : "deleteConversationTitle",
        notificationTextLabel: "actionNotReversible",
        confirmButtonLabel: "delete",
      },
    });

    const result = await dialogRef.afterClosed().toPromise();

    if (result) {
      try {
        await this.dataService.deleteConversations(
          conversations.map((c) => (c as Conversation).conversationId)
        );
      } catch {
        this._snackBar.openFromComponent(NotificationComponent, {
          duration: 5000,
          data: {
            snackType: "error",
            textLabel: "conversationDeleteError",
          } as NotificationItem,
        });
      }
    }
  }
}
