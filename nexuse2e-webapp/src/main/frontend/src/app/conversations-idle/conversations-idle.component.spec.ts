import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ConversationsIdleComponent } from "./conversations-idle.component";
import { TranslateModule } from "@ngx-translate/core";
import { LoadingSpinnerComponent } from "../loading-spinner/loading-spinner.component";

describe("ConversationsIdleComponent", () => {
  let component: ConversationsIdleComponent;
  let fixture: ComponentFixture<ConversationsIdleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      declarations: [ConversationsIdleComponent, LoadingSpinnerComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConversationsIdleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
