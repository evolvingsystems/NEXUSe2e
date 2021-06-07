import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ConversationsIdleComponent } from "./conversations-idle.component";
import { TranslateModule } from "@ngx-translate/core";
import { LoadingSpinnerComponent } from "../loading-spinner/loading-spinner.component";
import { ListComponent } from "../list/list.component";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatDialogModule } from "@angular/material/dialog";
import { CapsToTitleCasePipe } from "../pipes/caps-to-title-case.pipe";
import { MatCardModule } from "@angular/material/card";
import { RouterTestingModule } from "@angular/router/testing";
import { HttpClientTestingModule } from "@angular/common/http/testing";

describe("ConversationsIdleComponent", () => {
  let component: ConversationsIdleComponent;
  let fixture: ComponentFixture<ConversationsIdleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        MatSnackBarModule,
        MatDialogModule,
        MatCardModule,
        RouterTestingModule,
      ],
      declarations: [
        ConversationsIdleComponent,
        LoadingSpinnerComponent,
        ListComponent,
        CapsToTitleCasePipe,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConversationsIdleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
