import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ActionPanelComponent } from "./action-panel.component";
import { ActionButtonComponent } from "../action-button/action-button.component";

describe("ActionPanelComponent", () => {
  let component: ActionPanelComponent;
  let fixture: ComponentFixture<ActionPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActionPanelComponent, ActionButtonComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ActionPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
