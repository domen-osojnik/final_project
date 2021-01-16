import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SensorData, ScrapeData, SignData, Log, IMUData } from '../models/viewmodels';
import { catchError, tap } from "rxjs/operators";
import { throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  public static dataHost = "http://localhost:3000/";

  constructor(private http: HttpClient) { }

   //Pridobivanje Logov
   public getLogData(): Observable<Log[]> {
    return this.http.get<Log[]>(ApiService.dataHost + "getRecordings").pipe(
      tap(() => {}),
      catchError(this.handleError("Log data rertrieval", "404 NOT FOUND"))
    );
  }

     //Pridobivanje Logov
     public getSignData(): Observable<SignData[]> {
      return this.http.get<SignData[]>(ApiService.dataHost + "signData").pipe(
        tap(() => {}),
        catchError(this.handleError("Sign data rertrieval", "404 NOT FOUND"))
      );
    }

       //Pridobivanje Logov
   public getScrapeData(): Observable<ScrapeData[]> {
    return this.http.get<ScrapeData[]>(ApiService.dataHost + "scrapedEvent").pipe(
      tap(() => {}),
      catchError(this.handleError("Scrape data rertrieval", "404 NOT FOUND"))
    );
  }

  public getImuData(): Observable<IMUData[]> {
    return this.http.get<IMUData[]>(ApiService.dataHost + "imu").pipe(
      tap(() => {}),
      catchError(this.handleError("IMU data rertrieval", "404 NOT FOUND"))
    );
  }

  public insertImuData(imuData : IMUData){
    this.http.post<IMUData>(ApiService.dataHost + "imu",  imuData ).subscribe(data => {
      console.log(data);
  })
  }

  //Error handling
  private handleError(operation: String, error: string) {
    return (err: any) => {
      let errMsg = `error in ${operation}() retrieving ${ApiService.dataHost}`;
      console.log(`${errMsg}:`, err);
      if (err instanceof HttpErrorResponse) {
        // you could extract more info about the error if you want, e.g.:
        console.log(`status: ${err.status}, ${err.statusText}`);
        // errMsg = ...
      }
      return throwError(errMsg);
    };
  }
}
