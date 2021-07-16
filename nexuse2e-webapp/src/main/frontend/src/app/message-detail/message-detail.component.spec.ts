import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MessageDetailComponent } from "./message-detail.component";
import { RouterTestingModule } from "@angular/router/testing";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TranslateModule } from "@ngx-translate/core";
import { ActivatedRoute } from "@angular/router";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatIconModule } from "@angular/material/icon";
import { ListComponent } from "../list/list.component";
import { MatDialogModule } from "@angular/material/dialog";
import { ActionButtonComponent } from "../action-button/action-button.component";
import { MatCardModule } from "@angular/material/card";
import { LoadingSpinnerComponent } from "../loading-spinner/loading-spinner.component";
import { RefreshButtonComponent } from "../refresh-button/refresh-button.component";

describe("MessageDetailComponent", () => {
  let component: MessageDetailComponent;
  let fixture: ComponentFixture<MessageDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        MatSnackBarModule,
        MatIconModule,
        MatDialogModule,
        MatCardModule,
      ],
      declarations: [
        MessageDetailComponent,
        ListComponent,
        ActionButtonComponent,
        LoadingSpinnerComponent,
        RefreshButtonComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: {
                get: () => 233,
              },
            },
          },
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
