import { Component, OnInit } from "@angular/core";
import { ActiveFilterList, EngineLog } from "../types";
import { FilterType } from "../filter-panel/filter-panel.component";
import { ListConfig } from "../list/list.component";
import { DataService } from "../services/data.service";

@Component({
  selector: "app-engine-log",
  templateUrl: "./engine-log.component.html",
  styleUrls: ["./engine-log.component.scss"],
})
export class EngineLogComponent implements OnInit {
  totalEngineLogCount?: number;
  engineLogs: EngineLog[] = [];
  loaded = false;
  static readonly START_DATE_DEFAULT: Date = new Date(
    new Date().setHours(0, 0, 0, 0)
  );
  static readonly END_DATE_DEFAULT: Date = new Date(
    new Date().setHours(24, 0, 0, 0)
  );

  filters = [
    {
      fieldName: "startEndDateRange",
      filterType: FilterType.DATE_TIME_RANGE,
      defaultValue: {
        startDate: EngineLogComponent.START_DATE_DEFAULT,
        endDate: EngineLogComponent.END_DATE_DEFAULT,
      },
    },
    {
      fieldName: "severity",
      filterType: FilterType.SELECT,
      allowedValues: ["ERROR", "WARN", "INFO", "DEBUG", "TRACE"],
    },
    {
      fieldName: "messageText",
      filterType: FilterType.TEXT,
    },
  ];
  activeFilters: ActiveFilterList = {};

  mobileConfig: ListConfig[] = [
    { fieldName: "createdDate" },
    { fieldName: "description" },
  ];

  desktopConfig: ListConfig[] = [
    {
      fieldName: "createdDate",
    },
    {
      fieldName: "description",
    },
    {
      fieldName: "className",
    },
    {
      fieldName: "methodName",
    },
  ];

  constructor(private dataService: DataService) {}

  ngOnInit(): void {}

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
  }
}
