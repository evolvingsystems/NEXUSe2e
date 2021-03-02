import { Component, OnInit } from "@angular/core";
import { NavigationService } from "./navigation.service";

@Component({
  selector: "app-navigation",
  templateUrl: "./navigation.component.html",
  styleUrls: ["./navigation.component.scss"],
})
export class NavigationComponent implements OnInit {
  constructor(public navigationService: NavigationService) {}

  ngOnInit(): void {}
}
