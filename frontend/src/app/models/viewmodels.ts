export interface Log {
    value: string;
  }

export interface SensorData{
    date: string;
    latitude: number;
    longtitude: number;
    speed: number;
    maxSpeed:number;
    shakeDegree:number;
}

export interface SignData{
    date: string;
    latitude: number;
    longtitude: number;
    sign: string;
}

export interface ScrapeData{
    type:number;
    description:string;
}