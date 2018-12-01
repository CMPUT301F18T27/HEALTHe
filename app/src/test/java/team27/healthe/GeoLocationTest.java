package team27.healthe;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;

import team27.healthe.model.GeoLocation;

import static org.junit.Assert.*;

public class GeoLocationTest {
    @Test
    public void testNewGeoLocConstructor() {
        double lon = 63.6786;
        double lat = 38.1235;
        LatLng latlng = new LatLng(lat,lon);

        GeoLocation g1 = new GeoLocation(lat, lon);
        assertEquals(lat, g1.getLatLng().latitude, 0.001);
        assertEquals(lon, g1.getLatLng().longitude, 0.001);

        GeoLocation g2 = new GeoLocation(latlng);
        assertEquals(latlng, g2.getLatLng());
    }

    @Test
    public void testName() {
        double lon = 63.6786;
        double lat = 38.1235;
        LatLng latlng = new LatLng(lat,lon);
        GeoLocation g = new GeoLocation(latlng);

        // TESTING SETTERS AND GETTERS FOR NAME
        assertEquals(null, g.getName());
        String name = "New GeoLocation";
        g.setName(name);
        assertEquals(name, g.getName());
    }

    @Test
    public void testLatLng() {
        double lon = 63.6786;
        double lat = 38.1235;
        LatLng latlng = new LatLng(lat,lon);
        GeoLocation g = new GeoLocation(lat, lon);

        // TESTING SETTERS AND GETTERS FOR LATLONG
        assertEquals(latlng, g.getLatLng());
        LatLng newLatLng = new LatLng(24.65, 12.2864);
        g.setLatLng(newLatLng);
        assertEquals(newLatLng, g.getLatLng());

        assertEquals(newLatLng, g.getLatLng());
        double newLat = 86.23;
        double newLon = 152.6423;
        g.setLatLng(newLat, newLon);

        assertEquals(newLat, g.getLatLng().latitude, 0.001);
        assertEquals(newLon, g.getLatLng().longitude, 0.001);
    }

}
