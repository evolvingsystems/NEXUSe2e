import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { DateRange } from "../types";

@Component({
  selector: 'app-text-filter',
  templateUrl: './text-filter.component.html',
  styleUrls: ['./text-filter.component.scss']
})
export class TextFilterComponent implements OnInit {
  @Input() fieldName!: string;
  @Output() valueChange: EventEmitter<{ fieldName: string, value?: string | DateRange }> = new EventEmitter();

  constructor() {
  }

  ngOnInit(): void {
  }

  emitValue(event: Event) {
    if (event) {
      const target = event.target as HTMLInputElement;
      this.valueChange.emit({ fieldName: this.fieldName, value: target.value });
    }
  }
}
