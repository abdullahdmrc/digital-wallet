import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionCreation } from './transaction-creation';

describe('TransactionCreation', () => {
  let component: TransactionCreation;
  let fixture: ComponentFixture<TransactionCreation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransactionCreation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TransactionCreation);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
