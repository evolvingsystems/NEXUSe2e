import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ActiveFilter } from "../filter-panel/filter-panel.component";

@Component({
  selector: 'app-text-filter',
  templateUrl: './text-filter.component.html',
  styleUrls: ['./text-filter.component.scss']
})
export class TextFilterComponent implements OnInit {
  @Input() fieldName!: string;
  @Output() valueChange: EventEmitter<ActiveFilter> = new EventEmitter();

  constructor() {
  }

  ngOnInit(): void {
  }

  emitValue(value: string) {
    this.valueChange.emit({ fieldName: this.fieldName, value: value });
  }
}
