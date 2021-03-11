import { Injectable } from "@angular/core";

@Injectable({
  providedIn: "root",
})
export class NavigationService {
  private expanded = false;

  constructor() {}

  isExpanded() {
    return this.expanded;
  }

  toggleNavigation() {
    this.expanded = !this.expanded;
  }

  hideNavigation() {
    this.expanded = false;
  }
}
