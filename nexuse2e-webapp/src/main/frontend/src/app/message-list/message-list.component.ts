import { Component, OnInit } from "@angular/core";
import { Message } from "../types";
import { DataService } from "../data/data.service";
import {
  ActiveFilter,
  FilterType,
} from "../filter-panel/filter-panel.component";

@Component({
  selector: "app-message-list",
  templateUrl: "./message-list.component.html",
  styleUrls: ["./message-list.component.scss"],
})
export class MessageListComponent implements OnInit {
  totalMessageCount?: number;
  messages: Message[] = [];
  static readonly START_DATE_DEFAULT: Date = new Date(
    new Date().setHours(0, 0, 0, 0)
  );
  static readonly END_DATE_DEFAULT: Date = new Date(
    new Date().setHours(24, 0, 0, 0)
  );
  filters = [
    {
      fieldName: "status",
      filterType: FilterType.SELECT,
      allowedValues: [
        "FAILED",
        "SENT",
        "UNKNOWN",
        "RETRYING",
        "QUEUED",
        "STOPPED",
      ],
    },
    {
      fieldName: "type",
      filterType: FilterType.SELECT,
      allowedValues: ["NORMAL", "ACKNOWLEDGEMENT", "ERROR"],
    },
    {
      fieldName: "conversationId",
      filterType: FilterType.TEXT_FIELD,
    },
    {
      fieldName: "startEndDateRange",
      filterType: FilterType.DATE_TIME_RANGE,
      defaultValue: {
        startDate: MessageListComponent.START_DATE_DEFAULT,
        endDate: MessageListComponent.END_DATE_DEFAULT,
      },
    },
  ];
  activeFilters: ActiveFilter[] = [];

  constructor(private dataService: DataService) {}

  async ngOnInit() {
    // set default filter values as active filters for first page load
    this.activeFilters.push({
      fieldName: "startEndDateRange",
      value: {
        startDate: MessageListComponent.START_DATE_DEFAULT,
        endDate: MessageListComponent.END_DATE_DEFAULT,
      },
    });
    this.refreshMessageCount();
  }

  async loadMessages(pageIndex: number, pageSize: number) {
    this.messages = await this.dataService.getMessages(
      pageIndex,
      pageSize,
      this.activeFilters
    );
  }

  async refreshMessageCount() {
    this.totalMessageCount = await this.dataService.getMessagesCount(
      this.activeFilters
    );
  }

  filterMessages(activeFilters: ActiveFilter[]) {
    this.activeFilters = activeFilters;
    this.refreshMessageCount();
  }
}
