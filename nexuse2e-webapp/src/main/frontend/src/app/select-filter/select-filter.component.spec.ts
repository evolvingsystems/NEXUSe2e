import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SelectFilterComponent } from "./select-filter.component";
import { TranslateModule } from "@ngx-translate/core";
import { MatSelectModule } from "@angular/material/select";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { By } from "@angular/platform-browser";

describe("SelectFilterComponent", () => {
  let component: SelectFilterComponent;
  let fixture: ComponentFixture<SelectFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SelectFilterComponent],
      imports: [TranslateModule.forRoot(), MatSelectModule, NoopAnimationsModule]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectFilterComponent);
    component = fixture.componentInstance;
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should display an option for each allowed value", async () => {
    component.allowedValues = ["One", "Two", "Three"];
    fixture.detectChanges();
    await fixture.whenStable();
    const debugElement = fixture.debugElement;
    const matSelect = debugElement.query(By.css('.mat-select-trigger')).nativeElement;
    matSelect.click();
    fixture.detectChanges();
    await fixture.whenStable();
    const matOptions = debugElement.queryAll(By.css('.mat-option'));

    expect(matOptions.length).toBe(component.allowedValues.length + 1);
  });

  it("should emit the value when something is selected", async () => {
    spyOn(component, "emitValue");
    component.allowedValues = ["Three", "Four", "Five"];
    fixture.detectChanges();
    await fixture.whenStable();
    const debugElement = fixture.debugElement;
    const matSelect = debugElement.query(By.css(".mat-select-trigger")).nativeElement;
    matSelect.click();
    fixture.detectChanges();
    await fixture.whenStable();
    const matOptions = debugElement.queryAll(By.css(".mat-option"));
    matOptions[1].nativeElement.click();

    expect(component.emitValue).toHaveBeenCalled();
  })
});
