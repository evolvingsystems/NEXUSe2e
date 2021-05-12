import { Component, Input, OnInit } from "@angular/core";
import { Route, Router } from "@angular/router";
import { NavigationService } from "../services/navigation.service";

@Component({
  selector: "app-menu-item",
  templateUrl: "./menu-item.component.html",
  styleUrls: ["./menu-item.component.scss"],
})
export class MenuItemComponent implements OnInit {
  @Input() route!: Route;
  @Input() fullPath!: string;
  @Input() level = 1;
  expanded = true;

  constructor(
    private router: Router,
    private navigationService: NavigationService
  ) {}

  ngOnInit(): void {
    this.assemblePath();
  }

  assemblePath() {
    this.fullPath += "/" + this.route.path;
  }

  async onClick() {
    if (this.route.children?.length) {
      this.toggleChildren();
    } else {
      this.navigationService.hideNavigation();
      await this.router.navigateByUrl(this.fullPath);
    }
  }

  toggleChildren() {
    this.expanded = !this.expanded;
  }

  isActivatedRoute() {
    const activeUrlParts = this.router.url.split("/");
    return this.route.path === activeUrlParts[activeUrlParts.length - 1];
  }
}
