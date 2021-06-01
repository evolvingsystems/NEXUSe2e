import { Component, OnInit } from "@angular/core";
import { EngineTimeVariables } from "../types";
import { DataService } from "../services/data.service";

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

  constructor(private dataService: DataService) {}

  async ngOnInit() {
    this.engineTimeVariables = await this.dataService.getEngineTimeVariables();
  }
}
