import { Component, OnInit } from "@angular/core";
import { ActiveFilterList, Conversation, } from "../types";
import { DataService } from "../services/data.service";
import { SessionService } from "../services/session.service";
import {
  actions,
  activeFilters,
  choreographyFilter,
  defaultPageSize,
  desktopConfig,
  filters,
  mobileConfig,
  participantFilter
} from "./conversation-list.config";

@Component({
  selector: "app-conversation-list",
  templateUrl: "./conversation-list.component.html",
  styleUrls: ["./conversation-list.component.scss"],
})
export class ConversationListComponent implements OnInit {
  totalConversationCount?: number;
  conversations: Conversation[] = [];
  loaded = false;
  defaultPageSize = defaultPageSize;
  desktopConfig = desktopConfig;
  mobileConfig = mobileConfig;
  filters = filters;
  activeFilters = activeFilters;
  actions = actions;

  constructor(private dataService: DataService, private sessionService: SessionService) {
  }

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
    this.loadConversations(0,
      this.sessionService.getPageSize("conversation") || this.defaultPageSize);
  }
}
