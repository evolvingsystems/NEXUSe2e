import { Component, Input, OnInit } from "@angular/core";
import {
  CARD_LINK_CONFIG,
  IDLE_CONV_CONFIG,
} from "./conversations-idle.config";
import { Conversation } from "../types";
import { DataService } from "../services/data.service";
import { ScreensizeService } from "../services/screensize.service";
import { RequestHelper } from "../services/request-helper";

@Component({
  selector: "app-conversations-idle",
  templateUrl: "./conversations-idle.component.html",
  styleUrls: ["./conversations-idle.component.scss"],
})
export class ConversationsIdleComponent implements OnInit {
  @Input() dashboardTimeFrameInDays = 0;
  @Input() idleGracePeriodInMinutes = 0;
  idleConversations: Conversation[] | undefined;
  idleConversationsConfig = IDLE_CONV_CONFIG;
  loaded = false;
  mobileCardLinkUrl = CARD_LINK_CONFIG.linkUrl;
  mobileCardLinkQueryParams = CARD_LINK_CONFIG.linkParamsRecipe;
  idleConversationsCount = 0;

  constructor(
    private dataService: DataService,
    public screenSizeService: ScreensizeService,
    public requestHelper: RequestHelper
  ) {}

  async ngOnInit() {
    this.idleConversations = await this.dataService.getIdleConversations();
    this.idleConversationsCount = await this.dataService.getConversationsCount(
      this.requestHelper.getQueryParams(CARD_LINK_CONFIG.linkParamsRecipe)
    );
    this.loaded = true;
  }
}
