import { Component, Inject, OnInit } from "@angular/core";
import { MAT_DIALOG_DATA } from "@angular/material/dialog";
import { UserConfirmationDialog } from "../types";

@Component({
  selector: "app-user-confirmation-dialog",
  templateUrl: "./user-confirmation-dialog.component.html",
  styleUrls: ["./user-confirmation-dialog.component.scss"],
})
export class UserConfirmationDialogComponent implements OnInit {
  notificationTitleLabel?: string;
  notificationTextLabel?: string;
  confirmButtonLabel?: string;

  constructor(@Inject(MAT_DIALOG_DATA) public data: UserConfirmationDialog) {
    this.notificationTitleLabel = data.notificationTitleLabel;
    this.notificationTextLabel = data.notificationTextLabel;
    this.confirmButtonLabel = data.confirmButtonLabel;
  }

  ngOnInit(): void {}
}
