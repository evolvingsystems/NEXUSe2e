import { Component, OnInit } from "@angular/core";
import { Message } from "../types";
import { DataService } from "../data/data.service";

@Component({
  selector: "app-message-list",
  templateUrl: "./message-list.component.html",
  styleUrls: ["./message-list.component.css"],
})
export class MessageListComponent implements OnInit {
  messages?: Message[];

  constructor(private dataService: DataService) {
  }

  async ngOnInit() {
    this.messages = await this.dataService.get<Message[]>("/messages");
  }
}
