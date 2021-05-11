import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { NotificationComponent } from "../notification/notification.component";
import { MessageDetail, NotificationItem } from "../types";
import { DataService } from "../services/data.service";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  selector: "app-message-detail",
  templateUrl: "./message-detail.component.html",
  styleUrls: ["./message-detail.component.scss"],
})
export class MessageDetailComponent implements OnInit {
  message: MessageDetail[] = [];

  constructor(
    private route: ActivatedRoute,
    private dataService: DataService,
    private _snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const id = String(this.route.snapshot.paramMap.get("id"));
    this.loadMessage(id);
  }

  async loadMessage(nxMessageId: string) {
    try {
      const item = await this.dataService.getMessageById(nxMessageId);
      this.message.push(item);
    } catch {
      this._snackBar.openFromComponent(NotificationComponent, {
        duration: 5000,
        data: {
          snackType: "error",
          textLabel: "messageGetError",
        } as NotificationItem,
      });
    }
  }
}
