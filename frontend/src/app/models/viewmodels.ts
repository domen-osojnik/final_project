

export interface SensorData{
    date: string;
    latitude: number;
    longtitude: number;
    speed: number;
    maxSpeed:number;
    shakeDegree:number;
}

export interface Log {
    _id: string;
    readings:Array<SensorData>;
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