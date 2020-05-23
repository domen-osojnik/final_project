export interface SensorData{
    date: string;
    lat: number;
    long: number;
    speed: number;
    degree:number;
    degreeStatus:string;
    _id:string;
}

export interface Log {
    date:string;
    _id: string;
    events:Array<SensorData>;
  }

export interface SignData{
    date: string;
    latitude: number;
    longtitude: number;
    sign: string;
}

export interface ScrapeData{
    roadmark:string;
    desc:string;
    type:string
}
