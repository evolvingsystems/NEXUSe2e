import { Component, OnInit } from "@angular/core";
import {

  ActiveFilterList,
  Conversation
} from "../types";
import { DataService } from "../services/data.service";
import { SessionService } from "../services/session.service";
import {
  CONV_LIST__ACTIONS,
  activeFilters,
  choreographyFilter,
  CONV_LIST__DEFAULT_PAGE_SIZE,
  CONV_LIST__DESKTOP_CONFIG,
  CONV_LIST__FILTERS,
  CONV_LIST__MOBILE_CONFIG,
  participantFilter,
} from "./conversation-list.config";

@Component({
  selector: "app-conversation-list",
  templateUrl: "./conversation-list.component.html",
  styles: [],
})
export class ConversationListComponent implements OnInit {
  totalConversationCount?: number;
  conversations: Conversation[] = [];
  loaded = false;
  defaultPageSize = CONV_LIST__DEFAULT_PAGE_SIZE;
  desktopConfig = CONV_LIST__DESKTOP_CONFIG;
  mobileConfig = CONV_LIST__MOBILE_CONFIG;
  filters = CONV_LIST__FILTERS;
  activeFilters = activeFilters;
  actions = CONV_LIST__ACTIONS;

  constructor(

    private dataService: DataService,
    private sessionService: SessionService
  ) {}

  async ngOnInit() {
    [
      participantFilter.allowedValues,
      choreographyFilter.allowedValues,
    ] = await Promise.all([
      this.dataService.getPartnerIds(),
      this.dataService.getChoreographyIds(),
    ]);
  }

  async loadConversations(pageIndex: number, pageSize: number) {
    this.loaded = false;
    this.conversations = await this.dataService.getConversations(
      pageIndex,
      pageSize,
      this.activeFilters
    );
    this.loaded = true;
  }

  async refreshConversationCount() {
    this.totalConversationCount = await this.dataService.getConversationsCount(
      this.activeFilters
    );
  }

  filterConversations(activeFilters: ActiveFilterList) {
    this.activeFilters = activeFilters;
    this.refreshConversationCount();
    this.loadConversations(
      0,
      this.sessionService.getPageSize("conversation") || this.defaultPageSize
    );
  }
}
