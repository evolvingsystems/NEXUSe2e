import { Component, OnInit } from "@angular/core";
import { NavigationService } from "../services/navigation.service";
import { Router } from "@angular/router";
import { DataService } from "../services/data.service";

@Component({
  selector: "app-navigation",
  templateUrl: "./navigation.component.html",
  styleUrls: ["./navigation.component.scss"],
})
export class NavigationComponent implements OnInit {
  version?: string[];

  constructor(
    public navigationService: NavigationService,
    private dataService: DataService,
    public router: Router
  ) {}

  async ngOnInit() {
    this.version = await this.dataService.getVersion();
  }

  calculateMargin() {
    return this.version ? 20 + this.version.length * 20 + "px" : 0;
  }
}
