import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Approvedeny } from './approvedeny';

describe('Approvedeny', () => {
  let component: Approvedeny;
  let fixture: ComponentFixture<Approvedeny>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Approvedeny]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Approvedeny);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
