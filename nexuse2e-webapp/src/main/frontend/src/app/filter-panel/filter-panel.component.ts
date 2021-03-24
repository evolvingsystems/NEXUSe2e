import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { DateRange } from "../types";

export enum FilterType {
  TEXT,
  SELECT,
  DATE_TIME_RANGE,
}

export interface Filter {
  fieldName: string;
  filterType: FilterType;
  allowedValues?: string[];
  defaultValue?: string | DateRange;
}

export interface ActiveFilter {
  fieldName: string;
  value?: string | DateRange;
}

@Component({
  selector: "app-filter-panel",
  templateUrl: "./filter-panel.component.html",
  styleUrls: ["./filter-panel.component.scss"],
})
export class FilterPanelComponent implements OnInit {
  @Input() filters: Filter[] = [];
  @Output() filterChange: EventEmitter<ActiveFilter[]> = new EventEmitter();
  expanded = false;
  activeFilters: ActiveFilter[] = [];

  constructor() {}

  ngOnInit(): void {
    for (const filter of this.filters) {
      if (filter.defaultValue) {
        this.activeFilters.push({
          fieldName: filter.fieldName,
          value: filter.defaultValue
        });
      }
    }
    this.applyFilters();
  }

  getFilterType() {
    return FilterType;
  }

  toggleVisibility() {
    this.expanded = !this.expanded;
  }

  updateActiveFilters(activeFilter: ActiveFilter) {
    const index = this.activeFilters.findIndex(
      (filter) => filter.fieldName === activeFilter.fieldName
    );
    const existingFilter = this.activeFilters[index];
    if (existingFilter) {
      if (activeFilter.value) {
        existingFilter.value = activeFilter.value;
      } else {
        this.activeFilters.splice(index, 1);
      }
    } else {
      this.activeFilters.push(activeFilter);
    }
  }

  applyFilters() {
    this.filterChange.emit(this.activeFilters);
    this.expanded = false;
  }

  getNumberOfActivatedFilters(): number {
    let activeLength = this.activeFilters.length;
    this.activeFilters.forEach((filter) => {
      if (!(typeof filter.value === "string")) {
        // eslint-disable-next-line
        if (filter.value!.startDate && filter.value!.endDate) {
          activeLength++;
        }
      }
    });
    return activeLength;
  }
}
