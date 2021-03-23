import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

export enum FilterType {
  TEXT,
  SELECT,
  DATE
}

export interface Filter {
  fieldName: string;
  filterType: FilterType,
  allowedValues?: string[];
  defaultValue?: string | Date;
}

export interface ActiveFilter {
  fieldName: string;
  value: string;
}

@Component({
  selector: 'app-filter-panel',
  templateUrl: './filter-panel.component.html',
  styleUrls: ['./filter-panel.component.scss']
})
export class FilterPanelComponent implements OnInit {
  @Input() filters!: Filter[];
  @Output() filterChange: EventEmitter<ActiveFilter[]> = new EventEmitter();
  expanded = false;
  activeFilters: ActiveFilter[] = [];

  constructor() {
  }

  ngOnInit(): void {
  }

  getFilterType() {
    return FilterType;
  }

  toggleVisibility() {
    this.expanded = !this.expanded;
  }

  updateActiveFilters(activeFilter: ActiveFilter) {
    const index = this.activeFilters.findIndex(filter => filter.fieldName === activeFilter.fieldName);
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

}
