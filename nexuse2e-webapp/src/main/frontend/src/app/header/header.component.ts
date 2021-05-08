import { Component, OnInit } from "@angular/core";
import { NavigationService } from "../navigation/navigation.service";
import { DataService } from "../services/data.service";

@Component({
  selector: "app-header",
  templateUrl: "./header.component.html",
  styleUrls: ["./header.component.scss"],
})
export class HeaderComponent implements OnInit {
  machineName?: string;
  version?: string;

  constructor(
    private dataService: DataService,
    public navigationService: NavigationService
  ) {}

  async ngOnInit() {
    this.machineName = await this.dataService.getMachineName();
    this.version = (await this.dataService.getVersion())[0];
  }
}
