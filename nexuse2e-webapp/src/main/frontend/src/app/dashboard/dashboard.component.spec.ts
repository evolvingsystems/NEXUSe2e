import { ComponentFixture, TestBed, waitForAsync } from "@angular/core/testing";

import { DashboardComponent } from "./dashboard.component";
import { ConversationStatusCountsComponent } from "../conversation-status-counts/conversation-status-counts.component";
import { HttpClientTestingModule } from "@angular/common/http/testing";

describe("DashboardComponent", () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [DashboardComponent, ConversationStatusCountsComponent],
      }).compileComponents();
    })
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
