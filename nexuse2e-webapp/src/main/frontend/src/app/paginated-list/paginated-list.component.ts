import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { PageEvent } from "@angular/material/paginator";

@Component({
  selector: 'app-paginated-list',
  templateUrl: './paginated-list.component.html',
  styleUrls: ['./paginated-list.component.scss']
})
export class PaginatedListComponent implements OnInit {
  private _totalItemCount!: number;
  @Input() items!: unknown[];
  @Input() itemType!: string;
  @Output() triggerReload: EventEmitter<{ pageIndex: number, pageSize: number }> = new EventEmitter();
  pageSize = 20;
  pageIndex = 0;

  constructor() {
  }

  ngOnInit(): void {
  }

  @Input()
  set totalItemCount(value: number) {
    this._totalItemCount = value;
    this.onPageChange({
      pageIndex: 0,
      pageSize: this.pageSize,
      length: this.totalItemCount
    });
  }

  get totalItemCount() {
    return this._totalItemCount;
  }

  onPageChange(pageEvent: PageEvent) {
    this.pageSize = pageEvent.pageSize;
    this.pageIndex = pageEvent.pageIndex;
    this.triggerReload.emit({ pageIndex: this.pageIndex, pageSize: this.pageSize });
  }
}
