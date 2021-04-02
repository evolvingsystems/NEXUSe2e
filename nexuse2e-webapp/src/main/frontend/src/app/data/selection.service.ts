import { Injectable } from "@angular/core";

@Injectable({
  providedIn: "root",
})
export class SelectionService {
  private selections: { [key: string]: unknown[] } = {};

  private getIndexOfItem(selectionGroupName: string, item: unknown) {
    return this.selections[selectionGroupName]?.findIndex((e) => e === item);
  }

  private select(selectionGroupName: string, item: unknown) {
    if (!this.selections[selectionGroupName]) {
      this.selections[selectionGroupName] = [];
    }
    this.selections[selectionGroupName].push(item);
  }

  private deselect(selectionGroupName: string, item: unknown) {
    const index = this.getIndexOfItem(selectionGroupName, item);
    if (index != -1) {
      this.selections[selectionGroupName].splice(index, 1);
    }
  }

  updateSelection(checked: boolean, selectionGroupName: string, item: unknown) {
    if (checked) {
      this.select(selectionGroupName, item);
    } else {
      this.deselect(selectionGroupName, item);
    }
  }

  isSelected(selectionGroupName: string, item: unknown) {
    return this.selections[selectionGroupName]?.includes(item);
  }

  selectionContainsAll(selectionGroupName: string, items: unknown[]) {
    const selection = this.selections[selectionGroupName];
    return items?.length > 0 && items.every((item) => selection?.includes(item));
  }

  clearSelection(selectionGroupName: string) {
    this.selections[selectionGroupName] = [];
  }

  selectAll(selectionGroupName: string, items: unknown[]) {
    this.selections[selectionGroupName] = [...items];
  }

  getSelectionSize(selectionGroupName: string) {
    return this.selections[selectionGroupName]?.length;
  }
}
