import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FilterPanelComponent, FilterType } from './filter-panel.component';
import { By } from "@angular/platform-browser";
import { TranslateModule } from "@ngx-translate/core";

describe('FilterPanelComponent', () => {
  let component: FilterPanelComponent;
  let fixture: ComponentFixture<FilterPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FilterPanelComponent],
      imports: [TranslateModule.forRoot()]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FilterPanelComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  xit("should start in a collapsed state", () => {
    fixture.detectChanges();
    expect(component.collapsed).toBeTruthy();
    expect(fixture.nativeElement.querySelector(".expanded")).toBeFalsy();
    expect(fixture.nativeElement.querySelector(".collapsed")).toBeTruthy();
  });

  xit("should display the number of active filters when minimized", () => {
    component.activeFilters = [
      { fieldName: "filter1", value: "2" },
      { fieldName: "filter1", value: "1" }
    ];
    fixture.detectChanges();

    const activeFiltersBadge = fixture.debugElement.query(By.css(".active-filters-badge"));
    expect(activeFiltersBadge.nativeElement.textContent).toBe("2");
  });

  it("should render all filters specified in the config object", () => {
    component.filters = [
      {
        fieldName: "status",
        filterType: FilterType.SELECT,
        allowedValues: ["FAILED", "SENT"]
      },
      {
        fieldName: "conversationId",
        filterType: FilterType.TEXT_FIELD
      },
      {
        fieldName: "startDate",
        filterType: FilterType.DATE
      }
    ];
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll(".filter").length).toBe(3);
  });

  it("should allow the user to clear all filters");

  it("should emit an event when a filter is applied");

  it("should emit a filter object containing the filtered fields and their values when a filter is applied");
});
