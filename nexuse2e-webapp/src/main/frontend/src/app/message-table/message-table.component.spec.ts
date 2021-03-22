import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MessageTableComponent } from "./message-table.component";
import { By } from "@angular/platform-browser";
import { MatTableModule } from "@angular/material/table";
import { TranslateModule } from "@ngx-translate/core";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { RouterTestingModule } from "@angular/router/testing";
import { messages } from "../test-data";

describe("MessageTableComponent", () => {
  let component: MessageTableComponent;
  let fixture: ComponentFixture<MessageTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MatTableModule,
        MatCheckboxModule,
        RouterTestingModule,
        TranslateModule.forRoot(),
      ],
      declarations: [MessageTableComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageTableComponent);
    component = fixture.componentInstance;
    component.messages = messages.slice(0, 5);
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should show all messages", () => {
    const rowHtmlElements = fixture.nativeElement.querySelectorAll("tbody tr");
    expect(rowHtmlElements.length).toBe(component.messages.length);
  });

  it("should have a checkbox", () => {
    const checkbox = fixture.nativeElement.querySelector(
      "input[type='checkbox']"
    );
    expect(checkbox).toBeTruthy();
  });

  it("should have a link to the message detail page", () => {
    const firstMessageId = component.messages[0].nxMessageId;
    const linkToMessage = fixture.debugElement.query(By.css(`a[href*='${firstMessageId}']`));
    expect(linkToMessage).toBeTruthy();
    expect(linkToMessage.nativeElement.textContent).toBeTruthy();
  });

  it("should have a link to the conversation detail page", () => {
    const firstConversationId = component.messages[0].nxMessageId;
    const linkToConversation = fixture.debugElement.query(
      By.css(`a[href*='${firstConversationId}']`)
    );
    expect(linkToConversation).toBeTruthy();
    expect(linkToConversation.nativeElement.textContent).toBeTruthy();
  });
});
