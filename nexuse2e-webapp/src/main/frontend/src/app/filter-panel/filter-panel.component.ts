import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { ActiveFilterList, DateRange } from "../types";
import { SessionService } from "../data/session.service";
import { ScreensizeService } from "../screensize.service";

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

@Component({
  selector: "app-filter-panel",
  templateUrl: "./filter-panel.component.html",
  styleUrls: ["./filter-panel.component.scss"],
})
export class FilterPanelComponent implements OnInit {
  @Input() filters: Filter[] = [];
  @Input() itemType!: string;
  @Output() filterChange: EventEmitter<ActiveFilterList> = new EventEmitter();
  expanded = false;
  activeFilters: ActiveFilterList = {};
  innerWidth = window.innerWidth;

  constructor(
    public screenSizeService: ScreensizeService,
    public sessionService: SessionService
  ) {}

  ngOnInit(): void {
    this.activeFilters = this.sessionService.getActiveFilters(this.itemType);
    if (Object.keys(this.activeFilters).length === 0) {
      this.setDefaultValues();
    }
    this.applyFilters();
  }

  getFilterType() {
    return FilterType;
  }

  toggleVisibility() {
    this.expanded = !this.expanded;
  }

  updateActiveFilters(filters: ActiveFilterList) {
    for (let key in filters) {
      if (filters.hasOwnProperty(key)) {
        let value = filters[key];
        if (value) {
          this.activeFilters[key] = value;
        } else {
          delete this.activeFilters[key];
        }
      }
    }
  }

  applyFilters() {
    this.filterChange.emit(this.activeFilters);
    this.sessionService.setActiveFilters(this.itemType, this.activeFilters);
  }

  handleFilterAction() {
    this.applyFilters();
    this.expanded = false;
  }

  getNumberOfActivatedFilters(): number {
    let activeLength = Object.keys(this.activeFilters).length;
    for (const value of Object.values(this.activeFilters)) {
      if (this.isDateRange(value)) {
        if (value?.startDate && value?.endDate) {
          activeLength++;
        }
      }
    }
    return activeLength;
  }

  isDateRange(item: unknown): item is DateRange {
    return (item as DateRange).startDate !== undefined;
  }

  resetFiltersAndSetDefaults() {
    this.activeFilters = {};
    this.setDefaultValues();
    this.applyFilters();
  }

  setDefaultValues() {
    for (const filter of this.filters) {
      if (filter.defaultValue && !this.activeFilters[filter.fieldName]) {
        this.activeFilters[filter.fieldName] = filter.defaultValue;
      }
    }
  }
}
