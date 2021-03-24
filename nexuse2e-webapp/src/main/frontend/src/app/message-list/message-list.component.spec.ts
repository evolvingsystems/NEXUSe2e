import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MessageListComponent } from "./message-list.component";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TranslateModule } from "@ngx-translate/core";
import { DataService } from "../data/data.service";

describe("MessageListComponent", () => {
  let component: MessageListComponent;
  let fixture: ComponentFixture<MessageListComponent>;
  let dataService: DataService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, TranslateModule.forRoot()],
      declarations: [MessageListComponent],
      providers: [DataService],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MessageListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    dataService = TestBed.inject(DataService);
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should set start and end date as default inside active filters before refreshing the message count", () => {
    spyOn(dataService, "getMessagesCount");
    component.refreshMessageCount();
    expect(dataService.getMessagesCount).toHaveBeenCalledWith([
      {
        fieldName: "startEndDateRange",
        value: {
          startDate: MessageListComponent.START_DATE_DEFAULT,
          endDate: MessageListComponent.END_DATE_DEFAULT,
        },
      },
    ]);
  });
});
