import { ComponentFixture, TestBed } from "@angular/core/testing";

import { TableComponent } from "./table.component";
import { MatTableModule } from "@angular/material/table";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { RouterTestingModule } from "@angular/router/testing";
import { TranslateModule } from "@ngx-translate/core";

describe("TableComponent", () => {
  let component: TableComponent;
  let fixture: ComponentFixture<TableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        MatTableModule,
        MatCheckboxModule,
        RouterTestingModule,
        TranslateModule.forRoot(),
      ],
      declarations: [TableComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should find the header element if it is configured");
  it("should get property by propertyName");
  it("should thrown an error if propertyName does not exist");
  it("should show a checkbox if it is configured");
  it("should build the item url if configured");
  // $nxConversationId$
  // /base/test
  // /base/test/$nxConversationId$/test2/$nxConversationId$/
  // $nxConversationId$/test/test3/$nxConversationId$
  // ""
});
