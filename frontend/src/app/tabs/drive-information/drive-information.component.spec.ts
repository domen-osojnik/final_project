import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DriveInformationComponent } from './drive-information.component';

describe('DriveInformationComponent', () => {
  let component: DriveInformationComponent;
  let fixture: ComponentFixture<DriveInformationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DriveInformationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DriveInformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
