import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ActionButtonComponent } from "./action-button.component";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TranslateModule } from "@ngx-translate/core";
import { PermissionService } from "../services/permission.service";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatDialogModule } from "@angular/material/dialog";

describe("ActionButtonComponent", () => {
  let component: ActionButtonComponent;
  let fixture: ComponentFixture<ActionButtonComponent>;
  let permissionService: PermissionService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        TranslateModule.forRoot(),
        MatSnackBarModule,
        MatDialogModule,
      ],
      declarations: [ActionButtonComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ActionButtonComponent);
    component = fixture.componentInstance;
    component.action = {
      label: "requeue",
      actionKey: "messages.requeue",
    };
    permissionService = TestBed.inject(PermissionService);
    spyOn(permissionService, "isUserPermitted").and.returnValue(true);
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
