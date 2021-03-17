import { Component, Input, OnInit } from "@angular/core";
import { Conversation } from "../types";

@Component({
  selector: "app-conversation-table",
  templateUrl: "./conversation-table.component.html",
  styleUrls: ["./conversation-table.component.scss"],
})
export class ConversationTableComponent implements OnInit {
  @Input() conversations!: Conversation[];
  displayedColumns: string[] = [
    "select",
    "conversationId",
    "partnerId",
    "choreographyId",
    "currentAction",
    "createdDate",
    "status",
    "turnAroundTime",
  ];

  constructor() {}

  ngOnInit() {}
}
