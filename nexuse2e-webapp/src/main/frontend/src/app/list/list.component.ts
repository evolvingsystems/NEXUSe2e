import { Component, Input, OnInit } from "@angular/core";
import {
  ColumnConfig,
  ColumnType,
  Choreography,
  Conversation,
  EngineLog,
  isChoreography,
  isConversation,
  isEngineLog,
  isMessage,
  isPartner,
  Message,
  NexusData,
  NotificationItem,
  Separator,
  Partner,
  Certificate,
  isCertificate,
} from "../types";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { SelectionService } from "../services/selection.service";
import { ScreensizeService } from "../services/screensize.service";
import { MatDialog } from "@angular/material/dialog";
import { SimpleTableDialogComponent } from "../simple-table-dialog/simple-table-dialog.component";
import { NotificationComponent } from "../notification/notification.component";
import { MatSnackBar } from "@angular/material/snack-bar";
import { SessionService } from "../services/session.service";

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
  @Input() mobileConfig: ColumnConfig[] = [];
  @Input() desktopConfig: ColumnConfig[] = [];
  @Input() isSelectable?: boolean;
  @Input() showAsSimpleTable?: boolean;
  @Input() showAlwaysAsCard?: boolean;
  @Input() showWithoutBorder?: boolean;
  displayedColumns: string[] = ["select"];
  headerElement?: ColumnConfig;
  simpleTableConfig: ColumnConfig[] = [];

  constructor(
    private selectionService: SelectionService,
    private sessionService: SessionService,
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

  columnType(): typeof ColumnType {
    return ColumnType;
  }

  separator(): typeof Separator {
    return Separator;
  }

  getHeaderElement(): ColumnConfig | undefined {
    return this.mobileConfig.find((e) => e.isHeader);
  }

  getProperty(
    item: NexusData,
    propertyName: string
  ): string | number | undefined {
    if (isMessage(item)) {
      return item[propertyName as keyof Message];
    }
    if (isConversation(item)) {
      return item[propertyName as keyof Conversation];
    }
    if (isEngineLog(item)) {
      return item[propertyName as keyof EngineLog];
    }
    if (isChoreography(item)) {
      return item[propertyName as keyof Choreography];
    }
    if (isPartner(item)) {
      return item[propertyName as keyof Partner];
    }
    if (isCertificate(item)) {
      return item[propertyName as keyof Certificate];
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
    return url.toLowerCase();
  }

  getQueryParams(
    item: NexusData,
    queryParamsRecipe: { [s: string]: string }
  ): { [s: string]: string } {
    for (const k in queryParamsRecipe) {
      const segments = queryParamsRecipe[k].split("$");
      let queryParam = segments[0];
      for (let i = 1; i < segments.length; i++) {
        if (i % 2 == 0) {
          queryParam += segments[i];
        } else {
          switch (segments[i]) {
            case "todayMinusTransactionActivityTimeframeInWeeks":
              const transactionActivityTimeframeInWeeks = this.sessionService.getEngineTimeVariables()
                ?.transactionActivityTimeframeInWeeks;
              const paramDate = new Date();
              const minusDate =
                paramDate.getDate() -
                (transactionActivityTimeframeInWeeks || 0) * 7;
              paramDate.setDate(minusDate);
              queryParam += "" + paramDate.toISOString() + "";
              break;
            default:
              queryParam += this.getProperty(item, segments[i]);
          }
        }
      }
      queryParamsRecipe[k] = queryParam;
    }
    return queryParamsRecipe;
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

  buildAffectedItems(item: NexusData): NexusData[] {
    const affectedItems: NexusData[] = [];
    affectedItems.push(item);
    return affectedItems;
  }
}
