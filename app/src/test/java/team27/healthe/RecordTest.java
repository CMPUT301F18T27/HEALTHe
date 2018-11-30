package team27.healthe;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import team27.healthe.model.BodyLocation;
import team27.healthe.model.GeoLocation;
import team27.healthe.model.Record;

import static org.junit.Assert.*;

public class RecordTest {
    @Test
    public void testNewRecordConstructor() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Integer> comments = new ArrayList<Integer>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Integer> images = new ArrayList<>();

        Record r1 = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        assertEquals(title, r1.getTitle());
        assertEquals(rdate, r1.getRdate());
        assertEquals(descr, r1.getDescription());
        assertEquals(comments, r1.getCommentList());
        assertEquals(bodyLoc, r1.getBodyLocation());
        assertEquals(geoLoc, r1.getGeoLocation());
        assertEquals(images, r1.getImageList());

        Record r2 = new Record(title);
        assertEquals(title, r2.getTitle());
        assertNull(r2.getDescription());
        assertNull(r2.getCommentList());
        assertNull(r2.getBodyLocation());
        assertNull(r2.getGeoLocation());
        assertNull(r2.getImageList());
    }

    @Test
    public void testTitle() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Integer> comments = new ArrayList<Integer>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Integer> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR TITLE
        assertEquals(r.getTitle(), title);
        String newTitle = "Healed Burn Mark";
        r.setTitle(newTitle);
        assertEquals(newTitle, r.getTitle());
    }

    @Test
    public void testDate() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Integer> comments = new ArrayList<Integer>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Integer> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR DATE
        assertEquals(r.getRdate(), rdate);
        Date newDate = Calendar.getInstance().getTime();
        r.setRdate(newDate);
        assertEquals(newDate, r.getRdate());
    }

    @Test
    public void testDescription() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Integer> comments = new ArrayList<Integer>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Integer> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR DESCRIPTION
        assertEquals(r.getDescription(), descr);
        String newDesc = "This is a minor initial burn I got by touching a hot iron.";
        r.setDescription(newDesc);
        assertEquals(newDesc, r.getDescription());
    }

    @Test
    public void testComments() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Integer> comments = new ArrayList<Integer>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Integer> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR DESCRIPTION
        assertEquals(r.getCommentList(), comments);
        ArrayList<Integer> newComments = new ArrayList<Integer>();
        r.setCommentList(newComments);
        assertEquals(newComments, r.getCommentList());
    }

    @Test
    public void testBodyLocation() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Integer> comments = new ArrayList<Integer>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Integer> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR DESCRIPTION
        assertEquals(r.getBodyLocation(), bodyLoc);
        BodyLocation newBodyLoc = new BodyLocation(1,1);
        r.setBodyLocation(newBodyLoc);
        assertEquals(newBodyLoc, r.getBodyLocation());
    }

    @Test
    public void testGeoLocation() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Integer> comments = new ArrayList<Integer>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Integer> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR DESCRIPTION
        assertEquals(r.getGeoLocation(), geoLoc);
        GeoLocation newGeoLoc = new GeoLocation(94.56, 1.678);
        r.setGeoLocation(newGeoLoc);
        assertEquals(newGeoLoc, r.getGeoLocation());
    }

    @Test
    public void testImages() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Integer> comments = new ArrayList<Integer>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Integer> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR DESCRIPTION
        assertEquals(r.getImageList(), images);
        ArrayList<Integer> newImages = new ArrayList<>();
        r.setImageList(newImages);
        assertEquals(newImages, r.getImageList());
    }



}
