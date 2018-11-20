package team27.healthe.model;

/**
 * Represents a geolocation
 * @author Tamara
 */
public class GeoLocation {
    private double latitude;
    private double longitude;

    private String name;

    public GeoLocation(double lat, double lon) {
        latitude = lat;
        longitude = lon;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double lat) {
        this.latitude = lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double lon) {
        this.longitude = lon;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
