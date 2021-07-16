import { ComponentFixture, TestBed } from "@angular/core/testing";

import { TransactionReportingComponent } from "./transaction-reporting.component";
import { By } from "@angular/platform-browser";
import { TranslateModule } from "@ngx-translate/core";
import { RouterTestingModule } from "@angular/router/testing";
import { MatTabsModule } from "@angular/material/tabs";
import { MatIconModule } from "@angular/material/icon";
import { RefreshButtonComponent } from "../refresh-button/refresh-button.component";

describe("TransactionReportingComponent", () => {
  let component: TransactionReportingComponent;
  let fixture: ComponentFixture<TransactionReportingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        TranslateModule.forRoot(),
        RouterTestingModule,
        MatTabsModule,
        MatIconModule,
      ],
      declarations: [TransactionReportingComponent, RefreshButtonComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransactionReportingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should have a title", () => {
    const title = fixture.debugElement.query(By.css(".title"));
    expect(title.nativeElement.textContent).toBeTruthy();
  });

  it("should show a link to message overview", () => {
    const linkToMessages = fixture.debugElement.query(
      By.css("a[href*='messages']")
    );
    expect(linkToMessages).toBeTruthy();
    expect(linkToMessages.nativeElement.textContent).toBeTruthy();
  });

  it("should show a link to conversations overview", () => {
    const linkToConversations = fixture.debugElement.query(
      By.css("a[href*='conversations']")
    );
    expect(linkToConversations).toBeTruthy();
    expect(linkToConversations.nativeElement.textContent).toBeTruthy();
  });
});
