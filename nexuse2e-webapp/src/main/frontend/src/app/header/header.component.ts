import { Component, OnInit, Output, EventEmitter } from "@angular/core";

@Component({
  selector: "app-header",
  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.scss"],
})
export class HeaderComponent implements OnInit {
  @Output() navToggle = new EventEmitter();
  navOpen = false;

  constructor() {}

  ngOnInit(): void {}

  toggleNav() {
    this.navOpen = !this.navOpen;
    this.navToggle.emit();
  }
}
