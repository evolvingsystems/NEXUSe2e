import { Component, Inject, OnInit } from "@angular/core";
import { ColumnConfig, ListModalDialog, NexusData } from "../types";

import { MAT_DIALOG_DATA } from "@angular/material/dialog";

@Component({
  selector: "app-simple-table-dialog",
  templateUrl: "./simple-table-dialog.component.html",
  styleUrls: ["./simple-table-dialog.component.scss"],
})
export class SimpleTableDialogComponent implements OnInit {
  items: NexusData[] = [];
  itemType!: string;
  mobileConfig: ColumnConfig[] = [];
  desktopConfig: ColumnConfig[] = [];

  constructor(@Inject(MAT_DIALOG_DATA) public data: ListModalDialog) {
    this.items = data.items;
    this.itemType = data.itemType;
    this.mobileConfig = data.mobileConfig;
    this.desktopConfig = data.desktopConfig;
  }

  ngOnInit(): void {}
}
