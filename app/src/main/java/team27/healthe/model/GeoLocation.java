package team27.healthe.model;

public class GeoLocation {
    private String name;
    private double latitude;
    private double longitude;

    public GeoLocation(double lat, double lon) {
        latitude = lat;
        longitude = lon;
    }

    public void setName(String name) {}

    public String getName() {
        return null;
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
}
