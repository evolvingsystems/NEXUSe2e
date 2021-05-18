import { Component, OnInit } from "@angular/core";
import {
  ConversationDetail,
  EngineLog,
  Message,
  NotificationItem,
} from "../types";
import { ActivatedRoute, Router } from "@angular/router";
import { DataService } from "../services/data.service";
import { Location } from "@angular/common";
import { NotificationComponent } from "../notification/notification.component";
import { MatSnackBar } from "@angular/material/snack-bar";
import {
  CONV_DETAIL__ACTIONS,
  CONV_DETAIL__CONVERSATION_CONFIG,
  CONV_DETAIL__LOG_CONFIG,
  CONV_DETAIL__MESSAGE_CONFIG,
} from "./conversation-detail.config";

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
  conversationConfig = CONV_DETAIL__CONVERSATION_CONFIG;
  messageConfig = CONV_DETAIL__MESSAGE_CONFIG;
  logConfig = CONV_DETAIL__LOG_CONFIG;
  actions = CONV_DETAIL__ACTIONS;

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
    this.router.navigateByUrl("/reporting/transaction-reporting/conversations");
  }
}
