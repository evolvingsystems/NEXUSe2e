import { Component, OnInit } from "@angular/core";
import { CacheService } from "../data/cache.service";
import { NavigationService } from "../navigation/navigation.service";

@Component({
  selector: "app-header",
  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.scss"],
})
export class HeaderComponent implements OnInit {
  machineName?: string;
  version?: string;

  constructor(
    private cacheService: CacheService,
    public navigationService: NavigationService
  ) {
    this.cacheService
      .get<string>("/machine-name")
      .then((name) => (this.machineName = name));
    this.cacheService
      .get<string>("/version")
      .then((version) => (this.version = version));
  }

  ngOnInit(): void {}
}
