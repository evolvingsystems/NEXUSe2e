import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ConversationStatusCountsComponent } from "./conversation-status-counts.component";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TranslateModule } from "@ngx-translate/core";

describe("ConversationStatusCountsComponent", () => {
  let component: ConversationStatusCountsComponent;
  let fixture: ComponentFixture<ConversationStatusCountsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, TranslateModule.forRoot()],
      declarations: [ConversationStatusCountsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConversationStatusCountsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
