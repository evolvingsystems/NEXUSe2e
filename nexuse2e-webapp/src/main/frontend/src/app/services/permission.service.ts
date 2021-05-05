import { Injectable } from '@angular/core';
import { DataService } from "../data/data.service";

@Injectable({
  providedIn: 'root'
})
export class PermissionService {

  constructor(private dataService: DataService) {
  }

  async isUserPermitted(actionKey: string): Promise<boolean> {
    try {
      await this.dataService.getUserPermission(actionKey);
    } catch {
      return false;
    }
    return true;
  }
}
