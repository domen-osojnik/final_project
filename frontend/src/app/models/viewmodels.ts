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
export interface TrafficRoadWork{
    roadmark:string;
    desc:string;
}

export interface TrafficRoadClosed{
    roadmark:string;
    desc:string;
}

export interface TrafficOther{
    roadmark:string;
    desc:string;
}

export interface Marker{
    position:any;
    title:string;
    map:any;
}

export interface IMUData{
    imu_id : string;
    date: Date;
    direction : string;
}
