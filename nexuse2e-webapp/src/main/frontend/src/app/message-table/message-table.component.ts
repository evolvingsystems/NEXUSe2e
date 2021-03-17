import { Component, Input, OnInit } from "@angular/core";
import { Message } from "../types";

@Component({
  selector: "app-message-table",
  templateUrl: "./message-table.component.html",
  styleUrls: ["./message-table.component.scss"],
})
export class MessageTableComponent implements OnInit {
  @Input() messages!: Message[];
  displayedColumns: string[] = [
    "select",
    "messageId",
    "conversationId",
    "partnerId",
    "status",
    "backendStatus",
    "messageType",
    "createdDate",
    "turnAroundTime",
  ];

  constructor() {}

  ngOnInit(): void {}
}
