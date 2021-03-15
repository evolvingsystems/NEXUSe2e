import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ConversationListComponent } from "./conversation-list.component";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { DataService } from "../data/data.service";
import { TranslateModule } from "@ngx-translate/core";

describe("ConversationListComponent", () => {
  let component: ConversationListComponent;
  let fixture: ComponentFixture<ConversationListComponent>;
  let dataService: DataService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, TranslateModule.forRoot()],
      declarations: [ConversationListComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConversationListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    dataService = TestBed.inject(DataService);
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
