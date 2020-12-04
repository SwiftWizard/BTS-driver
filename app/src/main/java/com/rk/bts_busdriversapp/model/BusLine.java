package com.rk.bts_busdriversapp.model;


public class BusLine {

    private String name;
    private String direction;
    private String scheduleWorkDays;
    private String scheduleSaturday;
    private String scheduleSunday;

    public BusLine() {
    }

    public BusLine(String name, String direction, String scheduleWorkDays, String scheduleSaturday, String scheduleSunday) {
        this.name = name;
        this.direction = direction;
        this.scheduleWorkDays = scheduleWorkDays;
        this.scheduleSaturday = scheduleSaturday;
        this.scheduleSunday = scheduleSunday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getScheduleWorkDays() {
        return scheduleWorkDays;
    }

    public void setScheduleWorkDays(String scheduleWorkDays) {
        this.scheduleWorkDays = scheduleWorkDays;
    }

    public String getScheduleSaturday() {
        return scheduleSaturday;
    }

    public void setScheduleSaturday(String scheduleSaturday) {
        this.scheduleSaturday = scheduleSaturday;
    }

    public String getScheduleSunday() {
        return scheduleSunday;
    }

    public void setScheduleSunday(String scheduleSunday) {
        this.scheduleSunday = scheduleSunday;
    }
}
