import { Component, OnInit } from "@angular/core";
import { DataService } from "../services/data.service";

@Component({
  selector: "app-conversation-status-counts",
  templateUrl: "./conversation-status-counts.component.html",
  styleUrls: ["./conversation-status-counts.component.scss"],
})
export class ConversationStatusCountsComponent implements OnInit {
  conversationStatusCounts: { [status: string]: number } = {};
  totalCount: number = 0;
  objectKeys = Object.keys;

  constructor(private dataService: DataService) {}

  async ngOnInit(): Promise<void> {
    this.conversationStatusCounts = await this.dataService.getConversationStatusCounts();
    this.getConversationStatusTotalCount();
  }

  getConversationStatusTotalCount() {
    if (Object.keys(this.conversationStatusCounts).length > 0) {
      for (let conversationStatusCountsKey in this.conversationStatusCounts) {
        this.totalCount =
          this.totalCount +
          this.conversationStatusCounts[conversationStatusCountsKey];
      }
    }
  }
}
