import { Injectable } from "@angular/core";
import { NexusData } from "../types";
import { SessionService } from "./session.service";
import { DataService } from "./data.service";

@Injectable({
  providedIn: "root",
})
export class RequestHelper {
  constructor(
    private sessionService: SessionService,
    private dataService: DataService
  ) {}

  getUrl(item: NexusData, linkUrlRecipe: string): string {
    const segments = linkUrlRecipe.split("$");
    let url = segments[0];
    for (let i = 1; i < segments.length; i++) {
      if (i % 2 == 0) {
        url += segments[i];
      } else {
        url += this.dataService.getProperty(item, segments[i]);
      }
    }
    return url.toLowerCase();
  }

  getQueryParams(
    queryParamsRecipe: { [s: string]: string },
    item?: NexusData
  ): { [s: string]: string } {
    for (const k in queryParamsRecipe) {
      const segments = queryParamsRecipe[k].split("$");
      let queryParam = segments[0];
      for (let i = 1; i < segments.length; i++) {
        if (i % 2 == 0) {
          queryParam += segments[i];
        } else {
          const paramDate = new Date();
          let minusDate;
          switch (segments[i]) {
            case "todayMinusTransactionActivityTimeframeInWeeks":
              const transactionActivityTimeframeInWeeks = this.sessionService.getEngineTimeVariables()
                ?.transactionActivityTimeframeInWeeks;
              minusDate =
                paramDate.getDate() -
                (transactionActivityTimeframeInWeeks || 0) * 7;
              paramDate.setDate(minusDate);
              queryParam += "" + paramDate.toISOString() + "";
              break;
            case "todayMinusDashboardTimeFrameInDays":
              const dashboardTimeFrameInDays = this.sessionService.getEngineTimeVariables()
                ?.dashboardTimeFrameInDays;
              minusDate = paramDate.getDate() - (dashboardTimeFrameInDays || 0);
              paramDate.setDate(minusDate);
              queryParam += "" + paramDate.toISOString() + "";
              break;
            default:
              if (item) {
                queryParam += this.dataService.getProperty(item, segments[i]);
              }
          }
        }
      }
      queryParamsRecipe[k] = queryParam;
    }
    return queryParamsRecipe;
  }
}
