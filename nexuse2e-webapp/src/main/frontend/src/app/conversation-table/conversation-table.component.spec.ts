import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ConversationTableComponent } from "./conversation-table.component";
import { By } from "@angular/platform-browser";
import { RouterTestingModule } from "@angular/router/testing";
import { TranslateModule } from "@ngx-translate/core";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatTableModule } from "@angular/material/table";

describe("ConversationTableComponent", () => {
  let component: ConversationTableComponent;
  let fixture: ComponentFixture<ConversationTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        TranslateModule.forRoot(),
        MatCheckboxModule,
        MatTableModule,
      ],
      declarations: [ConversationTableComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConversationTableComponent);
    component = fixture.componentInstance;
    component.conversations = [
      {
        choreographyId: "GenericFile",
        conversationId: "sdasdad-sadsadasda-asdsadada",
        nxConversationId: 12,
        partnerId: "NexusFriend",
        createdDate: "03-02-2018 15:07:34 GMT",
        status: "Sent",
        currentAction: "SendFile",
        turnAroundTime: "Not terminated",
      },
      {
        choreographyId: "GenericFile",
        conversationId: "sdasdad-sadsadasda-asdsadada",
        nxConversationId: 13,
        partnerId: "NexusFriend2",
        createdDate: "03-02-2018 15:07:34 GMT",
        status: "Queued",
        currentAction: "SendFile",
        turnAroundTime: "Not terminated",
      },
    ];
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

  it("should show all conversations", () => {
    const rowHtmlElements = fixture.nativeElement.querySelectorAll("tbody tr");
    expect(rowHtmlElements.length).toBe(2);
  });

  it("should have a link to the conversation detail page", () => {
    const linkToConversation = fixture.debugElement.query(
      By.css("a[href*='12']")
    );
    expect(linkToConversation).toBeTruthy();
    expect(linkToConversation.nativeElement.textContent).toBeTruthy();
  });
});
