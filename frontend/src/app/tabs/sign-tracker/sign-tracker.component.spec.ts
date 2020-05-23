import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SignTrackerComponent } from './sign-tracker.component';

describe('SignTrackerComponent', () => {
  let component: SignTrackerComponent;
  let fixture: ComponentFixture<SignTrackerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SignTrackerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SignTrackerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
