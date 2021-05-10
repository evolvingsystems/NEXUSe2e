import { ComponentFixture, TestBed } from "@angular/core/testing";

import { NotificationComponent } from "./notification.component";
import {
  MAT_SNACK_BAR_DATA,
  MatSnackBarModule,
} from "@angular/material/snack-bar";
import { MatIconModule } from "@angular/material/icon";
import { TranslateModule } from "@ngx-translate/core";

describe("NotificationComponent", () => {
  let component: NotificationComponent;
  let fixture: ComponentFixture<NotificationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatSnackBarModule, MatIconModule, TranslateModule.forRoot()],
      declarations: [NotificationComponent],
      providers: [
        {
          provide: MAT_SNACK_BAR_DATA,
          useValue: {},
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
