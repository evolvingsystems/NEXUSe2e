import { Component, Input, OnInit } from "@angular/core";
import { Message } from "../types";
import { FAILED_MESSAGES_CONFIG } from "./messages-failed.config";
import { DataService } from "../services/data.service";

@Component({
  selector: "app-messages-failed",
  templateUrl: "./messages-failed.component.html",
  styleUrls: ["./messages-failed.component.scss"],
})
export class MessagesFailedComponent implements OnInit {
  @Input() dashboardTimeFrameInDays = 0;
  failedMessages: Message[] | undefined;
  failedMessagesConfig = FAILED_MESSAGES_CONFIG;
  loaded = false;

  constructor(private dataService: DataService) {}

  async ngOnInit() {
    this.failedMessages = await this.dataService.getFailedMessages();
    this.loaded = true;
  }
}
