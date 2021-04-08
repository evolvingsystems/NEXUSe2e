import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { ActiveFilter } from "../filter-panel/filter-panel.component";
import { DateRange } from "../types";

@Component({
  selector: "app-date-time-range-filter",
  templateUrl: "./date-time-range-filter.component.html",
  styleUrls: ["./date-time-range-filter.component.scss"],
})
export class DateTimeRangeFilterComponent implements OnInit {
  @Input() fieldName!: string;
  @Input() defaultValue?: DateRange;
  @Input() maxStartDate: Date = new Date();
  @Input() startDate: Date | undefined;
  @Input() endDate: Date | undefined;
  @Output() valueChange: EventEmitter<ActiveFilter> = new EventEmitter();

  constructor() {}

  ngOnInit(): void {
    if (this.defaultValue) {
      this.startDate = this.defaultValue.startDate;
      this.endDate = this.defaultValue.endDate;
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
    this.valueChange.emit({
      fieldName: this.fieldName,
      value:
        this.startDate || this.endDate
          ? { startDate: this.startDate, endDate: this.endDate }
          : undefined,
    });
  }
}
