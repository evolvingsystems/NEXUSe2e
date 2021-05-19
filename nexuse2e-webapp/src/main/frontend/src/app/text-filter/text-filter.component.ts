import { Component, EventEmitter, Input, Output } from "@angular/core";
import { ActiveFilterList } from "../types";

@Component({
  selector: "app-text-filter",
  templateUrl: "./text-filter.component.html",
  styles: [],
})
export class TextFilterComponent {
  @Input() fieldName!: string;
  @Input() allowedValues?: string[];
  @Input() defaultValue?: string;
  @Input() selectedValue?: string;
  @Output() valueChange: EventEmitter<ActiveFilterList> = new EventEmitter();
  filteredOptions: string[] = [];
  currentValue?: string;

  constructor() {}

  ngOnChanges(): void {
    this.currentValue = this.selectedValue;
  }

  clear() {
    this.currentValue = undefined;
    this.emitValue();
  }

  onSelect(value: string) {
    this.currentValue = value;
    this.emitValue();
  }

  checkInput() {
    if (
      this.currentValue &&
      this.allowedValues?.length &&
      !this.allowedValues.includes(this.currentValue)
    ) {
      this.currentValue = undefined;
    }
    this.emitValue();
  }

  private emitValue() {
    const filters: ActiveFilterList = {};
    filters[this.fieldName] = this.currentValue;
    this.valueChange.emit(filters);
  }

  filterSuggestions(input?: string) {
    if (this.allowedValues) {
      if (input) {
        this.filteredOptions = this.allowedValues.filter((option) =>
          option.toUpperCase().includes(input.toUpperCase())
        );
      } else {
        this.filteredOptions = this.allowedValues;
      }
    }
  }
}
