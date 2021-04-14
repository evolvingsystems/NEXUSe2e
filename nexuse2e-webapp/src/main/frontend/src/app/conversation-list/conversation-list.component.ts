import { Component, OnInit } from "@angular/core";
import { Conversation, DateRange } from "../types";
import { DataService } from "../data/data.service";
import { FilterType, } from "../filter-panel/filter-panel.component";

@Component({
  selector: "app-conversation-list",
  templateUrl: "./conversation-list.component.html",
  styleUrls: ["./conversation-list.component.scss"],
})
export class ConversationListComponent implements OnInit {
  totalConversationCount?: number;
  conversations: Conversation[] = [];
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
      allowedValues: ["ERROR", "PROCESSING", "IDLE", "COMPLETED"],
    },
    {
      fieldName: "conversationId",
      filterType: FilterType.TEXT,
    },
    {
      fieldName: "startEndDateRange",
      filterType: FilterType.DATE_TIME_RANGE,
      defaultValue: {
        startDate: ConversationListComponent.START_DATE_DEFAULT,
        endDate: ConversationListComponent.END_DATE_DEFAULT,
      },
    },
  ];
  activeFilters: { [fieldName: string]: string | DateRange | undefined } = {};

  constructor(private dataService: DataService) {}

  async ngOnInit() {}

  async loadConversations(pageIndex: number, pageSize: number) {
    this.conversations = await this.dataService.getConversations(
      pageIndex,
      pageSize,
      this.activeFilters
    );
  }

  async refreshConversationCount() {
    this.totalConversationCount = await this.dataService.getConversationsCount(
      this.activeFilters
    );
  }

  filterMessages(activeFilters: { [fieldName: string]: string | DateRange | undefined }) {
    this.activeFilters = activeFilters;
    this.refreshConversationCount();
  }
}
