import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MessageTableComponent } from "./message-table.component";
import { By } from "@angular/platform-browser";
import { MatTableModule } from "@angular/material/table";
import { TranslateModule } from "@ngx-translate/core";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { RouterTestingModule } from "@angular/router/testing";

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
    component.messages = [
      {
        messageId: "sdasdad-sadsadasda-asdsadada",
        actionId: "SendFile",
        createdDate: "03-02-2018 15:07:34 GMT",
        typeName: "Normal",
        status: "Sent",
        conversationId: "dsfsdfs-dsfsdfdf-dsfsdfsf",
        nxMessageId: 25,
        nxConversationId: 12,
        partnerId: "NexusFriend",
        backendStatus: "QUEUED",
        turnAroundTime: "Not terminated",
      },
      {
        messageId: "sdasdad-sadsadasda-asdsadada",
        actionId: "SendFile",
        createdDate: "03-02-2018 15:07:34 GMT",
        typeName: "Normal",
        status: "Queued",
        conversationId: "dsfsdfs-dsfsdfdf-dsfsdfsf",
        nxMessageId: 26,
        nxConversationId: 13,
        partnerId: "NexusFriend2",
        backendStatus: "QUEUED",
        turnAroundTime: "Not terminated",
      },
    ];
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should show all messages", () => {
    const rowHtmlElements = fixture.nativeElement.querySelectorAll("tbody tr");
    expect(rowHtmlElements.length).toBe(2);
  });

  it("should have a checkbox", () => {
    const checkbox = fixture.nativeElement.querySelector(
      "input[type='checkbox']"
    );
    expect(checkbox).toBeTruthy();
  });

  it("should have a link to the message detail page", () => {
    const linkToMessage = fixture.debugElement.query(By.css("a[href*='1']"));
    expect(linkToMessage).toBeTruthy();
    expect(linkToMessage.nativeElement.textContent).toBeTruthy();
  });

  it("should have a link to the conversation detail page", () => {
    const linkToConversation = fixture.debugElement.query(
      By.css("a[href*='1']")
    );
    expect(linkToConversation).toBeTruthy();
    expect(linkToConversation.nativeElement.textContent).toBeTruthy();
  });
});
