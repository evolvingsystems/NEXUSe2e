import { ComponentFixture, TestBed, waitForAsync } from "@angular/core/testing";

import { DashboardComponent } from "./dashboard.component";
import { TranslateModule } from "@ngx-translate/core";
import { MatTabsModule } from "@angular/material/tabs";
import { MessagesSuccessfulComponent } from "../messages-successful/messages-successful.component";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ListComponent } from "../list/list.component";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatDialogModule } from "@angular/material/dialog";
import { CapsToTitleCasePipe } from "../pipes/caps-to-title-case.pipe";
import { MatCardModule } from "@angular/material/card";
import { RouterTestingModule } from "@angular/router/testing";
import { ConversationStatusCountsComponent } from "../conversation-status-counts/conversation-status-counts.component";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { LoadingSpinnerComponent } from "../loading-spinner/loading-spinner.component";
import { MessagesFailedComponent } from "../messages-failed/messages-failed.component";
import { ActionButtonComponent } from "../action-button/action-button.component";
import { ConversationsIdleComponent } from "../conversations-idle/conversations-idle.component";
import { CertificatesComponent } from "../certificates/certificates.component";
import { MatChipsModule } from "@angular/material/chips";

describe("DashboardComponent", () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          HttpClientTestingModule,
          TranslateModule.forRoot(),
          MatTabsModule,
          BrowserAnimationsModule,
          MatSnackBarModule,
          MatDialogModule,
          MatCardModule,
          RouterTestingModule,
          MatChipsModule,
        ],
        declarations: [
          DashboardComponent,
          MessagesSuccessfulComponent,
          ListComponent,
          CapsToTitleCasePipe,
          ConversationStatusCountsComponent,
          LoadingSpinnerComponent,
          ConversationsIdleComponent,
          MessagesFailedComponent,
          ActionButtonComponent,
          CertificatesComponent,
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
