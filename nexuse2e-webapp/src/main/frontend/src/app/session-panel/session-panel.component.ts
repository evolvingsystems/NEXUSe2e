import { Component, OnInit } from "@angular/core";
import { DataService } from "../data/data.service";
import { Router } from "@angular/router";

@Component({
  selector: "app-session-panel",
  templateUrl: "./session-panel.component.html",
  styleUrls: ["./session-panel.component.scss"],
})
export class SessionPanelComponent implements OnInit {
  constructor(private dataService: DataService, private router: Router) {}

  ngOnInit(): void {}

  async logout() {
    await this.dataService.post("/logout", {});
    await this.router.navigateByUrl("/login");
  }
}
