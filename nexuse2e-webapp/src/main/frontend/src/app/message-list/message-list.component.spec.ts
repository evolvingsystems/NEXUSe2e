import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MessageListComponent } from "./message-list.component";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { DataService } from "../data/data.service";
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
        MessageListComponent
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
});
