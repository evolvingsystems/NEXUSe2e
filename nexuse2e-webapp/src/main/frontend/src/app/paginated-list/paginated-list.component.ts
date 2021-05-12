import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { PageEvent } from "@angular/material/paginator";
import { SelectionService } from "../services/selection.service";
import { ListConfig } from "../list/list.component";
import { SessionService } from "../services/session.service";
import { Action, NexusData } from "../types";
import { ScreensizeService } from "../services/screensize.service";
import { PermissionService } from "../services/permission.service";

@Component({
  selector: "app-paginated-list",
  templateUrl: "./paginated-list.component.html",
  styleUrls: ["./paginated-list.component.scss"],
})
export class PaginatedListComponent implements OnInit {
  @Input() totalItemCount!: number;
  @Input() defaultPageSize?: number;
  @Input() items: NexusData[] = [];
  @Input() itemType!: string;
  @Input() mobileConfig: ListConfig[] = [];
  @Input() desktopConfig: ListConfig[] = [];
  @Input() actions?: Action[] = [];
  @Output() triggerReload: EventEmitter<{
    pageIndex: number;
    pageSize: number;
  }> = new EventEmitter();
  pageSize = 20;
  pageIndex = 0;
  innerWidth = window.innerWidth;

  constructor(
    private selectionService: SelectionService,
    public screenSizeService: ScreensizeService,
    private sessionService: SessionService,
    private permissionService: PermissionService
  ) {
  }

  ngOnInit(): void {
    if (this.defaultPageSize) {
      this.pageSize = this.defaultPageSize;
    }
  }

  onPageChange(pageEvent: PageEvent) {
    this.pageSize = pageEvent.pageSize;
    this.pageIndex = pageEvent.pageIndex;
    this.update();
  }

  update() {
    this.triggerReload.emit({
      pageIndex: this.pageIndex,
      pageSize: this.pageSize,
    });
    this.selectionService.clearSelection(this.itemType);
    this.sessionService.setPageSize(this.itemType, this.pageSize);
  }

  isSelectable(): boolean {
    if (this.actions) {
      return this.actions.some(a => this.permissionService.isUserPermitted(a.actionKey));
    }
    return false;
  }
}
