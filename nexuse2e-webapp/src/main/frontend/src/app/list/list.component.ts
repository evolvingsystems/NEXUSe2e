import { Component, Input, OnInit } from "@angular/core";
import {
  ColumnConfig,
  ColumnType,
  isCertificate,
  NexusData,
  NotificationItem,
  Separator,
} from "../types";
import { MatCheckboxChange } from "@angular/material/checkbox";
import { SelectionService } from "../services/selection.service";
import { ScreensizeService } from "../services/screensize.service";
import { MatDialog } from "@angular/material/dialog";
import { SimpleTableDialogComponent } from "../simple-table-dialog/simple-table-dialog.component";
import { NotificationComponent } from "../notification/notification.component";
import { MatSnackBar } from "@angular/material/snack-bar";
import { SessionService } from "../services/session.service";
import { RequestHelper } from "../services/request-helper";
import { DataService } from "../services/data.service";

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
    public requestHelper: RequestHelper,
    public screenSizeService: ScreensizeService,
    public dataService: DataService,
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

  getTrimmedProperty(item: NexusData, fieldName: string) {
    const property = this.dataService.getProperty(item, fieldName);

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

  getClassNameForValidityColor(
    item: NexusData,
    columnFieldName: string
  ): string {
    if (isCertificate(item) && columnFieldName === "validity") {
      if (item.validity.toLowerCase().includes("okay")) {
        return "success";
      } else {
        return "error";
      }
    }
    return "";
  }
}
