import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../environments/environment";
import { Message } from "../types";

@Injectable({
  providedIn: "root",
})
export class DataService {
  API_URL: string;

  private cache: { [key: string]: unknown } = {};

  constructor(private http: HttpClient) {
    if (environment.API_URL.length) {
      this.API_URL = environment.API_URL;
    } else {
      this.API_URL = "../api";
    }
  }

  private get<T>(path: string, cache: boolean): Promise<T> {
    if (cache) {
      if (this.cache[path]) {
        return this.cache[path] as Promise<T>;
      }
      this.cache[path] = this.http.get<T>(this.API_URL + path).toPromise();
    }
    return this.http.get<T>(this.API_URL + path).toPromise<T>();
  }

  getFullUsername(): Promise<string> {
    return this.get<string>("/full-username", true);
  }

  getLoggedIn() {
    // This response should not be cached because otherwise,
    // the user would not be logged out if their session expired
    return this.get("/logged-in", false);
  }

  getMachineName(): Promise<string> {
    return this.get<string>("/machine-name", true);
  }

  getMessages(pageIndex: number, itemsPerPage: number): Promise<Message[]> {
    const params = {
      "pageIndex": String(pageIndex),
      "itemsPerPage": String(itemsPerPage)
    }
    return this.http.get<Message[]>(this.API_URL + "/messages", { params: params }).toPromise();
  }

  getMessagesCount(): Promise<number> {
    return this.get<number>("/messages-count", false);
  }

  getVersion(): Promise<string[]> {
    return this.get<string[]>("/version", true);
  }

  post(path: string, body: unknown) {
    return this.http.post(this.API_URL + path, body).toPromise();
  }

  postLogin(body: { user: string, password: string }) {
    return this.post("/login", body);
  }

  postLogout() {
    return this.post("/logout", null);
  }
}
