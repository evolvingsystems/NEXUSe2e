import { Component, OnInit } from "@angular/core";
import { Conversation } from "../types";
import { DataService } from "../data/data.service";

@Component({
  selector: "app-conversation-list",
  templateUrl: "./conversation-list.component.html",
  styleUrls: ["./conversation-list.component.scss"],
})
export class ConversationListComponent implements OnInit {
  totalConversationCount?: number;
  conversations: Conversation[] = [];

  constructor(private dataService: DataService) {}

  async ngOnInit() {
    this.totalConversationCount = await this.dataService.getConversationsCount();
  }

  async loadConversations(pageIndex: number, pageSize: number) {
    this.conversations = await this.dataService.getConversations(
      pageIndex,
      pageSize
    );
  }
}
