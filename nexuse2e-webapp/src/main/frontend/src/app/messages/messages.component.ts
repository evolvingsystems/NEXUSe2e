import { Component, OnInit } from "@angular/core";
import { ActiveFilterList, Message } from "../types";
import { DataService } from "../services/data.service";
import { SessionService } from "../services/session.service";
import {
  MESS_LIST__ACTIONS,
  activeFilters,
  choreographyFilter,
  MESS_LIST__DEFAULT_PAGE_SIZE,
  MESS_LIST__DESKTOP_CONFIG,
  MESS_LIST__FILTERS,
  MESS_LIST__MOBILE_CONFIG,
  participantFilter,
} from "./messages.config";

@Component({
  selector: "app-messages",
  templateUrl: "./messages.component.html",
  styles: [],
})
export class MessagesComponent implements OnInit {
  totalMessageCount?: number;
  messages: Message[] = [];
  loaded = false;
  defaultPageSize = MESS_LIST__DEFAULT_PAGE_SIZE;
  desktopConfig = MESS_LIST__DESKTOP_CONFIG;
  mobileConfig = MESS_LIST__MOBILE_CONFIG;
  filters = MESS_LIST__FILTERS;
  activeFilters = activeFilters;
  actions = MESS_LIST__ACTIONS;

  constructor(
    private dataService: DataService,
    private sessionService: SessionService
  ) {}

  async ngOnInit() {
    participantFilter.allowedValues = await this.dataService.getPartnerIds();
    choreographyFilter.allowedValues = await this.dataService.getChoreographyIds();
  }

  async loadMessages(pageIndex: number, pageSize: number) {
    this.loaded = false;
    this.messages = await this.dataService.getMessages(
      pageIndex,
      pageSize,
      this.activeFilters
    );
    this.loaded = true;
  }

  async refreshMessageCount() {
    this.totalMessageCount = await this.dataService.getMessagesCount(
      this.activeFilters
    );
  }

  filterMessages(activeFilters: ActiveFilterList) {
    this.activeFilters = activeFilters;
    this.refreshMessageCount();
    this.loadMessages(
      0,
      this.sessionService.getPageSize("message") || this.defaultPageSize
    );
  }
}
