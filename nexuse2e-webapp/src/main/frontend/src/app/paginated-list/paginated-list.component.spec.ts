import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PaginatedListComponent } from "./paginated-list.component";
import { messages } from "../test-data";
import { MatPaginatorModule } from "@angular/material/paginator";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { By } from "@angular/platform-browser";
import { MessageTableComponent } from "../message-table/message-table.component";

describe("PaginatedListComponent", () => {
  let component: PaginatedListComponent;
  let fixture: ComponentFixture<PaginatedListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatPaginatorModule, BrowserAnimationsModule],
      declarations: [PaginatedListComponent, MessageTableComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PaginatedListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should initially load the first page of items if there are any", async () => {
    spyOn(component.triggerReload, "emit");
    component.totalItemCount = 5;

    await component.ngOnInit();

    expect(component.triggerReload.emit).toHaveBeenCalledWith({
      pageIndex: 0,
      pageSize: component.pageSize,
    });
  });

  it("if mobile is true, it should render one item card for each item on the page", async () => {
    component.items = messages;
    spyOn(component, "isMobile").and.returnValue(true);
    fixture.detectChanges();

    const itemCards = fixture.nativeElement.querySelectorAll(
      ".item-card-wrapper"
    );
    expect(itemCards.length).toBe(component.items.length);
  });

  it("if mobile is false, it should render all items as a table", async () => {
    spyOn(component, "isMobile").and.returnValue(false);
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelectorAll(".items-table-wrapper")
    ).toBeTruthy();
  });

  it("should display the number of results found", () => {
    component.totalItemCount = 30;

    fixture.detectChanges();

    const rangeLabel = fixture.debugElement.query(
      By.css(".mat-paginator-range-label")
    );
    expect(rangeLabel.nativeElement.textContent.trim()).toContain("of 30");
  });
});
