import { Injectable } from "@angular/core";
import { DataService } from "./data.service";
import { SelectionService } from "./selection.service";
import { Message } from "../types";

@Injectable({
  providedIn: "root",
})
export class ActionService {
  constructor(private dataService: DataService, private selectionService: SelectionService) {
  }

  async stopMessages() {
    const messages = this.selectionService.getSelectedItems("message");
    try {
      this.dataService.stopMessages(messages.map(m => (m as Message).messageId));
    } catch {
      console.log("stopping failed");
      // TODO Snackbar error
    }
  }
}
