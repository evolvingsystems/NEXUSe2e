import { Component, Input, OnInit } from "@angular/core";
import { Conversation } from "../types";

@Component({
  selector: "app-conversation-card",
  templateUrl: "./conversation-card.component.html",
  styleUrls: ["./conversation-card.component.scss"],
})
export class ConversationCardComponent implements OnInit {
  @Input() conversation!: Conversation;

  constructor() {}

  ngOnInit(): void {}
}
