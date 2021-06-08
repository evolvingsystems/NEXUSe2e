import { ComponentFixture, TestBed, waitForAsync } from "@angular/core/testing";

import { DashboardComponent } from "./dashboard.component";
import { TranslateModule } from "@ngx-translate/core";
import { MatTabsModule } from "@angular/material/tabs";
import { SuccessfulMessagesComponent } from "../successful-messages/successful-messages.component";
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
import { ConversationsIdleComponent } from "../conversations-idle/conversations-idle.component";
import { CertificatesComponent } from "../certificates/certificates.component";

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
        ],
        declarations: [
          DashboardComponent,
          SuccessfulMessagesComponent,
          ListComponent,
          CapsToTitleCasePipe,
          ConversationStatusCountsComponent,
          LoadingSpinnerComponent,
          ConversationsIdleComponent,
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
