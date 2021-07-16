import { Injectable } from "@angular/core";
import { Certificate, isCertificate, NexusData } from "../types";

@Injectable({
  providedIn: "root",
})
export class StyleService {
  constructor() {}

  getClassNameForValidityColor(
    item: NexusData,
    columnFieldName: string
  ): string {
    if (isCertificate(item) && columnFieldName === "validity") {
      return this.generateClassNameByValidity(item.validity);
    }
    return "";
  }

  getClassNameForValidityColorFromCertificate(
    certificate: Certificate | undefined
  ): string {
    if (certificate) {
      return this.generateClassNameByValidity(certificate.validity);
    } else {
      return "";
    }
  }

  generateClassNameByValidity(validity: string): string {
    if (validity.toLowerCase().includes("okay")) {
      return "validity-success";
    } else {
      return "validity-error";
    }
  }
}
