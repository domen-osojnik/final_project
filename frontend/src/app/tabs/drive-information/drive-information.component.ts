import { Component, OnInit } from '@angular/core';
import { ApiService } from 'src/app/services/api.service';
import { SensorData, Log } from 'src/app/models/viewmodels';



@Component({
  selector: 'app-drive-information',
  templateUrl: './drive-information.component.html',
  styleUrls: ['./drive-information.component.css']
})
export class DriveInformationComponent implements OnInit {
  dataSource:SensorData[] = [];
  logs: Log[] = [];
  displayedColumns: string[] = ['date', 'latitude', 'longtitude', 'speed', 'shakeDegree'];
  
  SENSOR_DATA: SensorData[] = [];

  constructor(private apiService:ApiService) { }

  ngOnInit(): void {
    this.getData();
  }

  getData():void{
    this.apiService.getLogData().subscribe(data=>this.logs=data);
    console.log(this.logs);
  }
}
