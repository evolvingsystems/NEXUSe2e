import { Component, OnInit } from "@angular/core";
import {
  Action,
  ConversationDetail,
  EngineLog,
  ListConfig,
  Message,
  NotificationItem,
} from "../types";
import { ActivatedRoute, Router } from "@angular/router";
import { DataService } from "../services/data.service";
import { Location } from "@angular/common";
import { NotificationComponent } from "../notification/notification.component";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  selector: "app-conversation-detail",
  templateUrl: "./conversation-detail.component.html",
  styleUrls: ["./conversation-detail.component.scss"],
})
export class ConversationDetailComponent implements OnInit {
  conversation: ConversationDetail[] = [];
  messages: Message[] = [];
  engineLogs: EngineLog[] = [];
  messagesExpanded = true;
  logsExpanded = true;
  conversationConfig: ListConfig[] = [
    {
      fieldName: "conversationId",
    },
    {
      fieldName: "createdDate",
    },
    {
      fieldName: "modifiedDate",
    },
    {
      fieldName: "endDate",
    },
    {
      fieldName: "turnAroundTime",
    },
    {
      fieldName: "partnerId",
    },
    {
      fieldName: "currentAction",
    },
    {
      fieldName: "status",
    },
  ];

  messageConfig: ListConfig[] = [
    {
      fieldName: "messageId",
      linkUrlRecipe: "../../message/$nxMessageId$",
    },
    { fieldName: "typeName", label: "messageType" },
    {
      fieldName: "choreographyId",
      additionalFieldName: "actionId",
      label: "step",
    },
    { fieldName: "direction" },
    { fieldName: "createdDate" },
    { fieldName: "endDate" },
    { fieldName: "turnAroundTime" },
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
      label: "delete",
      icon: "delete",
      actionKey: "/conversation/delete",
    },
  ];

  constructor(
    private route: ActivatedRoute,
    private dataService: DataService,
    private location: Location,
    private _snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit() {
    const id = String(this.route.snapshot.paramMap.get("id"));
    this.loadConversation(id);
  }

  async loadConversation(nxConversationId: string) {
    try {
      const item = await this.dataService.getConversationById(nxConversationId);
      this.conversation.push(item);
      this.messages = item.messages || [];
      this.engineLogs = item.engineLogs || [];
    } catch {
      this._snackBar.openFromComponent(NotificationComponent, {
        duration: 5000,
        data: {
          snackType: "error",
          textLabel: "conversationGetError",
        } as NotificationItem,
      });
    }
  }

  back() {
    this.location.back();
  }

  toggleMessagesArea() {
    this.messagesExpanded = !this.messagesExpanded;
  }

  toggleLogArea() {
    this.logsExpanded = !this.logsExpanded;
  }

  redirect() {
    this.router.navigateByUrl("/reporting/transaction-reporting/conversations"); //TODO
  }
}
