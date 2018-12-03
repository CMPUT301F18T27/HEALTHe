package team27.healthe;

import android.graphics.Point;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import team27.healthe.model.BodyLocation;
import team27.healthe.model.BodyLocationPhoto;

public class BodyLocationPhotoTest {
    BodyLocationPhoto l;

    @Before
    public void testSetup(){
        l = new BodyLocationPhoto();
    }

    @Test
    public void testNewBodyLocationPhotoConstructor(){
        assertEquals(null, l.getBodyLocation());
        assertEquals(null, l.getBodyLocationPhotoId());
        assertEquals(null, l.getPatientId());
    }

    @Test
    public void testPatientId(){
        // TESTING SETTERS AND GETTERS FOR LOCATION NAME
        assertEquals(null, l.getPatientId());
        String newPatientID = "263sgf5";
        l.setPatientId(newPatientID);
        assertEquals(newPatientID, l.getPatientId());
    }

    @Test
    public void testBodyLocationPhotoId(){
        // TESTING SETTERS AND GETTERS FOR PATIENT ID
        assertEquals(null, l.getBodyLocationPhotoId());
        String newBodyLocationPhotoID = "2674";
        l.setBodyLocationPhotoId(newBodyLocationPhotoID);
        assertEquals(newBodyLocationPhotoID, l.getBodyLocationPhotoId());
    }

    @Test
    public void testBodyLocation(){
        // TESTING SETTERS AND GETTERS FOR BODY LOCATION ID
        assertEquals(null, l.getBodyLocation());
        String newBodyLocation = "Hand";
        l.setBodyLocation(newBodyLocation);
        assertEquals(newBodyLocation, l.getBodyLocation());
    }

}
