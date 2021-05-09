import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ListComponent } from "./list.component";
import { MatTableModule } from "@angular/material/table";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { RouterTestingModule } from "@angular/router/testing";
import { TranslateModule } from "@ngx-translate/core";
import { messages } from "../test-data";
import { MatCardModule } from "@angular/material/card";
import { MatDialogModule } from "@angular/material/dialog";

describe("ListComponent", () => {
  let component: ListComponent;
  let fixture: ComponentFixture<ListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MatTableModule,
        MatCheckboxModule,
        MatCardModule,
        RouterTestingModule,
        TranslateModule.forRoot(),
        MatDialogModule,
      ],
      declarations: [ListComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListComponent);
    component = fixture.componentInstance;
    component.mobileConfig = [
      {
        fieldName: "messageId",
        linkUrlRecipe: "$nxMessageId$",
        isHeader: true,
      },
      {
        fieldName: "conversationId",
        linkUrlRecipe: "../conversations/$nxConversationId$",
      },
      { fieldName: "partnerId" },
      { fieldName: "typeName" },
      { fieldName: "actionId" },
      { fieldName: "createdDate" },
    ];
    component.desktopConfig = [
      {
        fieldName: "messageId",
        linkUrlRecipe: "$nxMessageId$",
      },
      {
        fieldName: "conversationId",
        linkUrlRecipe: "../conversations/$nxConversationId$",
      },
      { fieldName: "partnerId" },
      { fieldName: "status" },
      { fieldName: "backendStatus" },
      { fieldName: "typeName" },
      { fieldName: "choreographyId" },
      { fieldName: "actionId" },
      { fieldName: "createdDate" },
      { fieldName: "turnAroundTime" },
    ];
    component.items = messages;
    component.itemType = "message";
    component.isModalDialog = false;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should show a header element if it is configured in mobile with a linkUrlRecipe", () => {
    spyOn(component.screenSizeService, "isMobile").and.returnValue(true);
    component.mobileConfig = [
      {
        fieldName: "messageId",
        linkUrlRecipe: "$nxMessageId$",
        isHeader: true,
      },
    ];
    component.items = messages;
    component.ngOnInit();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector(".card-header")).toBeTruthy();
  });

  it("should get property by propertyName", () => {
    const message = {
      messageId: "640FE4F8-6A6D-B7D8-E35B-FB7FE4764426",
      actionId: "SendFile",
      choreographyId: "GenericFile",
      createdDate: "01-12-2022 15:21:03 GMT",
      typeName: "Normal",
      status: "Sent",
      conversationId: "CEBC3B83-3648-41C6-F033-E8B8A4A48AF8",
      nxMessageId: 1,
      nxConversationId: 1,
      partnerId: "Victor",
      backendStatus: "QUEUED",
      turnAroundTime: "Not terminated",
    };
    const propertyValue = component.getProperty(message, "nxConversationId");
    expect(propertyValue).toEqual(message.nxConversationId);
  });

  it("should display one column for each configured ListConfig item in desktop", () => {
    spyOn(component.screenSizeService, "isMobile").and.returnValue(false);
    fixture.detectChanges();
    const columns = fixture.nativeElement.querySelectorAll("th");
    // display columns contain desktopConfig + select column (even if isSelectable is false)
    expect(columns.length).toEqual(component.desktopConfig.length + 1);
  });

  it("should display one card row for each configured ListConfig item in mobile", () => {
    spyOn(component.screenSizeService, "isMobile").and.returnValue(true);
    fixture.detectChanges();
    const firstCard = fixture.nativeElement.querySelector("mat-card");
    const header = firstCard.querySelectorAll(".card-header");
    const rows = firstCard.querySelectorAll("tr");
    expect(rows.length).toEqual(component.mobileConfig.length - header.length);
  });

  it("should build the item url if it consists of one variable", () => {
    const item = messages[0];
    const recipe = "$nxConversationId$";
    const builtUrl = component.getUrl(item, recipe);
    expect(builtUrl).toEqual("" + item.nxConversationId);
  });

  it("should build the item url if it contains no variables", () => {
    const item = messages[0];
    const recipe = "/base/test";
    const builtUrl = component.getUrl(item, recipe);
    expect(builtUrl).toEqual("/base/test");
  });

  it("should build the item url if it contains multiple variables in the middle", () => {
    const item = messages[0];
    const recipe = "/base/test/$nxConversationId$/test2/$nxConversationId$/";
    const builtUrl = component.getUrl(item, recipe);
    expect(builtUrl).toEqual(
      "/base/test/" +
        item.nxConversationId +
        "/test2/" +
        item.nxConversationId +
        "/"
    );
  });

  it("should build the item url if it contains multiple variables on the edges", () => {
    const item = messages[0];
    const recipe = "$nxConversationId$/test/test3/$nxConversationId$";
    const builtUrl = component.getUrl(item, recipe);
    expect(builtUrl).toEqual(
      item.nxConversationId + "/test/test3/" + item.nxConversationId
    );
  });
});
