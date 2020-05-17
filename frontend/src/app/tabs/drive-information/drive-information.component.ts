import { Component, OnInit } from '@angular/core';
import { ApiService } from 'src/app/services/api.service';
import { SensorData, Log } from 'src/app/models/viewmodels';



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

  displayedColumns: string[] = ['date', 'latitude', 'longtitude', 'speed', 'shakeDegree'];
  dataSource = null;
  SENSOR_DATA: SensorData[] = [];

  constructor(private apiService:ApiService) { }

  ngOnInit(): void {
    this.getData();
  }

  getData():void{
    this.apiService.getLogData().subscribe(data=>this.SENSOR_DATA=data);
    this.dataSource = this.SENSOR_DATA;
  }
}
