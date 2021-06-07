import { Injectable } from "@angular/core";
import {
  Choreography,
  Conversation,
  EngineLog,
  isChoreography,
  isConversation,
  isEngineLog,
  isMessage,
  isPartner,
  Message,
  NexusData,
  Partner,
} from "../types";
import { SessionService } from "./session.service";

@Injectable({
  providedIn: "root",
})
export class UrlBuilderService {
  constructor(private sessionService: SessionService) {}

  getProperty(
    item: NexusData,
    propertyName: string
  ): string | number | undefined {
    if (isMessage(item)) {
      return item[propertyName as keyof Message];
    }
    if (isConversation(item)) {
      return item[propertyName as keyof Conversation];
    }
    if (isEngineLog(item)) {
      return item[propertyName as keyof EngineLog];
    }
    if (isChoreography(item)) {
      return item[propertyName as keyof Choreography];
    }
    if (isPartner(item)) {
      return item[propertyName as keyof Partner];
    }
  }

  getUrl(item: NexusData, linkUrlRecipe: string): string {
    const segments = linkUrlRecipe.split("$");
    let url = segments[0];
    for (let i = 1; i < segments.length; i++) {
      if (i % 2 == 0) {
        url += segments[i];
      } else {
        url += this.getProperty(item, segments[i]);
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
          switch (segments[i]) {
            case "todayMinusTransactionActivityTimeframeInWeeks":
              const transactionActivityTimeframeInWeeks = this.sessionService.getEngineTimeVariables()
                ?.transactionActivityTimeframeInWeeks;
              const paramDate = new Date();
              const minusDate =
                paramDate.getDate() -
                (transactionActivityTimeframeInWeeks || 0) * 7;
              paramDate.setDate(minusDate);
              queryParam += "" + paramDate.toISOString() + "";
              break;
            default:
              if (item) {
                queryParam += this.getProperty(item, segments[i]);
              }
          }
        }
      }
      queryParamsRecipe[k] = queryParam;
    }
    return queryParamsRecipe;
  }
}
