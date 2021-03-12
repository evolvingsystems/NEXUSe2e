import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaginatedListComponent } from './paginated-list.component';
import { messages } from "../test-data";

describe('PaginatedListComponent', () => {
  let component: PaginatedListComponent;
  let fixture: ComponentFixture<PaginatedListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PaginatedListComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PaginatedListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it("should initially load the first page of items if there are any", async () => {
    spyOn(component.triggerReload, "emit");
    component.totalItemCount = 5;

    await component.ngOnInit();

    expect(component.triggerReload.emit).toHaveBeenCalledWith({ pageIndex: 0, pageSize: component.pageSize });
  });

  it("should render one item card for each item on the page", async () => {
    component.items = messages;

    fixture.detectChanges();

    const itemCards = fixture.nativeElement.querySelectorAll(".item-card-wrapper");
    expect(itemCards.length).toBe(component.items.length);
  });
});
