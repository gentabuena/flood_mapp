package com.fea.floodmapp.main.datamodels;

public class EvacuationSitesModel {

    private int id;
    private String name;
    private String description;
    private String address;
    private String latitude;
    private String longitude;
    private int population;
    private String created_at;
    private String updated_at;
    private int status;
    private String distance;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getPopulation() {
        return population;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getDistance() {
        return distance;
    }

    public int getStatus() {
        return status;
    }
}
