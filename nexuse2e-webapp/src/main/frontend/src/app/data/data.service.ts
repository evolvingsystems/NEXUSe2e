import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { environment } from "../../environments/environment";
import { ActiveFilterList, Conversation, Message } from "../types";

@Injectable({
  providedIn: "root",
})
export class DataService {
  API_URL: string;

  private cache: { [key: string]: Promise<unknown> } = {};

  constructor(private http: HttpClient) {
    if (environment.API_URL.length) {
      this.API_URL = environment.API_URL;
    } else {
      this.API_URL = "../api";
    }
  }

  private get<T>(path: string, cache = true, params?: HttpParams): Promise<T> {
    if (cache) {
      let key = path;
      if (params) {
        key += JSON.stringify(params);
      }
      if (this.cache[key]) {
        return this.cache[key] as Promise<T>;
      }
      this.cache[key] = this.http.get<T>(this.API_URL + path, { params: params }).toPromise();
    }
    return this.http.get<T>(this.API_URL + path, { params: params }).toPromise<T>();
  }

  getFullUsername(): Promise<string> {
    return this.get<string>("/full-username");
  }

  getLoggedIn() {
    // This response should not be cached because otherwise,
    // the user would not be logged out if their session expired
    return this.get("/logged-in", false);
  }

  getMachineName(): Promise<string> {
    return this.get<string>("/machine-name");
  }

  private static buildFilterParams(activeFilters: ActiveFilterList) {
    let httpParams = new HttpParams();
    for (const fieldName in activeFilters) {
      const value = activeFilters[fieldName];
      if (value) {
        if (typeof value === "string") {
          httpParams = httpParams.append(fieldName, value);
        } else { // type is DateRange
          if (value.startDate) {
            httpParams = httpParams.append(
              "startDate",
              value.startDate.toISOString()
            );
          }
          if (value.endDate) {
            httpParams = httpParams.append(
              "endDate",
              value.endDate.toISOString()
            );
          }
        }
      }
    }
    return httpParams;
  }

  getMessages(pageIndex: number, itemsPerPage: number, activeFilters: ActiveFilterList): Promise<Message[]> {
    let params = DataService.buildFilterParams(activeFilters);
    params = params.append("pageIndex", String(pageIndex));
    params = params.append("itemsPerPage", String(itemsPerPage));
    return this.get<Message[]>("/messages", false, params);
  }

  getMessagesCount(activeFilters: ActiveFilterList = {}): Promise<number> {
    return this.get<number>("/messages/count",
      false, DataService.buildFilterParams(activeFilters));
  }

  getConversations(
    pageIndex: number,
    itemsPerPage: number,
    activeFilters: ActiveFilterList
  ): Promise<Conversation[]> {
    let params = DataService.buildFilterParams(activeFilters);
    params = params.append("pageIndex", String(pageIndex));
    params = params.append("itemsPerPage", String(itemsPerPage));
    return this.get<Conversation[]>("/conversations", false, params);
  }

  getConversationsCount(activeFilters: ActiveFilterList = {}): Promise<number> {
    return this.get<number>("/conversations/count", false, DataService.buildFilterParams(activeFilters));
  }

  getParticipantIds(): Promise<string[]> {
    return this.get<string[]>("/participants/ids");
  }

  getChoreographyIds(): Promise<string[]> {
    return this.get<string[]>("/choreographies/ids");
  }

  getVersion(): Promise<string[]> {
    return this.get<string[]>("/version");
  }

  private post(path: string, body: unknown): Promise<void> {
    return this.http.post<void>(this.API_URL + path, body).toPromise();
  }

  postLogin(body: { user: string; password: string }): Promise<void> {
    return this.post("/login", body);
  }

  postLogout(): Promise<void> {
    return this.post("/logout", null);
  }
}
