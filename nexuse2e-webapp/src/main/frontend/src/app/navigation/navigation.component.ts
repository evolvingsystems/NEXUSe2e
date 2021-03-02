import { Component, OnInit } from "@angular/core";
import { NavigationService } from "./navigation.service";
import { CacheService } from "../data/cache.service";
import { Router } from "@angular/router";

@Component({
  selector: "app-navigation",
  templateUrl: "./navigation.component.html",
  styleUrls: ["./navigation.component.scss"],
})
export class NavigationComponent implements OnInit {
  version?: string[];

  constructor(
    public navigationService: NavigationService,
    private cacheService: CacheService,
    public router: Router
  ) {
    this.cacheService
      .get<string[]>("/version")
      .then((version) => (this.version = version));
  }

  ngOnInit() {}

  calculateMargin() {
    return this.version ? 20 + this.version.length * 20 + "px" : 0;
  }
}
