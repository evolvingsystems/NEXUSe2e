import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActiveFilter } from "../filter-panel/filter-panel.component";

@Component({
  selector: 'app-text-filter',
  templateUrl: './text-filter.component.html',
  styleUrls: ['./text-filter.component.scss']
})
export class TextFilterComponent implements OnInit {
  @Input() fieldName!: string;
  @Input() allowedValues!: string[];
  @Input() defaultValue?: string;
  @Output() valueChange: EventEmitter<ActiveFilter> = new EventEmitter();
  selectedValue?: string;

  constructor() {
  }

  ngOnInit(): void {
    this.selectedValue = this.defaultValue;
  }

  clear() {
    this.selectedValue = undefined;
    this.emitValue();
  }

  onSelect(value: string) {
    this.selectedValue = value;
    this.emitValue();
  }

  emitValue() {
    this.valueChange.emit({ fieldName: this.fieldName, value: this.selectedValue });
  }
}
