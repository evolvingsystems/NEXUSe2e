import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ConversationDetailComponent } from "./conversation-detail.component";
import { ActivatedRoute } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { TranslateModule } from "@ngx-translate/core";
import { MatIconModule } from "@angular/material/icon";
import { ListComponent } from "../list/list.component";
import { MatDialogModule } from "@angular/material/dialog";
import { ActionButtonComponent } from "../action-button/action-button.component";

describe("ConversationDetailComponent", () => {
  let component: ConversationDetailComponent;
  let fixture: ComponentFixture<ConversationDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        MatSnackBarModule,
        MatIconModule,
        MatDialogModule,
      ],
      declarations: [
        ConversationDetailComponent,
        ListComponent,
        ActionButtonComponent,
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
    fixture = TestBed.createComponent(ConversationDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
