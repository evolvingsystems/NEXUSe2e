import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MessageListComponent } from "./message-list.component";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { DataService } from "../data/data.service";
import { messages } from "../test-data";
import { MessageCardComponent } from "../message-card/message-card.component";
import { TranslateModule } from "@ngx-translate/core";

describe("MessageListComponent", () => {
  let component: MessageListComponent;
  let fixture: ComponentFixture<MessageListComponent>;
  let dataService: DataService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        TranslateModule.forRoot()
      ],
      declarations: [
        MessageListComponent,
        MessageCardComponent
      ],
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

  it("should initially load the first page of messages", async () => {
    spyOn(dataService, "getMessagesCount").and.returnValue(Promise.resolve(20));
    spyOn(dataService, "getMessages").and.returnValue(Promise.resolve(messages));
    component.pageSize = 5;

    await component.ngOnInit();

    expect(dataService.getMessages).toHaveBeenCalledWith(0, 5);
  });

  it("should not load messages if there are none", async () => {
    spyOn(dataService, "getMessagesCount").and.returnValue(Promise.resolve(0));
    spyOn(dataService, "getMessages").and.returnValue(Promise.resolve(messages));

    await component.ngOnInit();

    expect(dataService.getMessages).toHaveBeenCalledTimes(0);
  });

  it("should render one message card for each message on the page", async () => {
    spyOn(dataService, "getMessagesCount").and.returnValue(Promise.resolve(20));
    spyOn(dataService, "getMessages").and.returnValue(Promise.resolve(messages));

    await component.ngOnInit();
    fixture.detectChanges();

    const messageCards = fixture.nativeElement.querySelectorAll("app-message-card");
    expect(messageCards.length).toBe(20);
  });
});
