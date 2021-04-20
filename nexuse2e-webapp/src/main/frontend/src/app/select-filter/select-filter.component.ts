import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatSelectChange } from "@angular/material/select";
import { DateRange } from "../types";

@Component({
  selector: 'app-select-filter',
  templateUrl: './select-filter.component.html',
  styleUrls: ['./select-filter.component.scss']
})
export class SelectFilterComponent implements OnInit {
  @Input() fieldName!: string;
  @Input() allowedValues!: string[];
  @Input() selectedValue?: string;
  @Output() valueChange: EventEmitter<{ fieldName: string, value?: string | DateRange }> = new EventEmitter();

  constructor() {
  }

  ngOnInit(): void {
  }

  emitValue(event: MatSelectChange) {
    this.valueChange.emit({ fieldName: this.fieldName, value: event.value });
  }

}
