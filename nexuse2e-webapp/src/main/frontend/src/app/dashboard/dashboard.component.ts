import { Component, OnInit } from "@angular/core";
import { EngineTimeVariables } from "../types";
import { DataService } from "../services/data.service";
import { SessionService } from "../services/session.service";

@Component({
  selector: "app-dashboard",
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.scss"],
})
export class DashboardComponent implements OnInit {
  engineTimeVariables: EngineTimeVariables = {
    dashboardTimeFrameInDays: 0,
    transactionActivityTimeframeInWeeks: 0,
    idleGracePeriodInMinutes: 0,
  };

  constructor(
    private dataService: DataService,
    private sessionService: SessionService
  ) {}

  async ngOnInit() {
    this.engineTimeVariables = await this.dataService.getEngineTimeVariables();
    this.sessionService.setEngineTimeVariables(this.engineTimeVariables);
  }
}
