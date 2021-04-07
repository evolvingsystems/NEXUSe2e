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
    "statusColor",
    "select",
    "messageId",
    "conversationId",
    "partnerId",
    "status",
    "backendStatus",
    "messageType",
    "step",
    "createdDate",
    "turnAroundTime",
  ];

  constructor() {}

  ngOnInit(): void {}
}
