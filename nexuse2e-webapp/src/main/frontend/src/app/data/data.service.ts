import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../environments/environment";

@Injectable({
  providedIn: "root",
})
export class DataService {
  API_URL: string;

  constructor(private http: HttpClient) {
    if (environment.API_URL.length) {
      this.API_URL = environment.API_URL;
    } else {
      this.API_URL = "../api";
    }
  }

  get(path: string) {
    return this.http.get(this.API_URL + path).toPromise();
  }

  // eslint-disable-next-line
  post(path: string, body: any) {
    return this.http.post(this.API_URL + path, body).toPromise();
  }
}
