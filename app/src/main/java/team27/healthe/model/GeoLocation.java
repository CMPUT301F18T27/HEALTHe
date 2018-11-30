package team27.healthe.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Represents a geolocation
 * @author Tamara
 */
public class GeoLocation {
    private LatLng lat_lng;

    private String name;

    public GeoLocation(double lat, double lon) {
        lat_lng = new LatLng(lat,lon);
    }

    public GeoLocation(LatLng latlng) {
        lat_lng = latlng;
    }

    public LatLng getLatLng() {
        return lat_lng;
    }

    public void setLatLng(double lat, double lon) {
        this.lat_lng = new LatLng(lat, lon);
    }

    public void setLatLng(LatLng lat_lng) {
        this.lat_lng = lat_lng;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
