import { Component, Input, OnInit } from "@angular/core";
import { Choreography, Partner } from "../types";
import {
  CARD_LINK_CONFIG,
  SUCCESS_MESS__CHOREOGRAPHY_CONFIG,
  SUCCESS_MESS__PARTNER_CONFIG,
} from "./messages-successful.config";
import { DataService } from "../services/data.service";
import { ScreensizeService } from "../services/screensize.service";
import { RequestHelper } from "../services/request-helper";

@Component({
  selector: "app-successful-messages-list",
  templateUrl: "./messages-successful.component.html",
  styleUrls: ["./messages-successful.component.scss"],
})
export class MessagesSuccessfulComponent implements OnInit {
  @Input() transactionActivityTimeframeInWeeks = 0;
  choreographies: Choreography[] | undefined;
  choreographyConfig = SUCCESS_MESS__CHOREOGRAPHY_CONFIG;
  partners: Partner[] | undefined;
  partnerConfig = SUCCESS_MESS__PARTNER_CONFIG;
  loaded = false;
  mobileCardLinkUrl = CARD_LINK_CONFIG.linkUrl;
  mobileCardLinkQueryParams = CARD_LINK_CONFIG.linkParamsRecipe;

  constructor(
    private dataService: DataService,
    public screenSizeService: ScreensizeService,
    public requestHelper: RequestHelper
  ) {}

  async ngOnInit() {
    this.partners = await this.dataService.getStatisticsPartners();
    this.choreographies = await this.dataService.getStatisticsChoreographies();
    this.loaded = true;
  }
}
