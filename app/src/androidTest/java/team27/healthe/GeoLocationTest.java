package team27.healthe;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class GeoLocationTest {
    @Test
    public void testNewGeoLocContructor() {
        double lon = 63.6786;
        double lat = 38.1235;

        GeoLocation g = new GeoLocation(lon, lat);

        assertEquals(lat, g.getLatitude(), 0.001);
        assertEquals(lon, g.getLongitude(), 0.001);
    }

    @Test
    public void testLongitude() {
        double lon = 63.6786;
        double lat = 38.1235;

        GeoLocation g = new GeoLocation(lon, lat);

        // TESTING SETTERS AND GETTERS FOR TITLE
        assertEquals(g.getLongitude(), lon, 0.001);
        double newLon = 24.6878;
        g.setLongitude(newLon);
        assertEquals(newLon, g.getLongitude(), 0.001);
    }

    @Test
    public void testLatitude() {
        double lon = 63.6786;
        double lat = 38.1235;

        GeoLocation g = new GeoLocation(lon, lat);

        // TESTING SETTERS AND GETTERS FOR TITLE
        assertEquals(g.getLatitude(), lat, 0.001);
        double newLat = 56.2342;
        g.setLatitude(newLat);
        assertEquals(newLat, g.getLongitude(), 0.001);
    }

}
