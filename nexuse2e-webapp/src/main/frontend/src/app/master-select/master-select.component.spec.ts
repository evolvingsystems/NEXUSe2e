import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MasterSelectComponent } from './master-select.component';
import { MatCheckboxModule } from "@angular/material/checkbox";

describe('MasterSelectComponent', () => {
  let component: MasterSelectComponent;
  let fixture: ComponentFixture<MasterSelectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatCheckboxModule],
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
