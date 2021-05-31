import { Component, OnInit } from "@angular/core";
import { DataService } from "../services/data.service";
import { SessionService } from "../services/session.service";

@Component({
  selector: "app-conversation-status-counts",
  templateUrl: "./conversation-status-counts.component.html",
  styleUrls: ["./conversation-status-counts.component.scss"],
})
export class ConversationStatusCountsComponent implements OnInit {
  conversationStatusCounts: { [status: string]: number } = {};
  totalCount = 0;
  objectKeys = Object.keys;
  loaded = false;
  dashboardTimeFrameInDays = 0;

  constructor(
    private dataService: DataService,
    private sessionService: SessionService
  ) {
    this.dashboardTimeFrameInDays =
      this.sessionService.getEngineTimeVariables()?.dashboardTimeFrameInDays ||
      0;
  }

  async ngOnInit(): Promise<void> {
    this.conversationStatusCounts = await this.dataService.getConversationStatusCounts();
    this.getConversationStatusTotalCount();
  }

  getConversationStatusTotalCount() {
    this.loaded = false;
    if (Object.keys(this.conversationStatusCounts).length > 0) {
      for (const conversationStatusCountsKey in this.conversationStatusCounts) {
        this.totalCount =
          this.totalCount +
          this.conversationStatusCounts[conversationStatusCountsKey];
      }
    }
    this.loaded = true;
  }
}
