import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ConversationCardComponent } from "./conversation-card.component";
import { RouterTestingModule } from "@angular/router/testing";
import { TranslateModule } from "@ngx-translate/core";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { By } from "@angular/platform-browser";

describe("ConversationCardComponent", () => {
  let component: ConversationCardComponent;
  let fixture: ComponentFixture<ConversationCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        TranslateModule.forRoot(),
        MatCheckboxModule,
      ],
      declarations: [ConversationCardComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConversationCardComponent);
    component = fixture.componentInstance;

    component.conversation = {
      choreographyId: "GenericFile",
      conversationId: "sdasdad-sadsadasda-asdsadada",
      nxConversationId: 12,
      partnerId: "NexusFriend",
      createdDate: "03-02-2018 15:07:34 GMT",
      status: "Sent",
      currentAction: "SendFile",
      turnAroundTime: "Not terminated",
    };
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

  it("should have a link to the conversation detail page", () => {
    const linkToConversation = fixture.debugElement.query(
      By.css("a[href*='12']")
    );
    expect(linkToConversation).toBeTruthy();
    expect(linkToConversation.nativeElement.textContent).toBeTruthy();
  });
});
