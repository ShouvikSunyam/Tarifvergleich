import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContractEditOptionsComponent } from './contract-edit-options.component';

describe('ContractEditOptionsComponent', () => {
  let component: ContractEditOptionsComponent;
  let fixture: ComponentFixture<ContractEditOptionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContractEditOptionsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContractEditOptionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
