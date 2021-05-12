import { ComponentFixture, TestBed } from "@angular/core/testing";

import { UserConfirmationDialogComponent } from "./user-confirmation-dialog.component";
import { MAT_DIALOG_DATA, MatDialogModule } from "@angular/material/dialog";
import { TranslateModule } from "@ngx-translate/core";

describe("UserConfirmationDialogComponent", () => {
  let component: UserConfirmationDialogComponent;
  let fixture: ComponentFixture<UserConfirmationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatDialogModule, TranslateModule.forRoot()],
      declarations: [UserConfirmationDialogComponent],
      providers: [{ provide: MAT_DIALOG_DATA, useValue: "" }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserConfirmationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
