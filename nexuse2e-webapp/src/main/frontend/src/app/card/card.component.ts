import { Component, Input, OnInit } from '@angular/core';
import { Conversation, Message } from "../types";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { SelectionService } from "../data/selection.service";

export interface CardConfig {
  fieldName: string;
  linkUrl?: string;
  isHeader?: boolean;
}

@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.scss']
})
export class CardComponent implements OnInit {
  @Input() itemType!: string;
  @Input() config: CardConfig[] = [];
  @Input() item!: Message | Conversation;
  @Input() isSelectable?: boolean;
  headerElement?: CardConfig;

  constructor(private selectionService: SelectionService) {
  }

  ngOnInit(): void {
    this.headerElement = this.getHeaderElement();
  }

  getHeaderElement(): CardConfig | undefined {
    return this.config.find(e => e.isHeader);
  }

  getProperty(propertyName: string) {
    switch (this.itemType) {
      case "message":
        const message = this.item as Message;
        return message[propertyName as keyof Message];
      case "conversation":
        const conversation = this.item as Conversation;
        return conversation[propertyName as keyof Conversation];
    }
  }

  toggleSelection(change: MatCheckboxChange) {
    this.selectionService.updateSelection(change.checked, this.itemType, this.item);
  }

  isSelected() {
    return this.selectionService.isSelected(this.itemType, this.item);
  }
}
