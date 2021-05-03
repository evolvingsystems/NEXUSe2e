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
  displayedColumns: string[] = [];

  constructor(private selectionService: SelectionService) {}

  ngOnInit(): void {
    if (this.isSelectable) {
      this.displayedColumns.push("select");
    }

    this.displayedColumns.push(...this.config.map((e) => e.fieldName));
  }

  toggleSelection(change: MatCheckboxChange, item: Message | Conversation) {
    this.selectionService.updateSelection(change.checked, this.itemType, item);
  }

  isSelected(item: Message | Conversation) {
    return this.selectionService.isSelected(this.itemType, item);
  }
}
