import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MasterSelectComponent } from './master-select.component';

describe('MasterSelectComponent', () => {
  let component: MasterSelectComponent;
  let fixture: ComponentFixture<MasterSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MasterSelectComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MasterSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
