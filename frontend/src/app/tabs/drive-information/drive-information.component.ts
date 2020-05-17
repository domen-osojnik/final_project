import { Component, OnInit } from '@angular/core';

interface Log {
  value: string;
}

export interface SensorReading {
  date: string;
  latitude: number;
  longtitude: number;
  speed: number;
  maxSpeed:number;
  shakeDegree:number;
}

const ELEMENT_DATA: SensorReading[] = [
  {date: new Date().getDate().toLocaleString(), latitude: 1.0079, longtitude: 1.0079, speed: 0, maxSpeed: 0, shakeDegree:0}
];

@Component({
  selector: 'app-drive-information',
  templateUrl: './drive-information.component.html',
  styleUrls: ['./drive-information.component.css']
})
export class DriveInformationComponent implements OnInit {
  logs: Log[] = [
    {value: 'ID12421525'},
    {value: 'ID1242152521t'},
    {value: 'ID12421525-2'}
  ];

  displayedColumns: string[] = ['date', 'latitude', 'longtitude', 'speed', 'maxSpeed', 'shakeDegree'];
  dataSource = ELEMENT_DATA;

  constructor() { }

  ngOnInit(): void {
  }
}
