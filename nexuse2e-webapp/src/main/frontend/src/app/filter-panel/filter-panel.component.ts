import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { ActiveFilterList, DateRange, Filter, FilterType } from "../types";
import { SessionService } from "../services/session.service";
import { ScreensizeService } from "../services/screensize.service";
import { ActivatedRoute } from "@angular/router";

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
    public sessionService: SessionService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit() {
    const filtersFromRoute = this.extractFiltersFromRouteParams();
    this.activeFilters = Object.keys(filtersFromRoute).length ? filtersFromRoute : this.sessionService.getActiveFilters(this.itemType);
    if (Object.keys(this.activeFilters).length === 0) {
      this.setDefaultValues();
    }
    this.applyFilters();
  }

  extractFiltersFromRouteParams(): ActiveFilterList {
    return Object.assign({}, this.route.snapshot.queryParams);
  }

  getFilterType() {
    return FilterType;
  }

  toggleVisibility() {
    this.expanded = !this.expanded;
  }

  updateActiveFilters(filters: ActiveFilterList) {
    for (const key in filters) {
      if (filters.hasOwnProperty(key)) {
        const value = filters[key];
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
  }

  handleFilterAction() {
    this.applyFilters();
    this.sessionService.setActiveFilters(this.itemType, this.activeFilters);
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
    this.sessionService.setActiveFilters(this.itemType, this.activeFilters);
  }

  setDefaultValues() {
    for (const filter of this.filters) {
      if (filter.defaultValue && !this.activeFilters[filter.fieldName]) {
        this.activeFilters[filter.fieldName] = filter.defaultValue;
      }
    }
  }
}
