import { Injectable } from "@angular/core";
import { DataService } from "./data.service";
import { SelectionService } from "./selection.service";
import { Message } from "../types";
import { MatSnackBar } from "@angular/material/snack-bar";

@Injectable({
  providedIn: "root",
})
export class ActionService {
  constructor(
    private dataService: DataService,
    private selectionService: SelectionService,
    private _snackBar: MatSnackBar
  ) {}

  async stopMessages() {
    const messages = this.selectionService.getSelectedItems("message");
    try {
      await this.dataService.stopMessages(
        messages.map((m) => (m as Message).messageId)
      );
    } catch {
      this._snackBar.open(
        "An error occurred while trying to change message status",
        undefined,
        {
          duration: 5000,
        }
      );
    }
  }

  async deleteConversations() {
    //const conversations = this.selectionService.getSelectedItems("conversation");
    try {
      //await this.dataService.deleteConversations(//TODO);
    } catch {
      this._snackBar.open(
        "An error occurred while trying to change conversation status",
        undefined,
        {
          duration: 5000,
        }
      );
    }
  }
}
