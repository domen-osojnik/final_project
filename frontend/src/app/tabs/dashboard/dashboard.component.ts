import { Component, OnInit, ViewChild } from '@angular/core';
import { ScrapeData, TrafficRoadWork, TrafficRoadClosed, TrafficOther } from 'src/app/models/viewmodels';
import { ApiService } from 'src/app/services/api.service';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  scrapeLog: ScrapeData[] = [];
  roadWorkData:TrafficRoadWork[]=[];
  closedRoadData: TrafficRoadClosed[]=[];
  otherData : TrafficOther[] = [];

  dataSourceWorkData:any;
  dataSourceClosedRoad:any;
  dataSourceOtherData:any;


  displayedColumns: string[] = ['roadmark', 'desc'];

  @ViewChild('otherRoadDataPaginator') paginator: MatPaginator;
  @ViewChild('workRoadDataPaginator') paginator1: MatPaginator;
  @ViewChild('closedRoadDataPaginator') paginator2: MatPaginator;

  constructor(private apiService:ApiService,
    ) { }

  ngOnInit(): void {
    this.getData();
  }

  getData():void{
    this.apiService.getScrapeData().subscribe((data)=>{this.scrapeLog=data;
      this.handleData(data);});
    
  }

  handleData(data:ScrapeData[]){
      data.forEach(scrapeEvent => {
          if(scrapeEvent.type == "delo")this.roadWorkData.push({desc : scrapeEvent.desc, roadmark : scrapeEvent.roadmark});
          if(scrapeEvent.type == "zaprta")this.closedRoadData.push({desc : scrapeEvent.desc, roadmark : scrapeEvent.roadmark});
          else this.otherData.push({desc : scrapeEvent.desc, roadmark : scrapeEvent.roadmark});
      });

      this.dataSourceWorkData = new MatTableDataSource(this.roadWorkData);
      this.dataSourceClosedRoad = new MatTableDataSource(this.closedRoadData);
      this.dataSourceOtherData = new MatTableDataSource(this.otherData);

      setTimeout(() => this.dataSourceOtherData.paginator = this.paginator);
      setTimeout(() => this.dataSourceWorkData.paginator = this.paginator1);
      setTimeout(() => this.dataSourceClosedRoad.paginator = this.paginator2);
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSourceWorkData.filter = filterValue.trim().toLowerCase();
    this.dataSourceClosedRoad.filter = filterValue.trim().toLowerCase();
    this.dataSourceOtherData.filter = filterValue.trim().toLowerCase();
  }
}
