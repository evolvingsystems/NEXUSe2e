import { Component, OnInit } from "@angular/core";
import { Certificate } from "../types";
import { CERTIFICATES_CONFIG } from "./certificates.config";
import { DataService } from "../services/data.service";

@Component({
  selector: "app-certificates",
  templateUrl: "./certificates.component.html",
  styleUrls: ["./certificates.component.scss"],
})
export class CertificatesComponent implements OnInit {
  certificates: Certificate[] | undefined;
  certificatesConfig = CERTIFICATES_CONFIG;
  loaded = false;

  constructor(private dataService: DataService) {}

  async ngOnInit() {
    this.certificates = await this.dataService.getCertificatesForReport();
    this.loaded = true;
  }
}
