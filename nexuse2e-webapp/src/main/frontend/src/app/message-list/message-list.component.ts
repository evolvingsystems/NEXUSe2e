import { Component, OnInit } from "@angular/core";
import { ActiveFilterList, Message, } from "../types";
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
} from "./message-list.config";

@Component({
  selector: "app-message-list",
  templateUrl: "./message-list.component.html",
  styleUrls: ["./message-list.component.scss"],
})
export class MessageListComponent implements OnInit {
  totalMessageCount?: number;
  messages: Message[] = [];
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
    this.loadMessages(0, this.sessionService.getPageSize("message") || this.defaultPageSize);
  }
}
