import { ComponentFixture, TestBed, waitForAsync } from "@angular/core/testing";

import { DashboardComponent } from "./dashboard.component";
import { TranslateModule } from "@ngx-translate/core";
import { MatTabsModule } from "@angular/material/tabs";
import { SuccessfulMessagesListComponent } from "../successful-messages-list/successful-messages-list.component";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ListComponent } from "../list/list.component";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatDialogModule } from "@angular/material/dialog";
import { CapsToTitleCasePipe } from "../pipes/caps-to-title-case.pipe";
import { MatCardModule } from "@angular/material/card";

describe("DashboardComponent", () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          TranslateModule.forRoot(),
          MatTabsModule,
          BrowserAnimationsModule,
          MatSnackBarModule,
          MatDialogModule,
          MatCardModule,
        ],
        declarations: [
          DashboardComponent,
          SuccessfulMessagesListComponent,
          ListComponent,
          CapsToTitleCasePipe,
        ],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
