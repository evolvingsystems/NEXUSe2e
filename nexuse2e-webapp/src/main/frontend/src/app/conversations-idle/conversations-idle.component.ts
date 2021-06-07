import { Component, Input, OnInit } from "@angular/core";
import { IDLE_CONV_CONFIG } from "./conversations-idle.config";
import { Conversation } from "../types";
import { DataService } from "../services/data.service";

@Component({
  selector: "app-conversations-idle",
  templateUrl: "./conversations-idle.component.html",
  styleUrls: ["./conversations-idle.component.scss"],
})
export class ConversationsIdleComponent implements OnInit {
  @Input() dashboardTimeFrameInDays = 0;
  @Input() idleGracePeriodInMinutes = 0;
  idleConversations: Conversation[] | undefined;
  idleConversationsConfig = IDLE_CONV_CONFIG;
  loaded = false;

  constructor(private dataService: DataService) {}

  async ngOnInit() {
    this.idleConversations = await this.dataService.getIdleConversations();
    this.loaded = true;
  }
}
