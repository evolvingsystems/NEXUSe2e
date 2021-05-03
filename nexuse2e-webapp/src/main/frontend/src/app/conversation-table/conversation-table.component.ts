import { Component, Input, OnInit } from "@angular/core";
import { Conversation } from "../types";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { SelectionService } from "../data/selection.service";

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
    "participantId",
    "choreographyId",
    "currentAction",
    "createdDate",
    "status",
    "turnAroundTime",
  ];

  constructor(private selectionService: SelectionService) {
  }

  ngOnInit() {
  }

  toggleSelection(change: MatCheckboxChange, conversation: Conversation) {
    this.selectionService.updateSelection(change.checked, "conversation", conversation);
  }

  isSelected(conversation: Conversation) {
    return this.selectionService.isSelected("conversation", conversation);
  }
}
