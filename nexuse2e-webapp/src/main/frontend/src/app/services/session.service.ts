import { Injectable } from "@angular/core";
import { ActiveFilterList, EngineTimeVariables } from "../types";

@Injectable({
  providedIn: "root",
})
export class SessionService {
  getActiveFilters(itemType: string): ActiveFilterList {
    const stringFromSession = sessionStorage.getItem(
      `active-${itemType}-filters`
    );
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

  setActiveFilters(itemType: string, filters: ActiveFilterList) {
    sessionStorage.setItem(
      `active-${itemType}-filters`,
      JSON.stringify(filters)
    );
  }

  getPageSize(itemType: string): number | undefined {
    const pageSizeFromSession = sessionStorage.getItem(`${itemType}-page-size`);
    if (pageSizeFromSession) {
      return +pageSizeFromSession;
    }
    return undefined;
  }

  setPageSize(itemType: string, pageSize: number) {
    sessionStorage.setItem(`${itemType}-page-size`, String(pageSize));
  }

  getPermittedActions(): string[] {
    const permittedActionsFromSession = sessionStorage.getItem(
      "permitted-actions"
    );
    if (permittedActionsFromSession) {
      return JSON.parse(permittedActionsFromSession) as string[];
    }
    return [];
  }

  setPermittedActions(actionKeys: string[]) {
    if (actionKeys) {
      sessionStorage.setItem("permitted-actions", JSON.stringify(actionKeys));
    }
  }

  getEngineTimeVariables(): EngineTimeVariables | undefined {
    const engineTimeVariables = sessionStorage.getItem("engine-time-variables");
    if (engineTimeVariables) {
      return JSON.parse(engineTimeVariables) as EngineTimeVariables;
    }
    return undefined;
  }

  setEngineTimeVariables(engineTimeVariables: EngineTimeVariables) {
    if (engineTimeVariables) {
      sessionStorage.setItem(
        "engine-time-variables",
        JSON.stringify(engineTimeVariables)
      );
    }
  }

  clearSession() {
    sessionStorage.clear();
  }
}
