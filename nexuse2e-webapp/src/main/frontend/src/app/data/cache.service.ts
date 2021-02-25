import { Injectable } from "@angular/core";
import { DataService } from "./data.service";

@Injectable({
  providedIn: "root",
})
export class CacheService {
  cache: { [key: string]: unknown } = {};

  constructor(private dataService: DataService) {}

  get<T>(key: string): Promise<T> {
    if (!this.cache[key]) {
      this.cache[key] = this.dataService.get<T>(key);
    }
    return this.cache[key] as Promise<T>;
  }
}
