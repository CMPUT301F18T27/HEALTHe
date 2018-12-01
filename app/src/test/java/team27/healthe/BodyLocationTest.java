package team27.healthe;

import android.graphics.Point;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import team27.healthe.model.BodyLocation;

public class BodyLocationTest {
    BodyLocation l;
    float x;
    float y;

    @Before
    public void testSetup(){
        x = 10;
        y = 11;

        l = new BodyLocation(x, y);
    }

    @Test
    public void testNewBodyLocationConstructor(){
        assertEquals(l.getX(), x, 0.001);
        assertEquals(l.getY(), y, 0.001);
        assertEquals("", l.getLocationName());

        BodyLocation new_l = new BodyLocation();
        assertEquals(new_l.getX(), 0, 0.001);
        assertEquals(new_l.getX(), 0, 0.001);
        assertEquals(new_l.getLocationName(), "");

    }

    @Test
    public void testLocationName(){
        // TESTING SETTERS AND GETTERS FOR LOCATION NAME
        assertEquals(null, l.getLocationName());
        String newName = "Hand";
        l.setLocation(newName);
        assertEquals(newName, l.getLocationName());
    }

    @Test
    public void testPatientId(){
        // TESTING SETTERS AND GETTERS FOR PATIENT ID
        assertEquals("", l.getPatientId());
        String newPatientId = "2674";
        l.setPatientId(newPatientId);
        assertEquals(newPatientId, l.getPatientId());
    }

    @Test
    public void testBodyLocationId(){
        // TESTING SETTERS AND GETTERS FOR BODY LOCATION ID
        assertEquals(null, l.getBodyLocationId());
        String newBodyId = "2w461";
        l.setBodyLocationId(newBodyId);
        assertEquals(newBodyId, l.getBodyLocationId());
    }

}
