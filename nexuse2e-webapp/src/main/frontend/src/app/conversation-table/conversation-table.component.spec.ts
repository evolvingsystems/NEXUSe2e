import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ConversationTableComponent } from "./conversation-table.component";
import { By } from "@angular/platform-browser";
import { RouterTestingModule } from "@angular/router/testing";
import { TranslateModule } from "@ngx-translate/core";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatTableModule } from "@angular/material/table";
import { conversations } from "../test-data";
import { MasterSelectComponent } from "../master-select/master-select.component";

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
      declarations: [ConversationTableComponent, MasterSelectComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConversationTableComponent);
    component = fixture.componentInstance;
    component.conversations = conversations;
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
    expect(rowHtmlElements.length).toBe(component.conversations.length);
  });

  it("should have a link to the conversation detail page", () => {
    const firstConversationId = component.conversations[0].nxConversationId;
    const linkToConversation = fixture.debugElement.query(
      By.css(`a[href*='${firstConversationId}']`)
    );
    expect(linkToConversation).toBeTruthy();
    expect(linkToConversation.nativeElement.textContent).toBeTruthy();
  });
});
