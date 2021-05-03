import { Component, Input, OnInit } from '@angular/core';
import { Conversation, Message } from "../types";

interface CardConfig {
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
}
