import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { ApiService } from 'src/app/services/api.service';
import { Log, Marker } from 'src/app/models/viewmodels';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialog } from '@angular/material/dialog';
import { DriveStatisticsComponent } from './drive-statistics/drive-statistics.component';

@Component({
  selector: 'app-drive-information',
  templateUrl: './drive-information.component.html',
  styleUrls: ['./drive-information.component.css']
})
export class DriveInformationComponent implements OnInit {
  selectedLog:Log;
  //#region MAPS
  @ViewChild('mapContainer', { static: false }) gmap: ElementRef;
  map: google.maps.Map;
  lat = 46.364567;
  lng = 15.1077635;

  coordinates = new google.maps.LatLng(this.lat, this.lng);

  markers: Marker[] = [];

  mapOptions: google.maps.MapOptions = {
    center: this.coordinates,
    zoom: 8
  };


  mapInitializer() {
    this.map = new google.maps.Map(this.gmap.nativeElement, 
    this.mapOptions);
     //Adding other markers
    this.loadAllMarkers();
  }

  loadAllMarkers(): void {
    this.markers.forEach(markerInfo => {
      //Creating a new marker object
      const marker = new google.maps.Marker({
        ...markerInfo
      });

      //creating a new info window with markers info
      const infoWindow = new google.maps.InfoWindow({
        content: marker.getTitle()
      });

      //Add click event to open info window on marker
      marker.addListener("click", () => {
        infoWindow.open(marker.getMap(), marker);
      });

      //Adding marker to google map
      marker.setMap(this.map);
    });
  }

  clearMarkers():void{
    this.markers = [];
  }
  //#endregion
  
  //#region TABLE DATA
  dataSource:any;
  logs: Log[] = [];
  displayedColumns: string[] = ['date', 'lat', 'long', 'speed', 'degreeStatus'];
  logData:string="";
  @ViewChild('dataSourcePaginator') paginator: MatPaginator;
  //#endregion
 

  constructor(private apiService:ApiService, public dialog: MatDialog) { }

  ngOnInit(): void {
    this.getData();
  }

  getData():void{
    this.apiService.getLogData().subscribe(data=>{this.logs=data;
    this.selectLog(this.logs[0]._id)});
  }

  selectLog(id:string) {
    this.logs.forEach(log => {
      this.logData = log.date;
      if(id == log._id){
        this.handleData(log);};
    });
  }

  handleData(data:Log){ 
      this.clearMarkers();

      data.events.forEach(data => {
        if(data.degree == 1)data.degreeStatus = "Lajši tresljaj"
        else if(data.degree == 2)data.degreeStatus = "Hujši tresljaj"
        else data.degreeStatus = "Normalno stanje"
        this.markers.push({position: new google.maps.LatLng(data.lat, data.long), title: data.degreeStatus, map:this.map});
      });
      
      this.mapInitializer();
      this.dataSource = new MatTableDataSource(data.events);
      setTimeout(() => this.dataSource.paginator = this.paginator);
      this.selectedLog = data;
  }

  public openDetailsDialog(){
    let dialogRef = this.dialog.open(DriveStatisticsComponent, {
      data: this.selectedLog
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log(`${result}`); // Pizza!
    });
    
  }
}
