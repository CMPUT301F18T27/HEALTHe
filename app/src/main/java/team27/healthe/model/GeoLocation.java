package team27.healthe.model;

import com.google.android.gms.maps.model.LatLng;

import org.elasticsearch.common.geo.GeoPoint;

/**
 * Represents a geolocation
 * @author Chase
 * @author Cody
 */
public class GeoLocation {
    private GeoPoint location;

    private String name;

    public GeoLocation(double lat, double lon) {
        location = new GeoPoint(lat, lon);
    }

    public GeoLocation(LatLng latlng) {
        location = new GeoPoint(latlng.latitude, latlng.longitude);
    }

    public LatLng getLatLng() {

        return new LatLng(location.getLat(), location.getLon());
    }

    public void setLatLng(double lat, double lon) {

        location.reset(lat, lon);
    }

    public void setLatLng(LatLng lat_lng) {

        location.reset(lat_lng.latitude, lat_lng.longitude);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
