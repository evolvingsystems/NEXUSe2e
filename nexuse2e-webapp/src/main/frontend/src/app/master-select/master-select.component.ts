import { Component, Input, OnInit } from '@angular/core';
import { SelectionService } from "../data/selection.service";
import { MatCheckboxChange } from "@angular/material/checkbox";

@Component({
  selector: 'app-master-select',
  templateUrl: './master-select.component.html',
  styleUrls: ['./master-select.component.scss']
})
export class MasterSelectComponent implements OnInit {
  @Input() selectionGroupName!: string;
  @Input() items!: unknown[];

  constructor(private selectionService: SelectionService) {
  }

  ngOnInit(): void {
  }

  allItemsSelected() {
    return this.selectionService.selectionContainsAll(this.selectionGroupName, this.items);
  }

  toggleSelection(event: MatCheckboxChange) {
    if (event.checked) {
      this.selectionService.selectAll(this.selectionGroupName, this.items);
    } else {
      this.selectionService.clearSelection(this.selectionGroupName);
    }
  }
}
