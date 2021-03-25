import { Component, OnInit } from "@angular/core";
import { Conversation } from "../types";
import { DataService } from "../data/data.service";
import { ActiveFilter, Filter, FilterType, } from "../filter-panel/filter-panel.component";

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

  private participantFilter: Filter = {
    fieldName: "participantId",
    filterType: FilterType.TEXT,
  };
  private choreographyFilter: Filter = {
    fieldName: "choreographyId",
    filterType: FilterType.TEXT,
  };

  filters = [
    {
      fieldName: "startEndDateRange",
      filterType: FilterType.DATE_TIME_RANGE,
      defaultValue: {
        startDate: ConversationListComponent.START_DATE_DEFAULT,
        endDate: ConversationListComponent.END_DATE_DEFAULT,
      },
    },
    {
      fieldName: "conversationId",
      filterType: FilterType.TEXT,
    },
    this.choreographyFilter,
    this.participantFilter,
    {
      fieldName: "status",
      filterType: FilterType.SELECT,
      allowedValues: ["ERROR", "PROCESSING", "IDLE", "COMPLETED"],
    },
  ];
  activeFilters: ActiveFilter[] = [];

  constructor(private dataService: DataService) {
  }

  async ngOnInit() {
    this.participantFilter.allowedValues = await this.dataService.getParticipantIds();
    this.choreographyFilter.allowedValues = await this.dataService.getChoreographyIds();
  }

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

  filterMessages(activeFilters: ActiveFilter[]) {
    this.activeFilters = activeFilters;
    this.refreshConversationCount();
  }
}
