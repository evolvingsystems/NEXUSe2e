import { Component, Input, OnInit } from "@angular/core";
import { Message } from "../types";
import {
  CARD_LINK_CONFIG,
  FAILED_MESSAGES_CONFIG,
} from "./messages-failed.config";
import { DataService } from "../services/data.service";
import { ScreensizeService } from "../services/screensize.service";
import { RequestHelper } from "../services/request-helper";

@Component({
  selector: "app-messages-failed",
  templateUrl: "./messages-failed.component.html",
  styleUrls: ["./messages-failed.component.scss"],
})
export class MessagesFailedComponent implements OnInit {
  @Input() dashboardTimeFrameInDays = 0;
  failedMessages: Message[] | undefined;
  failedMessagesConfig = FAILED_MESSAGES_CONFIG;
  mobileCardLinkUrl = CARD_LINK_CONFIG.linkUrl;
  mobileCardLinkQueryParams = CARD_LINK_CONFIG.linkParamsRecipe;
  failedMessagesCount = 0;
  loaded = false;

  constructor(
    private dataService: DataService,
    public screenSizeService: ScreensizeService,
    public requestHelper: RequestHelper
  ) {}

  async ngOnInit() {
    this.failedMessages = await this.dataService.getFailedMessages();
    this.loaded = true;
  }
}
