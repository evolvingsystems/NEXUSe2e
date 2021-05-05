import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { DateRange } from "../types";

@Component({
  selector: 'app-text-filter',
  templateUrl: './text-filter.component.html',
  styleUrls: ['./text-filter.component.scss']
})
export class TextFilterComponent implements OnInit {
  @Input() fieldName!: string;
  @Input() allowedValues: string[] = [];
  @Input() defaultValue?: string;
  @Output() valueChange: EventEmitter<{ fieldName: string, value?: string | DateRange }> = new EventEmitter();
  filteredOptions: string[] = [];
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

  checkInput() {
    if (this.selectedValue && this.allowedValues?.length && !this.allowedValues.includes(this.selectedValue)) {
      this.selectedValue = undefined;
    }
    this.emitValue();
  }

  private emitValue() {
    this.valueChange.emit({ fieldName: this.fieldName, value: this.selectedValue });
  }

  filterSuggestions(input?: string) {
    if (input) {
      this.filteredOptions = this.allowedValues.filter(
        (option) => option.toUpperCase().includes(input.toUpperCase())
      );
    } else {
      this.filteredOptions = this.allowedValues;
    }
  }
}
