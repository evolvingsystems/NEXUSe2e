import { Component, OnInit } from "@angular/core";
import { Choreography, Partner } from "../types";
import {
  SUCCESS_MESS__CHOREOGRAPHY_CONFIG,
  SUCCESS_MESS__PARTNER_CONFIG,
} from "./successful-messages-list.config";

@Component({
  selector: "app-successful-messages-list",
  templateUrl: "./successful-messages-list.component.html",
  styleUrls: ["./successful-messages-list.component.scss"],
})
export class SuccessfulMessagesListComponent implements OnInit {
  choreographies: Choreography[] | undefined;
  choreographyConfig = SUCCESS_MESS__CHOREOGRAPHY_CONFIG;
  partners: Partner[] | undefined;
  partnerConfig = SUCCESS_MESS__PARTNER_CONFIG;

  constructor() {}

  ngOnInit(): void {}
}
