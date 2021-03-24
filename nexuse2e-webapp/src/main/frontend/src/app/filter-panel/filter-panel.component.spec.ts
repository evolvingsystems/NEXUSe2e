import { ComponentFixture, TestBed } from "@angular/core/testing";

import { FilterPanelComponent, FilterType } from "./filter-panel.component";
import { By } from "@angular/platform-browser";
import { TranslateModule } from "@ngx-translate/core";

describe("FilterPanelComponent", () => {
  let component: FilterPanelComponent;
  let fixture: ComponentFixture<FilterPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FilterPanelComponent],
      imports: [TranslateModule.forRoot()],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FilterPanelComponent);
    component = fixture.componentInstance;
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should display the number of active filters when collapsed", () => {
    component.activeFilters = [
      { fieldName: "filter1", value: "2" },
      { fieldName: "filter1", value: "1" },
    ];
    fixture.detectChanges();

    const activeFiltersBadge = fixture.debugElement.query(
      By.css(".active-filters-badge")
    );
    expect(activeFiltersBadge.nativeElement.textContent).toBe("2");
  });

  it("should render all filters specified in the config object", () => {
    component.toggleVisibility();
    component.filters = [
      {
        fieldName: "status",
        filterType: FilterType.SELECT,
        allowedValues: ["FAILED", "SENT"],
      },
      {
        fieldName: "conversationId",
        filterType: FilterType.TEXT
      },
      {
        fieldName: "startEndDateRange",
        filterType: FilterType.DATE_TIME_RANGE,
      },
    ];
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelectorAll(".filter").length).toBe(3);
  });

  it("should emit an event including the active filters when the filter button is clicked", () => {
    component.toggleVisibility();
    fixture.detectChanges();
    spyOn(component.filterChange, "emit");
    const button = fixture.nativeElement.querySelector("button");

    button.dispatchEvent(new Event("click"));
    fixture.detectChanges();

    expect(component.filterChange.emit).toHaveBeenCalledWith(
      component.activeFilters
    );
  });

  it("should add 1 to active filter count if active filters contain start and end date in one DateRange object", () => {
    component.activeFilters.push(
      {
        fieldName: "startEndDateRange",
        value: {
          startDate: new Date(),
          endDate: new Date(),
        },
      },
      {
        fieldName: "type",
        value: "Completed",
      }
    );

    expect(component.getNumberOfActivatedFilters()).toEqual(3);
  });

  it("should not add 1 to active filter count if active filters contain only one date in the DateRange object", () => {
    component.activeFilters.push(
      {
        fieldName: "startEndDateRange",
        value: {
          startDate: new Date(),
          endDate: undefined,
        },
      },
      {
        fieldName: "type",
        value: "Completed",
      }
    );

    expect(component.getNumberOfActivatedFilters()).toEqual(2);
  });
});
