import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SimpleTableDialogComponent } from "./simple-table-dialog.component";
import { MAT_DIALOG_DATA, MatDialogModule } from "@angular/material/dialog";
import { TranslateModule } from "@ngx-translate/core";
import { ListComponent } from "../list/list.component";
import { MatSnackBarModule } from "@angular/material/snack-bar";

describe("ModalDialogComponent", () => {
  let component: SimpleTableDialogComponent;
  let fixture: ComponentFixture<SimpleTableDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatDialogModule, TranslateModule.forRoot(), MatSnackBarModule],
      declarations: [SimpleTableDialogComponent, ListComponent],
      providers: [{ provide: MAT_DIALOG_DATA, useValue: "" }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SimpleTableDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
