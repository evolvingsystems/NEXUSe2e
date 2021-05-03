import { Component, Input, OnInit } from "@angular/core";
import { Conversation, Message } from "../types";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { SelectionService } from "../data/selection.service";

export interface ListConfig {
  fieldName: string;
  linkUrlRecipe?: string;
  isHeader?: boolean;
}

@Component({
  selector: "app-table",
  templateUrl: "./table.component.html",
  styleUrls: ["./table.component.scss"],
})
export class TableComponent implements OnInit {
  @Input() itemType!: string;
  @Input() items: Message[] | Conversation[] = [];
  @Input() config: ListConfig[] = [];
  @Input() isSelectable?: boolean;
  displayedColumns: string[] = ["select"];

  constructor(private selectionService: SelectionService) {}

  ngOnInit(): void {
    this.displayedColumns.push(...this.config.map((e) => e.fieldName));
  }

  getProperty(item: Message | Conversation, propertyName: string) {
    switch (this.itemType) {
      case "message":
        const message = item as Message;
        return message[propertyName as keyof Message];
      case "conversation":
        const conversation = item as Conversation;
        return conversation[propertyName as keyof Conversation];
    }
  }

  getUrl(item: Message | Conversation, linkUrlRecipe: string): string {
    const segments = linkUrlRecipe.split("$");
    let url = segments[0];
    for (let i = 1; i < segments.length; i++) {
      if (i % 2 == 0) {
        url += segments[i];
      } else {
        url += this.getProperty(item, segments[i]);
      }
    }
    return url;
  }

  toggleSelection(change: MatCheckboxChange, item: Message | Conversation) {
    this.selectionService.updateSelection(change.checked, this.itemType, item);
  }

  isSelected(item: Message | Conversation) {
    return this.selectionService.isSelected(this.itemType, item);
  }
}
