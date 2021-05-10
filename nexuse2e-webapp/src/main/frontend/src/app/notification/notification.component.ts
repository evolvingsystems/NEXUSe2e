import { Component, Inject, OnInit } from "@angular/core";
import { MAT_SNACK_BAR_DATA } from "@angular/material/snack-bar";
import { NotificationItem } from "../types";

@Component({
  selector: "app-notification",
  templateUrl: "./notification.component.html",
  styleUrls: ["./notification.component.scss"],
})
export class NotificationComponent implements OnInit {
  constructor(@Inject(MAT_SNACK_BAR_DATA) public data: NotificationItem) {}

  ngOnInit(): void {}

  getIcon(): string {
    switch (this.data.snackType) {
      case "success":
        return "check";
      case "error":
        return "error";
      case "warn":
        return "warning_amber";
      case "info":
        return "info";
      default:
        return "";
    }
  }
}
