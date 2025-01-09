package com.example.a;

public class VisitedPlace {

    public String placeId;
    public String name;
    public String imageUrl;

    public VisitedPlace() {
    }

    public VisitedPlace(String placeId, String name, String imageUrl) {
        this.placeId = placeId;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
