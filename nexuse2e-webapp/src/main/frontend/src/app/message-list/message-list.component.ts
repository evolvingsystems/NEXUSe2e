import { Component, OnInit } from "@angular/core";
import { Message } from "../types";
import { DataService } from "../data/data.service";

@Component({
  selector: "app-message-list",
  templateUrl: "./message-list.component.html",
  styleUrls: ["./message-list.component.scss"],
})
export class MessageListComponent implements OnInit {
  totalMessageCount?: number;
  messages: Message[] = [];

  constructor(private dataService: DataService) {
  }

  async ngOnInit() {
    this.totalMessageCount = await this.dataService.getMessagesCount();
  }

  async loadMessages(pageIndex: number, pageSize: number) {
    this.messages = await this.dataService.getMessages(pageIndex, pageSize);
  }
}
