package edu.ucsb.cs.cs190i.jsegovia.getmethere;

import java.sql.Time;

/**
 * Created by issacholguin1 on 6/10/17.
 */

public class Event {

    String location;
    String name;

    public String getEstTime() {
        return estTime;
    }

    public void setEstTime(String estTime) {
        this.estTime = estTime;
    }

    String estTime;


    Double startLng, startLat, eventLat, eventLng;
    Time eventStart, eventEnd;

    public Event(String name, String location, Time eventStart, Time eventEnd) {
        this.name = name;
        this.location = location;
        this.eventStart = eventStart;
        this.eventEnd = eventEnd;
    }




    public Time getEventStart() {
        return eventStart;
    }

    public void setEventStart(Time eventStart) {
        this.eventStart = eventStart;
    }

    public Time getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(Time eventEnd) {
        this.eventEnd = eventEnd;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getStartLng() {
        return startLng;
    }

    public void setStartLng(Double startLng) {
        this.startLng = startLng;
    }

    public Double getStartLat() {
        return startLat;
    }

    public void setStartLat(Double startLat) {
        this.startLat = startLat;
    }

    public Double getEventLat() {
        return eventLat;
    }

    public void setEventLat(Double eventLat) {
        this.eventLat = eventLat;
    }

    public Double getEventLng() {
        return eventLng;
    }

    public void setEventLng(Double eventLng) {
        this.eventLng = eventLng;
    }
}
