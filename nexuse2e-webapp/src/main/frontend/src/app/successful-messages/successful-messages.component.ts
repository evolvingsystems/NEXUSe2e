import { Component, Input, OnInit } from "@angular/core";
import { Choreography, Partner } from "../types";
import {
  SUCCESS_MESS__CHOREOGRAPHY_CONFIG,
  SUCCESS_MESS__PARTNER_CONFIG,
} from "./successful-messages.config";

@Component({
  selector: "app-successful-messages-list",
  templateUrl: "./successful-messages.component.html",
  styleUrls: ["./successful-messages.component.scss"],
})
export class SuccessfulMessagesComponent implements OnInit {
  @Input() transactionActivityTimeframeInWeeks = 0;
  choreographies: Choreography[] | undefined;
  choreographyConfig = SUCCESS_MESS__CHOREOGRAPHY_CONFIG;
  partners: Partner[] | undefined;
  partnerConfig = SUCCESS_MESS__PARTNER_CONFIG;

  constructor() {}

  ngOnInit(): void {}
}
