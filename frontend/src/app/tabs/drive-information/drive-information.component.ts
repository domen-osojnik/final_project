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
  displayedColumns: string[] = ['date', 'lat', 'long', 'speed', 'degreeStatus'];
  
  SENSOR_DATA: SensorData[] = [];

  constructor(private apiService:ApiService) { }

  ngOnInit(): void {
    this.getData();
  }

  getData():void{
    this.apiService.getLogData().subscribe(data=>this.logs=data);
  }

  selectLog(id:string) {
    this.logs.forEach(log => {
      if(id == log._id)this.handleData(log);
    });
  }

  handleData(data:Log){
      data.events.forEach(data => {
        if(data.degree == 0)data.degreeStatus = "Normalno stanje"
        else if(data.degree == 1)data.degreeStatus = "Lajši tresljaj"
        else if(data.degree == 2)data.degreeStatus = "Hujši tresljaj"
      });

      this.dataSource = data.events;
  }
}
