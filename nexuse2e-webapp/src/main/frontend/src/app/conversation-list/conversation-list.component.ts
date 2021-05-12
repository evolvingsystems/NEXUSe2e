import { Component, OnInit } from "@angular/core";
import { Action, ActiveFilterList, Conversation, Filter, FilterType, ListConfig, } from "../types";
import { DataService } from "../services/data.service";
import { SessionService } from "../services/session.service";

@Component({
  selector: "app-conversation-list",
  templateUrl: "./conversation-list.component.html",
  styleUrls: ["./conversation-list.component.scss"],
})
export class ConversationListComponent implements OnInit {
  totalConversationCount?: number;
  conversations: Conversation[] = [];
  loaded = false;
  static readonly START_DATE_DEFAULT: Date = new Date(
    new Date().setHours(0, 0, 0, 0)
  );
  static readonly END_DATE_DEFAULT: Date = new Date(
    new Date().setHours(24, 0, 0, 0)
  );
  defaultPageSize = 20;

  private participantFilter: Filter = {
    fieldName: "partnerId",
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
  activeFilters: ActiveFilterList = {};

  mobileConfig: ListConfig[] = [
    {
      fieldName: "conversationId",
      linkUrlRecipe: "../../conversation/$nxConversationId$",
      isHeader: true,
    },
    { fieldName: "choreographyId" },
    { fieldName: "partnerId" },
    { fieldName: "createdDate" },
  ];

  desktopConfig: ListConfig[] = [
    {
      fieldName: "conversationId",
      linkUrlRecipe: "../../conversation/$nxConversationId$",
    },
    {
      fieldName: "partnerId",
    },
    {
      fieldName: "choreographyId",
    },
    {
      fieldName: "currentAction",
    },
    {
      fieldName: "createdDate",
    },
    {
      fieldName: "status",
    },
    {
      fieldName: "turnAroundTime",
    },
  ];

  actions: Action[] = [
    {
      label: "delete",
      icon: "delete",
      actionKey: "/conversations/delete",
    },
  ];

  constructor(private dataService: DataService, private sessionService: SessionService) {
  }

  async ngOnInit() {
    [
      this.participantFilter.allowedValues,
      this.choreographyFilter.allowedValues,
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
