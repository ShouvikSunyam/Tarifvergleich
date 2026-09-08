import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EnergySupplierComponent } from './energy-supplier.component';

describe('EnergySupplierComponent', () => {
  let component: EnergySupplierComponent;
  let fixture: ComponentFixture<EnergySupplierComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnergySupplierComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EnergySupplierComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
