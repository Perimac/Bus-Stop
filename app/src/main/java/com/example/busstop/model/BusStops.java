package com.example.busstop.model;

public class BusStops {
    public String bsId;
    public String journey;
    public String destination;
    public String dest_latitude;
    public String dest_longitude;

    public BusStops() {
    }

    public String getBsId() {
        return bsId;
    }

    public void setBsId(String bsId) {
        this.bsId = bsId;
    }

    public String getJourney() {
        return journey;
    }

    public void setJourney(String journey) {
        this.journey = journey;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDest_latitude() {
        return dest_latitude;
    }

    public void setDest_latitude(String dest_latitude) {
        this.dest_latitude = dest_latitude;
    }

    public String getDest_longitude() {
        return dest_longitude;
    }

    public void setDest_longitude(String dest_longitude) {
        this.dest_longitude = dest_longitude;
    }


}
