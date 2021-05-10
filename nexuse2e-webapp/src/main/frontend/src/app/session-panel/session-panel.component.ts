import { Component, OnInit } from "@angular/core";
import { DataService } from "../services/data.service";
import { Router } from "@angular/router";
import { SessionService } from "../services/session.service";

@Component({
  selector: "app-session-panel",
  templateUrl: "./session-panel.component.html",
  styleUrls: ["./session-panel.component.scss"],
})
export class SessionPanelComponent implements OnInit {
  username?: string;

  constructor(private dataService: DataService, private sessionService: SessionService, private router: Router) {}

  async ngOnInit() {
    this.username = await this.dataService.getFullUsername();
  }

  async logout() {
    await this.dataService.postLogout();
    this.dataService.clearCache();
    this.sessionService.clearSession();
    await this.router.navigateByUrl("/login");
  }
}
