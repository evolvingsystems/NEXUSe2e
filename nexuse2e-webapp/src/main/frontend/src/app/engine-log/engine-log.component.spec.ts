import { ComponentFixture, TestBed } from "@angular/core/testing";

import { EngineLogComponent } from "./engine-log.component";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TranslateModule } from "@ngx-translate/core";
import { FilterPanelComponent } from "../filter-panel/filter-panel.component";
import { SelectFilterComponent } from "../select-filter/select-filter.component";
import { TextFilterComponent } from "../text-filter/text-filter.component";
import { DateRangeFilterComponent } from "../date-range-filter/date-range-filter.component";
import { PaginatedListComponent } from "../paginated-list/paginated-list.component";
import { FormsModule } from "@angular/forms";
import { MatIconModule } from "@angular/material/icon";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { MatSelectModule } from "@angular/material/select";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { MatPaginatorModule } from "@angular/material/paginator";
import { LoadingSpinnerComponent } from "../loading-spinner/loading-spinner.component";
import { MatTableModule } from "@angular/material/table";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { ListComponent } from "../list/list.component";
import { MatCardModule } from "@angular/material/card";
import { StringPipe } from "../pipes/string.pipe";
import { DateRangePipe } from "../pipes/date-range.pipe";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { ActivatedRoute } from "@angular/router";
import { MatDialogModule } from "@angular/material/dialog";

describe("EngineLogComponent", () => {
  let component: EngineLogComponent;
  let fixture: ComponentFixture<EngineLogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        MatPaginatorModule,
        FormsModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        MatDatepickerModule,
        MatNativeDateModule,
        BrowserAnimationsModule,
        MatAutocompleteModule,
        MatTableModule,
        MatCheckboxModule,
        MatCardModule,
        MatSnackBarModule,
        MatDialogModule,
      ],
      declarations: [
        EngineLogComponent,
        FilterPanelComponent,
        PaginatedListComponent,
        SelectFilterComponent,
        TextFilterComponent,
        DateRangeFilterComponent,
        LoadingSpinnerComponent,
        ListComponent,
        StringPipe,
        DateRangePipe,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParams: {},
              queryParamMap: {
                get: () => {
                  "startEndDateRange";
                },
              },
            },
          },
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EngineLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
