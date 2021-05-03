import { Component, Input, OnInit } from "@angular/core";
import { Message } from "../types";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { SelectionService } from "../data/selection.service";

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
    "participantId",
    "status",
    "backendStatus",
    "messageType",
    "createdDate",
    "turnAroundTime",
  ];

  constructor(private selectionService: SelectionService) {
  }

  ngOnInit(): void {
  }

  toggleSelection(change: MatCheckboxChange, message: Message) {
    this.selectionService.updateSelection(change.checked, "message", message);
  }

  isSelected(message: Message) {
    return this.selectionService.isSelected("message", message);
  }
}
