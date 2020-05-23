import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { Log } from 'src/app/models/viewmodels';

@Component({
  selector: 'app-drive-statistics',
  templateUrl: './drive-statistics.component.html',
  styleUrls: ['./drive-statistics.component.css']
})
export class DriveStatisticsComponent implements OnInit {
  //#region nastavitve grafa
  public chartLabels: Array<any> = ['Normalno stanje', 'Lažji tresljaj', 'Srednji tresljaj', 'Hujši tresljaj'];
  public chartType: string = 'bar';
  public chartDatasets: Array<any> = [
    
  ];

  public chartColors: Array<any> = [
    {
      backgroundColor: [
        'rgba(75, 192, 192, 0.2)',
        'rgba(255, 206, 86, 0.2)',
        'rgba(255, 159, 64, 0.2)',
        'rgba(255, 99, 132, 0.2)',
      ],
      borderColor: [
        'rgba(75, 192, 192, 1)',
        'rgba(255, 206, 86, 1)',
        'rgba(255, 159, 64, 1)',
        'rgba(255,99,132,1)',
      ],
      borderWidth: 2,
    }
  ];

  public chartOptions: any = {
    responsive: true
  };
  public chartClicked(e: any): void { }
  public chartHovered(e: any): void { }
  //#endregion
  constructor(public dialogRef: MatDialogRef<DriveStatisticsComponent>, @Inject(MAT_DIALOG_DATA) public data: Log) { }

  closeDialog() {
    this.dialogRef.close('Statistični podatki zaprti!');
  }

  ngOnInit(): void {
    this.renderData();
  }

  renderData(){
    let normalStatus = 0, easyDegreeCount = 0,heavyDegreeCount = 0, mediumDegreeCount = 0;

    this.data.events.forEach(data => {
        if(data.degree==1)easyDegreeCount++
        if(data.degree==2)mediumDegreeCount++
        if(data.degree==3)heavyDegreeCount++
        else normalStatus++
    });

    this.chartDatasets = [{ data: [normalStatus, easyDegreeCount, mediumDegreeCount, heavyDegreeCount], label: 'Podatki' }];
  }

}
