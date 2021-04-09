import { ComponentFixture, TestBed } from "@angular/core/testing";

import { FilterPanelComponent, FilterType } from "./filter-panel.component";
import { By } from "@angular/platform-browser";
import { TranslateModule } from "@ngx-translate/core";
import { MatIconModule } from "@angular/material/icon";
import { SelectFilterComponent } from "../select-filter/select-filter.component";
import { TextFilterComponent } from "../text-filter/text-filter.component";
import { DateTimeRangeFilterComponent } from "../date-time-range-filter/date-time-range-filter.component";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import { FormsModule } from "@angular/forms";
import { ScreensizeService } from "../screensize.service";

describe("FilterPanelComponent", () => {
  let component: FilterPanelComponent;
  let fixture: ComponentFixture<FilterPanelComponent>;
  let screensizeService: ScreensizeService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        FilterPanelComponent,
        SelectFilterComponent,
        TextFilterComponent,
        DateTimeRangeFilterComponent,
      ],
      imports: [
        TranslateModule.forRoot(),
        FormsModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatDatepickerModule,
        MatNativeDateModule,
        BrowserAnimationsModule,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FilterPanelComponent);
    component = fixture.componentInstance;
    screensizeService = TestBed.inject(ScreensizeService);
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should display the number of active filters when collapsed and if mobile view", () => {
    component.activeFilters = [
      { fieldName: "filter1", value: "2" },
      { fieldName: "filter1", value: "1" },
    ];
    spyOn(screensizeService, "isMobile").and.returnValue(true);
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
        filterType: FilterType.TEXT,
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
