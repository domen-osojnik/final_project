import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { IMUComponent } from './imu.component';

describe('IMUComponent', () => {
  let component: IMUComponent;
  let fixture: ComponentFixture<IMUComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IMUComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IMUComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
