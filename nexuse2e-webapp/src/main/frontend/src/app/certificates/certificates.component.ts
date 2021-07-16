import { Component, OnInit } from "@angular/core";
import { Certificate } from "../types";
import { CERTIFICATES_CONFIG } from "./certificates.config";
import { DataService } from "../services/data.service";
import { ScreensizeService } from "../services/screensize.service";
import { StyleService } from "../services/style.service";

@Component({
  selector: "app-certificates",
  templateUrl: "./certificates.component.html",
  styleUrls: ["./certificates.component.scss"],
})
export class CertificatesComponent implements OnInit {
  certificates: Certificate[] | undefined;
  certificatesConfig = CERTIFICATES_CONFIG;
  loaded = false;
  certificateNextExpiring: Certificate | undefined;

  constructor(
    private dataService: DataService,
    public screenSizeService: ScreensizeService,
    public styleService: StyleService
  ) {}

  async ngOnInit() {
    this.certificates = await this.dataService.getCertificatesForReport();
    this.certificateNextExpiring = this.dataService.sortAndGetNextExpiringCertificate(
      this.certificates
    );

    this.loaded = true;
  }
}
