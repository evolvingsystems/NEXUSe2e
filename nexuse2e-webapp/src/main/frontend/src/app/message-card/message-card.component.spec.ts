import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MessageCardComponent } from "./message-card.component";
import { TranslateModule } from "@ngx-translate/core";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { By } from "@angular/platform-browser";
import { RouterTestingModule } from "@angular/router/testing";
import { messages } from "../test-data";

describe("MessageCardComponent", () => {
  let component: MessageCardComponent;
  let fixture: ComponentFixture<MessageCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        TranslateModule.forRoot(),
        MatCheckboxModule,
      ],
      declarations: [MessageCardComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageCardComponent);
    component = fixture.componentInstance;
    component.message = messages[0];
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should have a checkbox", () => {
    const checkbox = fixture.nativeElement.querySelector(
      "input[type='checkbox']"
    );
    expect(checkbox).toBeTruthy();
  });

  it("should have a link to the message detail page", () => {
    const messageId = component.message.nxMessageId;
    const linkToMessage = fixture.debugElement.query(By.css(`a[href*='${messageId}']`));
    expect(linkToMessage).toBeTruthy();
    expect(linkToMessage.nativeElement.textContent).toBeTruthy();
  });

  it("should have a link to the conversation detail page", () => {
    const linkToConversation = fixture.debugElement.query(
      By.css(`a[href*='${component.message.nxConversationId}']`)
    );
    expect(linkToConversation).toBeTruthy();
    expect(linkToConversation.nativeElement.textContent).toBeTruthy();
  });
});
