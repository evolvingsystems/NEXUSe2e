import { Component, Input, OnInit } from '@angular/core';
import { Message } from "../types";
import { SelectionService } from "../data/selection.service";
import { MatCheckboxChange } from "@angular/material/checkbox";

@Component({
  selector: 'app-message-card',
  templateUrl: './message-card.component.html',
  styleUrls: ['./message-card.component.scss']
})
export class MessageCardComponent implements OnInit {
  @Input() message!: Message;

  constructor(private selectionService: SelectionService) {
  }

  ngOnInit(): void {
  }

  toggleSelection(change: MatCheckboxChange) {
    this.selectionService.updateSelection(change.checked, "message", this.message);
  }

  isSelected() {
    return this.selectionService.isSelected("message", this.message);
  }

}
