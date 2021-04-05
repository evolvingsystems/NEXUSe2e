import { SelectionService } from "./selection.service";

describe("SelectionService", () => {
  let service: SelectionService;
  beforeEach(() => {
    service = new SelectionService();
  });

  it("should add the first item of a type to the selection", () => {
    service.updateSelection(true, "newSelectionGroup", { id: "123" });

    expect(service.getSelectionSize("newSelectionGroup")).toEqual(1);
  });

  it("should add more than one item to the selection", () => {
    service.updateSelection(true, "newSelectionGroup", { id: "123" });
    service.updateSelection(true, "newSelectionGroup", { id: "234" });

    expect(service.getSelectionSize("newSelectionGroup")).toEqual(2);
  });

  it("should not add an item to the selection if it's already contained", () => {
    const item = { id: "123" };
    service.updateSelection(true, "newSelectionGroup", item);
    service.updateSelection(true, "newSelectionGroup", item);

    expect(service.getSelectionSize("newSelectionGroup")).toEqual(1);
  });

  it("should remove an item from the selection", () => {
    const item = { id: "123" };
    service.updateSelection(true, "newSelectionGroup", item);
    service.updateSelection(false, "newSelectionGroup", item);

    expect(service.getSelectionSize("newSelectionGroup")).toEqual(0);
  });

  it("should ignore a deselection if the item was not contained in the selection", () => {
    service.updateSelection(false, "newSelectionGroup", { id: 2121 });

    expect().nothing();
  });
});
