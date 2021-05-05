import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { PageEvent } from "@angular/material/paginator";
import { ScreensizeService } from "../screensize.service";
import { SelectionService } from "../data/selection.service";
import { SessionService } from "../data/session.service";
import { ListConfig } from "../list/list.component";
import { Conversation, Message } from "../types";

@Component({
  selector: "app-paginated-list",
  templateUrl: "./paginated-list.component.html",
  styleUrls: ["./paginated-list.component.scss"],
})
export class PaginatedListComponent implements OnInit {
  private _totalItemCount!: number;
  @Input() items: Message[] | Conversation[] = [];
  @Input() itemType!: string;
  @Input() mobileConfig: ListConfig[] = [];
  @Input() desktopConfig: ListConfig[] = [];
  @Input() isSelectable?: boolean;
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
    private sessionService: SessionService) {
  }

  ngOnInit(): void {}

  @Input()
  set totalItemCount(value: number) {
    this._totalItemCount = value;
    this.pageIndex = 0;
    const savedPageSize = this.sessionService.getPageSize(this.itemType);
    if (savedPageSize) {
      this.pageSize = savedPageSize;
    }
    this.update();
  }

  get totalItemCount() {
    return this._totalItemCount;
  }

  onPageChange(pageEvent: PageEvent) {
    this.pageSize = pageEvent.pageSize;
    this.pageIndex = pageEvent.pageIndex;
    this.update();
  }

  private update() {
    this.triggerReload.emit({
      pageIndex: this.pageIndex,
      pageSize: this.pageSize,
    });
    this.selectionService.clearSelection(this.itemType);
    this.sessionService.setPageSize(this.itemType, this.pageSize);
  }
}
