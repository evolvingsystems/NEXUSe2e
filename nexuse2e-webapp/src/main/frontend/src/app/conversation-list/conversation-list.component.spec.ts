import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ConversationListComponent } from "./conversation-list.component";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TranslateModule } from "@ngx-translate/core";
import { FilterPanelComponent } from "../filter-panel/filter-panel.component";
import { SelectFilterComponent } from "../select-filter/select-filter.component";
import { TextFilterComponent } from "../text-filter/text-filter.component";
import { DateTimeRangeFilterComponent } from "../date-time-range-filter/date-time-range-filter.component";
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
import { StringPipe } from "../pipes/string.pipe";
import { DateRangePipe } from "../pipes/date-range.pipe";
import { ActionButtonComponent } from "../action-button/action-button.component";
import { MatSnackBarModule } from "@angular/material/snack-bar";

describe("ConversationListComponent", () => {
  let component: ConversationListComponent;
  let fixture: ComponentFixture<ConversationListComponent>;

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
        MatSnackBarModule,
      ],
      declarations: [
        ConversationListComponent,
        FilterPanelComponent,
        PaginatedListComponent,
        SelectFilterComponent,
        TextFilterComponent,
        DateTimeRangeFilterComponent,
        LoadingSpinnerComponent,
        ListComponent,
        StringPipe,
        DateRangePipe,
        ActionButtonComponent,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConversationListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
