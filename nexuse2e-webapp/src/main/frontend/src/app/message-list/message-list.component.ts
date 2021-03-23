import { Component, OnInit } from "@angular/core";
import { Message } from "../types";
import { DataService } from "../data/data.service";
import { ActiveFilter, FilterType } from "../filter-panel/filter-panel.component";

@Component({
  selector: "app-message-list",
  templateUrl: "./message-list.component.html",
  styleUrls: ["./message-list.component.scss"],
})
export class MessageListComponent implements OnInit {
  totalMessageCount?: number;
  messages: Message[] = [];
  filters = [
    {
      fieldName: "status",
      filterType: FilterType.SELECT,
      allowedValues: ["FAILED", "SENT", "UNKNOWN", "RETRYING", "QUEUED", "STOPPED"]
    },
    {
      fieldName: "type",
      filterType: FilterType.SELECT,
      allowedValues: ["NORMAL", "ACKNOWLEDGEMENT", "ERROR"]
    },
    {
      fieldName: "conversationId",
      filterType: FilterType.TEXT
    },
    {
      fieldName: "startDate",
      filterType: FilterType.DATE
    }
  ];
  activeFilters: ActiveFilter[] = [];

  constructor(private dataService: DataService) {
  }

  async ngOnInit() {
    this.refreshMessageCount();
  }

  async loadMessages(pageIndex: number, pageSize: number) {
    this.messages = await this.dataService.getMessages(pageIndex, pageSize, this.activeFilters);
  }

  async refreshMessageCount() {
    this.totalMessageCount = await this.dataService.getMessagesCount(this.activeFilters);
  }

  filterMessages(activeFilters: ActiveFilter[]) {
    this.activeFilters = activeFilters;
    this.refreshMessageCount();
  }
}
