import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

export enum FilterType {
  TEXT_FIELD,
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
  collapsed = true;
  activeFilters: ActiveFilter[] = [];

  constructor() {
  }

  ngOnInit(): void {
  }

  getFilterType() {
    return FilterType;
  }

  updateActiveFilters(activeFilter: ActiveFilter) {
    const existingFilter = this.activeFilters.find(filter => filter.fieldName === activeFilter.fieldName);
    if (existingFilter) {
      existingFilter.value = activeFilter.value;
    } else {
      this.activeFilters.push(activeFilter);
    }
  }

}
