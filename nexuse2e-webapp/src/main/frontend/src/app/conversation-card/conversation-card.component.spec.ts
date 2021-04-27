import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ConversationCardComponent } from "./conversation-card.component";
import { RouterTestingModule } from "@angular/router/testing";
import { TranslateModule } from "@ngx-translate/core";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { By } from "@angular/platform-browser";
import { conversations } from "../test-data";
import { MatCardModule } from "@angular/material/card";

describe("ConversationCardComponent", () => {
  let component: ConversationCardComponent;
  let fixture: ComponentFixture<ConversationCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        TranslateModule.forRoot(),
        MatCheckboxModule,
        MatCardModule,
      ],
      declarations: [ConversationCardComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConversationCardComponent);
    component = fixture.componentInstance;

    component.conversation = conversations[0];
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should have a link to the conversation detail page", () => {
    const linkToConversation = fixture.debugElement.query(
      By.css(`a[href*='${component.conversation.nxConversationId}']`)
    );
    expect(linkToConversation).toBeTruthy();
    expect(linkToConversation.nativeElement.textContent).toBeTruthy();
  });
});
