import { Component, Input, OnInit } from "@angular/core";
import { Conversation, Message, NexusData } from "../types";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { SelectionService } from "../services/selection.service";
import { ScreensizeService } from "../services/screensize.service";

export interface ListConfig {
  fieldName: string;
  additionalFieldName?: string;
  label?: string;
  linkUrlRecipe?: string;
  additionalLinkUrlRecipe?: string;
  isHeader?: boolean;
}

@Component({
  selector: "app-list",
  templateUrl: "./list.component.html",
  styleUrls: ["./list.component.scss"],
})
export class ListComponent implements OnInit {
  @Input() itemType!: string;
  @Input() items: NexusData[] = [];
  @Input() mobileConfig: ListConfig[] = [];
  @Input() desktopConfig: ListConfig[] = [];
  @Input() isSelectable?: boolean;
  displayedColumns: string[] = ["select"];
  headerElement?: ListConfig;

  constructor(
    private selectionService: SelectionService,
    public screenSizeService: ScreensizeService
  ) {}

  ngOnInit(): void {
    this.displayedColumns.push(...this.desktopConfig.map((e) => e.fieldName));
    this.headerElement = this.getHeaderElement();
  }

  getHeaderElement(): ListConfig | undefined {
    return this.mobileConfig.find((e) => e.isHeader);
  }

  getProperty(item: NexusData, propertyName: string) {
    if (this.isMessage(item)) {
      return item[propertyName as keyof Message];
    }
    if (this.isConversation(item)) {
      return item[propertyName as keyof Conversation];
    }
  }

  isMessage(item: NexusData): item is Message {
    return (item as Message).typeName !== undefined;
  }

  isConversation(item: NexusData): item is Conversation {
    return (item as Conversation).currentAction !== undefined;
  }

  getUrl(item: NexusData, linkUrlRecipe: string): string {
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

  toggleSelection(change: MatCheckboxChange, item: NexusData) {
    this.selectionService.updateSelection(change.checked, this.itemType, item);
  }

  isSelected(item: NexusData) {
    return this.selectionService.isSelected(this.itemType, item);
  }
}
