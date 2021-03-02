import { Component, EventEmitter, OnInit, Output } from "@angular/core";
import { CacheService } from "../data/cache.service";

@Component({
  selector: "app-header",
  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.scss"],
})
export class HeaderComponent implements OnInit {
  @Output() navToggle = new EventEmitter();
  navOpen = false;
  machineName?: string;
  version?: string;

  constructor(private cacheService: CacheService) {
    this.cacheService
      .get<string>("/machine-name")
      .then((name) => (this.machineName = name));
    this.cacheService
      .get<string>("/version")
      .then((version) => (this.version = version));
  }

  ngOnInit(): void {}

  toggleNav() {
    this.navOpen = !this.navOpen;
    this.navToggle.emit();
  }
}
