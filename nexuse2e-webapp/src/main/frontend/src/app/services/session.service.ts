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

  setEngineTimeVariables(config: EngineTimeVariables) {
    sessionStorage.setItem("engine-config", JSON.stringify(config));
  }

  getEngineTimeVariables(): EngineTimeVariables | undefined {
    const engineConfigFromSession = sessionStorage.getItem("engine-config");
    if (engineConfigFromSession) {
      return JSON.parse(engineConfigFromSession) as EngineTimeVariables;
    }
    return undefined;
  }

  clearSession() {
    sessionStorage.clear();
  }
}
