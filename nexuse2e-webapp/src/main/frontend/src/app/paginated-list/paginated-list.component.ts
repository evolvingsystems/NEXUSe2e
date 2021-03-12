import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { PageEvent } from "@angular/material/paginator";

@Component({
  selector: 'app-paginated-list',
  templateUrl: './paginated-list.component.html',
  styleUrls: ['./paginated-list.component.scss']
})
export class PaginatedListComponent implements OnInit {
  @Input() totalItemCount!: number;
  @Input() items!: unknown[];
  @Input() itemType!: string;
  @Output() triggerReload: EventEmitter<{ pageIndex: number, pageSize: number }> = new EventEmitter();
  pageSize = 20;
  pageIndex = 0;

  constructor() {
  }

  ngOnInit(): void {
    this.onPageChange({
      pageIndex: this.pageIndex,
      pageSize: this.pageSize,
      length: this.totalItemCount
    });
  }

  onPageChange(pageEvent: PageEvent) {
    this.pageSize = pageEvent.pageSize;
    this.pageIndex = pageEvent.pageIndex;
    this.triggerReload.emit({ pageIndex: this.pageIndex, pageSize: this.pageSize });
  }
}
