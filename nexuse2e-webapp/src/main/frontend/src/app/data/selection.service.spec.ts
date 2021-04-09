import { SelectionService } from "./selection.service";

describe("SelectionService", () => {
  let service: SelectionService;
  beforeEach(() => {
    service = new SelectionService();
  });

  it("should add the first item of a type to the selection", () => {
    const item = { id: "123" };
    service.updateSelection(true, "newSelectionGroup", item);

    expect(service.isSelected("newSelectionGroup", item)).toBeTrue();
  });

  it("should add more than one item to the selection", () => {
    const item1 = { id: "123" };
    const item2 = { id: "234" };
    service.updateSelection(true, "newSelectionGroup", item1);
    service.updateSelection(true, "newSelectionGroup", item2);

    expect(service.isSelected("newSelectionGroup", item1)).toBeTrue();
    expect(service.isSelected("newSelectionGroup", item2)).toBeTrue();
  });

  it("should still work if the same item is added twice", () => {
    const item = { id: "123" };
    service.updateSelection(true, "newSelectionGroup", item);
    service.updateSelection(true, "newSelectionGroup", item);

    expect(service.isSelected("newSelectionGroup", item)).toBeTrue();
  });

  it("should remove an item from the selection", () => {
    const item = { id: "123" };
    service.updateSelection(true, "newSelectionGroup", item);
    service.updateSelection(false, "newSelectionGroup", item);

    expect(service.isSelected("newSelectionGroup", item)).toBeFalse();
  });

  it("should ignore a deselection if the item was not contained in the selection", () => {
    const item = { id: 2121 };
    service.updateSelection(false, "newSelectionGroup", item);

    expect(service.isSelected("newSelectionGroup", item)).toBeFalse();
  });
});
