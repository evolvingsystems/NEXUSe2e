import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActiveFilter } from "../filter-panel/filter-panel.component";
import { MatSelectChange } from "@angular/material/select";

@Component({
  selector: 'app-select-filter',
  templateUrl: './select-filter.component.html',
  styleUrls: ['./select-filter.component.scss']
})
export class SelectFilterComponent implements OnInit {
  @Input() fieldName!: string;
  @Input() allowedValues!: string[];
  @Input() defaultValue?: string;
  @Output() valueChange: EventEmitter<ActiveFilter> = new EventEmitter();

  constructor() {
  }

  ngOnInit(): void {
  }

  emitValue(event: MatSelectChange) {
    this.valueChange.emit({ fieldName: this.fieldName, value: event.value });
  }

}
