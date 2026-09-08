import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeAmountComponent } from './change-amount.component';

describe('ChangeAmountComponent', () => {
  let component: ChangeAmountComponent;
  let fixture: ComponentFixture<ChangeAmountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangeAmountComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChangeAmountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
