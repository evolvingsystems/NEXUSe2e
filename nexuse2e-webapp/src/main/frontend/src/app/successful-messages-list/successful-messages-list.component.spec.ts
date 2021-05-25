import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SuccessfulMessagesListComponent } from "./successful-messages-list.component";
import { TranslateModule } from "@ngx-translate/core";
import { MatTabsModule } from "@angular/material/tabs";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ListComponent } from "../list/list.component";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatDialogModule } from "@angular/material/dialog";
import { CapsToTitleCasePipe } from "../pipes/caps-to-title-case.pipe";
import { MatCardModule } from "@angular/material/card";
import { RouterTestingModule } from "@angular/router/testing";

describe("SuccessfulMessagesListComponent", () => {
  let component: SuccessfulMessagesListComponent;
  let fixture: ComponentFixture<SuccessfulMessagesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        TranslateModule.forRoot(),
        MatTabsModule,
        BrowserAnimationsModule,
        MatSnackBarModule,
        MatDialogModule,
        MatCardModule,
        RouterTestingModule,
      ],
      declarations: [
        SuccessfulMessagesListComponent,
        ListComponent,
        CapsToTitleCasePipe,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SuccessfulMessagesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
