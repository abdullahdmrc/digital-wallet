import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WalletCreateDialog } from './wallet-create-dialog';

describe('WalletCreateDialog', () => {
  let component: WalletCreateDialog;
  let fixture: ComponentFixture<WalletCreateDialog>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WalletCreateDialog]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WalletCreateDialog);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
