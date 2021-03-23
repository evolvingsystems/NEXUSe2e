import { Component, OnInit } from "@angular/core";
import { Conversation } from "../types";
import { DataService } from "../data/data.service";
import { ActiveFilter, FilterType } from "../filter-panel/filter-panel.component";

@Component({
  selector: "app-conversation-list",
  templateUrl: "./conversation-list.component.html",
  styleUrls: ["./conversation-list.component.scss"],
})
export class ConversationListComponent implements OnInit {
  totalConversationCount?: number;
  conversations: Conversation[] = [];
  filters = [
    {
      fieldName: "status",
      filterType: FilterType.SELECT,
      allowedValues: ["ERROR", "PROCESSING", "IDLE", "COMPLETED"]
    },
    {
      fieldName: "conversationId",
      filterType: FilterType.TEXT
    }
  ];
  activeFilters: ActiveFilter[] = [];

  constructor(private dataService: DataService) {
  }

  async ngOnInit() {
    this.totalConversationCount = await this.dataService.getConversationsCount(this.activeFilters);
  }

  async loadConversations(pageIndex: number, pageSize: number) {
    this.conversations = await this.dataService.getConversations(
      pageIndex,
      pageSize,
      this.activeFilters
    );
  }

  async refreshConversationCount() {
    this.totalConversationCount = await this.dataService.getConversationsCount(this.activeFilters);
  }

  filterMessages(activeFilters: ActiveFilter[]) {
    this.activeFilters = activeFilters;
    this.refreshConversationCount();
  }
}
