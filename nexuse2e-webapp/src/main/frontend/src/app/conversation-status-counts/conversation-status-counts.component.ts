import { Component, Input, OnInit } from "@angular/core";
import { DataService } from "../services/data.service";

@Component({
  selector: "app-conversation-status-counts",
  templateUrl: "./conversation-status-counts.component.html",
  styleUrls: ["./conversation-status-counts.component.scss"],
})
export class ConversationStatusCountsComponent implements OnInit {
  @Input() dashboardTimeFrameInDays = 0;
  conversationStatusCounts: { [status: string]: number } = {};
  totalCount = 0;
  objectKeys = Object.keys;
  loaded = false;

  constructor(private dataService: DataService) {}

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
