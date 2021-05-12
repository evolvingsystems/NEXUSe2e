import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SuccessfulMessagesListComponent } from "./successful-messages-list.component";
import { TranslateModule } from "@ngx-translate/core";
import { MatTabsModule } from "@angular/material/tabs";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";

describe("SuccessfulMessagesListComponent", () => {
  let component: SuccessfulMessagesListComponent;
  let fixture: ComponentFixture<SuccessfulMessagesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        TranslateModule.forRoot(),
        MatTabsModule,
        BrowserAnimationsModule,
      ],
      declarations: [SuccessfulMessagesListComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SuccessfulMessagesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
