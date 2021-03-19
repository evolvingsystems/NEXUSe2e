import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../environments/environment";
import { Message } from "../types";

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

  getAllMessages(): Promise<Message[]> {
    return this.get<Message[]>("/messages", false);
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

  getVersion(): Promise<string[]> {
    return this.get<string[]>("/version");
  }

  private post(path: string, body: unknown): Promise<void> {
    return this.http.post<void>(this.API_URL + path, body).toPromise();
  }

  postLogin(body: { user: string, password: string }): Promise<void> {
    return this.post("/login", body);
  }

  postLogout(): Promise<void> {
    return this.post("/logout", null);
  }
}
