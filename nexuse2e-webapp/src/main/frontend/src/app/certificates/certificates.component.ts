import { Component, OnInit } from "@angular/core";
import { Certificate } from "../types";
import { CERTIFICATES_CONFIG } from "./certificates.config";

@Component({
  selector: "app-certificates",
  templateUrl: "./certificates.component.html",
  styleUrls: ["./certificates.component.scss"],
})
export class CertificatesComponent implements OnInit {
  certificates: Certificate[] | undefined;
  certificatesConfig = CERTIFICATES_CONFIG;
  loaded = false;

  constructor() {}

  ngOnInit(): void {
    this.loaded = true;
  }
}
