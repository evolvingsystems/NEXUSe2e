import { Component, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { NotificationComponent } from "../notification/notification.component";
import {
  EngineLog,
  isMessageDetail,
  MessageDetail,
  NotificationItem,
  Payload,
} from "../types";
import { DataService } from "../services/data.service";
import { MatSnackBar } from "@angular/material/snack-bar";
import { Location } from "@angular/common";
import {
  MESS_DETAIL__ACTIONS,
  MESS_DETAIL__LOG_CONFIG,
  MESS_DETAIL__MESSAGE_CONFIG,
} from "./message-detail.config";

@Component({
  selector: "app-message-detail",
  templateUrl: "./message-detail.component.html",
  styleUrls: ["./message-detail.component.scss"],
})
export class MessageDetailComponent implements OnInit {
  message: MessageDetail[] = [];
  engineLogs: EngineLog[] = [];
  messagePayloads: Payload[] = [];
  messageLabels?: ReadonlyMap<string, string>;
  loaded = false;
  contentExpanded = true;
  messageLabelsExpanded = true;
  logsExpanded = true;
  messageConfig = MESS_DETAIL__MESSAGE_CONFIG;
  logConfig = MESS_DETAIL__LOG_CONFIG;
  actions = MESS_DETAIL__ACTIONS;

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
    this.loaded = false;
    try {
      const item = await this.dataService.getMessageByNxId(nxMessageId);
      this.message.push(item);
      this.engineLogs = item.engineLogs || [];
      this.messagePayloads = item.messagePayloads || [];
      this.messageLabels = item.messageLabels || {};
    } catch {
      this._snackBar.openFromComponent(NotificationComponent, {
        duration: 5000,
        data: {
          snackType: "error",
          textLabel: "messageGetError",
        } as NotificationItem,
      });
    }
    this.loaded = true;
  }

  update() {
    // message needs to be resetted otherwise it pushes message as new item in loadMessage
    this.message = [];
    this.loadMessage(String(this.route.snapshot.paramMap.get("id")));
  }

  buildDownloadPayloadLink(payloadId?: number): string {
    const message = this.message[0];
    if (isMessageDetail(message)) {
      const affectedPayload = {
        choreographyId: message.choreographyId,
        partnerId: message.partnerId,
        conversationId: message.conversationId,
        messageId: message.messageId,
        payloadId: payloadId?.toString(),
      };
      return this.dataService.getDownloadPayloadLink(affectedPayload);
    }

    return "";
  }

  back() {
    this.location.back();
  }

  toggleContentArea() {
    this.contentExpanded = !this.contentExpanded;
  }

  toggleMessageLabelsArea() {
    this.messageLabelsExpanded = !this.messageLabelsExpanded;
  }

  toggleLogArea() {
    this.logsExpanded = !this.logsExpanded;
  }
}
