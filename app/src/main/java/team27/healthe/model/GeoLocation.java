package team27.healthe.model;

import com.google.android.gms.maps.model.LatLng;

import org.elasticsearch.common.geo.GeoPoint;

/**
 * Represents a geolocation
 * @author Chase
 */
public class GeoLocation {
    private GeoPoint geo_point;

    private String name;

    public GeoLocation(double lat, double lon) {
        geo_point = new GeoPoint(lat, lon);
    }

    public GeoLocation(LatLng latlng) {
        geo_point = new GeoPoint(latlng.latitude, latlng.longitude);
    }

    public LatLng getLatLng() {

        return new LatLng(geo_point.getLat(), geo_point.getLon());
    }

    public void setLatLng(double lat, double lon) {

        geo_point.reset(lat, lon);
    }

    public void setLatLng(LatLng lat_lng) {

        geo_point.reset(lat_lng.latitude, lat_lng.longitude);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

}
