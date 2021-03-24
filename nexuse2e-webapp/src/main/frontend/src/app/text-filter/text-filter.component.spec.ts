import { ComponentFixture, TestBed } from "@angular/core/testing";

import { TextFilterComponent } from "./text-filter.component";
import { TranslateModule } from "@ngx-translate/core";
import { MatInputModule } from "@angular/material/input";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { FormsModule } from "@angular/forms";
import { By } from "@angular/platform-browser";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatAutocompleteModule } from "@angular/material/autocomplete";

describe("TextFilterComponent", () => {
  let component: TextFilterComponent;
  let fixture: ComponentFixture<TextFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TextFilterComponent],
      imports: [
        TranslateModule.forRoot(),
        MatInputModule,
        FormsModule,
        NoopAnimationsModule,
        MatFormFieldModule,
        MatAutocompleteModule
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TextFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should display the field name", () => {
    component.fieldName = "Test";
    fixture.detectChanges();
    const label = fixture.nativeElement.querySelector("label");

    expect(label.innerText).toContain(component.fieldName);
  });

  it("should have a text input field", () => {
    const textField = fixture.nativeElement.querySelector("input[type='text']");

    expect(textField).toBeTruthy();
  });

  it("should emit active filter on blur", async () => {
    const input = fixture.debugElement.query(By.css("input"));
    spyOn(component.valueChange, "emit");
    const test = "testValue";
    component.selectedValue = test;
    input.triggerEventHandler("blur", {});
    fixture.detectChanges();
    const activeFilter = { fieldName: component.fieldName, value: test };

    expect(component.valueChange.emit).toHaveBeenCalledWith(activeFilter);
  });
});
