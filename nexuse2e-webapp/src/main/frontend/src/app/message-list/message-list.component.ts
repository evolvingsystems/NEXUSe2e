import { Component, OnInit } from "@angular/core";
import { Message } from "../types";
import { DataService } from "../data/data.service";
import { PageEvent } from "@angular/material/paginator";

@Component({
  selector: "app-message-list",
  templateUrl: "./message-list.component.html",
  styleUrls: ["./message-list.component.scss"],
})
export class MessageListComponent implements OnInit {
  totalMessageCount?: number;
  messages?: Message[];
  pageSize = 20;
  pageIndex = 0;

  constructor(private dataService: DataService) {
  }

  async ngOnInit() {
    this.totalMessageCount = await this.dataService.getMessagesCount();
    if (this.totalMessageCount > 0) {
      await this.loadPage(this.pageIndex);
    }
  }

  async onPageChange(pageEvent: PageEvent) {
    this.pageIndex = pageEvent.pageIndex;
    await this.loadPage(this.pageIndex);
  }

  async loadPage(pageIndex: number) {
    this.messages = await this.dataService.getMessages(pageIndex, this.pageSize);
  }
}
