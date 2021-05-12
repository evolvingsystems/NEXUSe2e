import { Component, Input, OnInit } from "@angular/core";
import {
  Conversation,
  EngineLog,
  ListConfig,
  Message,
  NexusData,
  NotificationItem,
} from "../types";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { SelectionService } from "../services/selection.service";
import { ScreensizeService } from "../services/screensize.service";
import { MatDialog } from "@angular/material/dialog";
import { SimpleTableDialogComponent } from "../simple-table-dialog/simple-table-dialog.component";
import { NotificationComponent } from "../notification/notification.component";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  selector: "app-list",
  templateUrl: "./list.component.html",
  styleUrls: ["./list.component.scss"],
})
export class ListComponent implements OnInit {
  private readonly longTextThreshold: number = 200;
  private readonly modalDialogMaxWidth: number = 1000;
  @Input() itemType!: string;
  @Input() items: NexusData[] = [];
  @Input() mobileConfig: ListConfig[] = [];
  @Input() desktopConfig: ListConfig[] = [];
  @Input() isSelectable?: boolean;
  @Input() showAsSimpleTable?: boolean;
  @Input() showForDetailPage?: boolean;
  displayedColumns: string[] = ["select"];
  headerElement?: ListConfig;
  simpleTableConfig: ListConfig[] = [];

  constructor(
    private selectionService: SelectionService,
    private _snackBar: MatSnackBar,
    public screenSizeService: ScreensizeService,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    if (this.desktopConfig) {
      this.displayedColumns.push(...this.desktopConfig.map((e) => e.fieldName));
    }
    if (this.showAsSimpleTable) {
      this.simpleTableConfig = this.screenSizeService.isMobile()
        ? this.mobileConfig
        : this.desktopConfig;
    } else {
      this.headerElement = this.getHeaderElement();
    }
  }

  getHeaderElement(): ListConfig | undefined {
    return this.mobileConfig.find((e) => e.isHeader);
  }

  getProperty(
    item: NexusData,
    propertyName: string
  ): string | number | undefined {
    if (this.isMessage(item)) {
      return item[propertyName as keyof Message];
    }
    if (this.isConversation(item)) {
      return item[propertyName as keyof Conversation];
    }
    if (this.isEngineLog(item)) {
      return item[propertyName as keyof EngineLog];
    }
  }

  getTrimmedProperty(item: NexusData, fieldName: string) {
    const property = this.getProperty(item, fieldName);

    if (typeof property === "string") {
      return this.isLongText(property)
        ? property.substr(0, this.longTextThreshold) + "..."
        : property;
    } else {
      return property;
    }
  }

  isLongText(property?: string | number): boolean {
    return (
      typeof property === "string" && property.length > this.longTextThreshold
    );
  }

  isMessage(item: NexusData): item is Message {
    return (item as Message).typeName !== undefined;
  }

  isConversation(item: NexusData): item is Conversation {
    return (item as Conversation).currentAction !== undefined;
  }

  isEngineLog(item: NexusData): item is EngineLog {
    return (item as EngineLog).methodName !== undefined;
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

  showMore(item: NexusData) {
    this.dialog.open(SimpleTableDialogComponent, {
      maxWidth: this.modalDialogMaxWidth,
      data: {
        items: [item],
        itemType: this.itemType,
        mobileConfig: this.mobileConfig,
        desktopConfig: this.desktopConfig,
      },
    });
  }

  showCopiedNotification() {
    this._snackBar.openFromComponent(NotificationComponent, {
      duration: 5000,
      data: {
        snackType: "info",
        textLabel: "copiedToClipboard",
      } as NotificationItem,
    });
  }
}
