import { Component, Input, OnInit } from "@angular/core";
import { Route, Router } from "@angular/router";
import { NavigationService } from "../navigation/navigation.service";

@Component({
  selector: "app-menu-item",
  templateUrl: "./menu-item.component.html",
  styleUrls: ["./menu-item.component.scss"],
})
export class MenuItemComponent implements OnInit {
  @Input() route!: Route;
  @Input() fullPath!: string;
  @Input() level = 1;
  expanded = false;

  constructor(
    private router: Router,
    private navigationService: NavigationService
  ) {}

  ngOnInit(): void {
    this.fullPath += "/" + this.route.path;
  }

  async onClick() {
    this.navigationService.hideNavigation();
    await this.router.navigateByUrl(this.fullPath);
  }

  toggleChildren() {
    this.expanded = !this.expanded;
  }
}
