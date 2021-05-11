import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { NotificationComponent } from "../notification/notification.component";
import {
  Action,
  EngineLog,
  ListConfig,
  MessageDetail,
  NotificationItem,
} from "../types";
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
  engineLogs: EngineLog[] = [];
  logsExpanded = true;

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

  logConfig: ListConfig[] = [
    {
      fieldName: "severity",
    },
    {
      fieldName: "createdDate",
    },
    {
      fieldName: "description",
    },
    {
      fieldName: "origin",
    },
    {
      fieldName: "className",
    },
    {
      fieldName: "methodName",
    },
  ];

  actions: Action[] = [
    {
      label: "requeue",
      icon: "refresh",
      actionKey: "/message/requeue",
    },
    {
      label: "stop",
      icon: "stop",
      actionKey: "/message/stop",
    },
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
      this.engineLogs = item.engineLogs || [];
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

  update() {
    // message needs to be resetted otherwise it pushes message as new item in loadMessage
    this.message = [];
    this.loadMessage(String(this.route.snapshot.paramMap.get("id")));
  }

  back() {
    this.location.back();
  }

  toggleLogArea() {
    this.logsExpanded = !this.logsExpanded;
  }
}
