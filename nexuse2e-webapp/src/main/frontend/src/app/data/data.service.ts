import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { environment } from "../../environments/environment";
import { Conversation, Message } from "../types";
import { ActiveFilter } from "../filter-panel/filter-panel.component";

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

  private get<T>(path: string, cache = true): Promise<T> {
    if (cache) {
      if (this.cache[path]) {
        return this.cache[path] as Promise<T>;
      }
      this.cache[path] = this.http.get<T>(this.API_URL + path).toPromise();
    }
    return this.http.get<T>(this.API_URL + path).toPromise<T>();
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

  buildFilterParams(activeFilters: ActiveFilter[]) {
    let httpParams = new HttpParams();
    for (const filter of activeFilters) {
      if (filter.value) {
        httpParams = httpParams.append(filter.fieldName, filter.value);
      }
    }
    return httpParams;
  }

  getMessages(pageIndex: number, itemsPerPage: number, activeFilters: ActiveFilter[]): Promise<Message[]> {
    let params = this.buildFilterParams(activeFilters);
    params = params.append("pageIndex", String(pageIndex));
    params = params.append("itemsPerPage", String(itemsPerPage));
    return this.http
      .get<Message[]>(this.API_URL + "/messages", { params: params })
      .toPromise();
  }

  getMessagesCount(activeFilters: ActiveFilter[] = []): Promise<number> {
    return this.http.get<number>(this.API_URL + "/messages-count", {
      params: this.buildFilterParams(activeFilters),
    }).toPromise();
  }

  getConversations(
    pageIndex: number,
    itemsPerPage: number,
    activeFilters: ActiveFilter[]
  ): Promise<Conversation[]> {
    let params = this.buildFilterParams(activeFilters);
    params = params.append("pageIndex", String(pageIndex));
    params = params.append("itemsPerPage", String(itemsPerPage));
    return this.http
      .get<Conversation[]>(this.API_URL + "/conversations", { params: params })
      .toPromise();
  }

  getConversationsCount(activeFilters: ActiveFilter[] = []): Promise<number> {
    return this.http.get<number>(this.API_URL + "/conversations-count", {
      params: this.buildFilterParams(activeFilters),
    }).toPromise();
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
