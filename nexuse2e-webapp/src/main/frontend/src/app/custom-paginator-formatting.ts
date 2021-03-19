import { MatPaginatorIntl } from "@angular/material/paginator";
import { TranslateService } from "@ngx-translate/core";
import { Injectable } from "@angular/core";

@Injectable()
export class CustomPaginatorFormatting extends MatPaginatorIntl {
  showingResultsLabel;
  itemsPerPageLabel;
  nextPageLabel;
  previousPageLabel;
  ofLabel;
  noResultsFoundLabel;

  constructor(private translate: TranslateService) {
    super();
    this.showingResultsLabel = this.translate.instant("labels.showingResults");
    this.itemsPerPageLabel = this.translate.instant("labels.itemsPerPage");
    this.nextPageLabel = this.translate.instant("labels.nextPage");
    this.previousPageLabel = this.translate.instant("labels.previousPage");
    this.ofLabel = this.translate.instant("labels.of");
    this.noResultsFoundLabel = this.translate.instant("labels.noResultsFound");
  }

  getRangeLabel = (page: number, pageSize: number, length: number) => {
    if (length === 0 || pageSize === 0) {
      return this.noResultsFoundLabel;
    }
    length = Math.max(length, 0);
    const startIndex = page * pageSize;
    const endIndex = startIndex < length ?
      Math.min(startIndex + pageSize, length) :
      startIndex + pageSize;
    return `${this.showingResultsLabel} ${startIndex + 1} - ${endIndex} ${this.ofLabel} ${length}`;
  };
}
