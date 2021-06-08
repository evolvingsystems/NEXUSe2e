import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MessagesFailedComponent } from "./messages-failed.component";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TranslateModule } from "@ngx-translate/core";
import { MatTabsModule } from "@angular/material/tabs";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatDialogModule } from "@angular/material/dialog";
import { MatCardModule } from "@angular/material/card";
import { RouterTestingModule } from "@angular/router/testing";
import { ListComponent } from "../list/list.component";
import { ActionButtonComponent } from "../action-button/action-button.component";
import { CapsToTitleCasePipe } from "../pipes/caps-to-title-case.pipe";
import { LoadingSpinnerComponent } from "../loading-spinner/loading-spinner.component";

describe("MessagesFailedComponent", () => {
  let component: MessagesFailedComponent;
  let fixture: ComponentFixture<MessagesFailedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        MatTabsModule,
        BrowserAnimationsModule,
        MatSnackBarModule,
        MatDialogModule,
        MatCardModule,
        RouterTestingModule,
      ],
      declarations: [
        MessagesFailedComponent,
        ListComponent,
        ActionButtonComponent,
        CapsToTitleCasePipe,
        LoadingSpinnerComponent,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MessagesFailedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
