import { Injectable } from "@angular/core";
import { DateRange } from "../types";

@Injectable({
  providedIn: "root",
})
export class SessionService {
  getActiveFilters(itemType: string): { [fieldName: string]: string | DateRange | undefined } {
    const stringFromSession = sessionStorage.getItem(`active-${itemType}-filters`);
    if (stringFromSession) {
      return JSON.parse(stringFromSession, SessionService.jsonDateReviver);
    }
    return {};
  }

  private static jsonDateReviver(key: string, value: string) {
    if (key === "startDate" || key === "endDate") {
      return new Date(value);
    }
    return value;
  }

  setActiveFilters(itemType: string, filters: { [fieldName: string]: string | DateRange | undefined }) {
    sessionStorage.setItem(`active-${itemType}-filters`, JSON.stringify(filters));
  }

  getPageSize(itemType: string): number | undefined {
    const pageSizeFromSession = sessionStorage.getItem(`${itemType}-page-size`);
    if (pageSizeFromSession) {
      console.log(pageSizeFromSession);
      return +pageSizeFromSession;
    }
    return undefined;
  }

  setPageSize(itemType: string, pageSize: number) {
    console.log("setting", pageSize);
    sessionStorage.setItem(`${itemType}-page-size`, String(pageSize));
  }
}
