import { Component, EventEmitter, Input, Output } from "@angular/core";
import { ActiveFilterList, DateRange } from "../types";

@Component({
  selector: "app-date-time-range-filter",
  templateUrl: "./date-time-range-filter.component.html",
  styleUrls: ["./date-time-range-filter.component.scss"],
})
export class DateTimeRangeFilterComponent {
  @Input() fieldName!: string;
  @Input() selectedValue?: DateRange;
  @Input() maxStartDate: Date = new Date();
  @Input() startDate: Date | undefined;
  @Input() endDate: Date | undefined;
  @Output() valueChange: EventEmitter<ActiveFilterList> = new EventEmitter();

  constructor() {}

  ngOnChanges(): void {
    if (this.selectedValue) {
      this.startDate = this.selectedValue.startDate;
      this.endDate = this.selectedValue.endDate;
    }
  }

  clearStartDatePicker(): void {
    this.startDate = undefined;
    this.emitValue();
  }

  clearEndDatePicker(): void {
    this.endDate = undefined;
    this.emitValue();
  }

  emitValue() {
    const filters: ActiveFilterList = {};
    filters[this.fieldName] =
      this.startDate || this.endDate
        ? { startDate: this.startDate, endDate: this.endDate }
        : undefined;
    this.valueChange.emit({
      fieldName: this.fieldName,
      value:
        this.startDate || this.endDate
          ? { startDate: this.startDate, endDate: this.endDate }
          : undefined,
    });
  }
}
