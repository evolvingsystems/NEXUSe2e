import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { NotificationComponent } from "../notification/notification.component";
import { ListConfig, MessageDetail, NotificationItem } from "../types";
import { DataService } from "../services/data.service";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Location } from "@angular/common";

@Component({
  selector: "app-message-detail",
  templateUrl: "./message-detail.component.html",
  styleUrls: ["./message-detail.component.scss"],
})
export class MessageDetailComponent implements OnInit {
  message: MessageDetail[] = [];

  messageConfig: ListConfig[] = [
    {
      fieldName: "messageId",
    },
    {
      fieldName: "conversationId",
      linkUrlRecipe: "../../conversation/$nxConversationId$",
    },
    { fieldName: "choreographyId" },
    { fieldName: "partnerId" },
    { fieldName: "typeName", label: "messageType" },
    { fieldName: "direction" },
    { fieldName: "referencedMessageId" },
    { fieldName: "actionId" },
    { fieldName: "backendStatus" },
    { fieldName: "createdDate" },
    { fieldName: "modifiedDate" },
    { fieldName: "endDate" },
    { fieldName: "turnAroundTime" },
    { fieldName: "expirationDate" },
    { fieldName: "retries" },
    { fieldName: "trp", label: "protocolVersion" },
    { fieldName: "status" },
  ];

  constructor(
    private route: ActivatedRoute,
    private dataService: DataService,
    private _snackBar: MatSnackBar,
    private location: Location
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

  back() {
    this.location.back();
  }
}
