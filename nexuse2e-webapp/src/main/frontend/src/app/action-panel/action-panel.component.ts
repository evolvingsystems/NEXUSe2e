import { Component, Input, OnInit } from '@angular/core';
import { Action } from "../types";

@Component({
  selector: 'app-action-panel',
  templateUrl: './action-panel.component.html',
  styleUrls: ['./action-panel.component.scss']
})
export class ActionPanelComponent implements OnInit {
  @Input() itemType!: string;
  @Input() actions!: Action[];

  constructor() {
  }

  ngOnInit(): void {
  }

}
