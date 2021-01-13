import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { IMUData } from 'src/app/models/viewmodels';
import { ApiService } from 'src/app/services/api.service';

@Component({
  selector: 'app-imu',
  templateUrl: './imu.component.html',
  styleUrls: ['./imu.component.css']
})
export class IMUComponent implements OnInit {
  IMUDATA: IMUData[] = [];

  dataSourceIMU:any;

  displayedColumns: string[] = ['date', 'direction'];

  @ViewChild('imuDataPaginator') paginator: MatPaginator;

  constructor(private apiService:ApiService) { }

  getData():void{
    this.apiService.getImuData().subscribe((data)=>{this.IMUDATA=data;
      this.handleData(data);});
  }
  ngOnInit(): void {
    this.getData();
  }

  handleData(data:IMUData[]){
this.dataSourceIMU = new MatTableDataSource(this.IMUDATA);

    setTimeout(() => this.dataSourceIMU.paginator = this.paginator);

}
//test : IMUData;
insertTest(){
   let test = {
    imu_id: "test",
    date: new Date(),
    direction: "TO THE MOON"
  };
  this.apiService.insertImuData(test);
  this.getData();
}

}
