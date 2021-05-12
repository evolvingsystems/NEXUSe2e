import { Component, OnInit } from "@angular/core";
import { ActiveFilterList, EngineLog } from "../types";

import { DataService } from "../services/data.service";
import { SessionService } from "../services/session.service";
import { activeFilters, defaultPageSize, desktopConfig, filters, mobileConfig } from "./engine-log.config";

@Component({
  selector: "app-engine-log",
  templateUrl: "./engine-log.component.html",
  styleUrls: ["./engine-log.component.scss"],
})
export class EngineLogComponent implements OnInit {
  totalEngineLogCount?: number;
  engineLogs: EngineLog[] = [];
  loaded = false;
  defaultPageSize = defaultPageSize;
  desktopConfig = desktopConfig;
  mobileConfig = mobileConfig;
  filters = filters;
  activeFilters = activeFilters;

  constructor(private dataService: DataService, private sessionService: SessionService) {
  }

  ngOnInit(): void {
  }

  async loadEngineLogs(pageIndex: number, pageSize: number) {
    this.loaded = false;
    this.engineLogs = await this.dataService.getEngineLogs(
      pageIndex,
      pageSize,
      this.activeFilters
    );
    this.loaded = true;
  }

  async refreshEngineLogCount() {
    this.totalEngineLogCount = await this.dataService.getEngineLogsCount(
      this.activeFilters
    );
  }

  filterEngineLogs(activeFilters: ActiveFilterList) {
    this.activeFilters = activeFilters;
    this.refreshEngineLogCount();
    this.loadEngineLogs(0, this.sessionService.getPageSize("engine.log") || this.defaultPageSize);
  }
}
