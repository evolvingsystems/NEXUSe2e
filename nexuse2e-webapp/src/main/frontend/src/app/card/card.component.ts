import { Component, Input, OnInit } from '@angular/core';
import { Conversation, Message } from "../types";

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
  headerElement?: CardConfig;

  constructor() {
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
}
