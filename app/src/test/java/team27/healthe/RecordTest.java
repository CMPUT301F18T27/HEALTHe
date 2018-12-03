package team27.healthe;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import team27.healthe.model.BodyLocation;
import team27.healthe.model.Comment;
import team27.healthe.model.GeoLocation;
import team27.healthe.model.Photo;
import team27.healthe.model.Record;

import static org.junit.Assert.*;

public class RecordTest {
    @Test
    public void testNewRecordConstructor() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Comment> comments = new ArrayList<>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Photo> images = new ArrayList<>();

        Record r1 = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        assertEquals(title, r1.getTitle());
        assertEquals(rdate, r1.getRdate());
        assertEquals(descr, r1.getDescription());
        assertEquals(comments, r1.getCommentList());
        assertEquals(bodyLoc, r1.getBodyLocation());
        assertEquals(geoLoc, r1.getGeoLocation());
        assertEquals(images, r1.getPhotos());
        assert(r1.getRecordID() instanceof String);

        Record r2 = new Record(title, rdate, descr);
        assertEquals(title, r2.getTitle());
        assertEquals(rdate, r2.getRdate());
        assertEquals(descr, r2.getDescription());
        assertEquals(new ArrayList<String>(), r2.getCommentList());
        assert(r2.getBodyLocation() instanceof BodyLocation);
        assertNull(r2.getGeoLocation());
        assertEquals(new ArrayList<>(), r2.getPhotos());
        assert(r2.getRecordID() instanceof String);

        Record r3 = new Record(comments);
        assertEquals("Test title", r3.getTitle());
        assert(r3.getRdate() instanceof Date);
        assertEquals("This is a record", r3.getDescription());
        assertEquals(comments, r3.getCommentList());
        assert(r2.getBodyLocation() instanceof BodyLocation);
        assertNull(r2.getGeoLocation());
        assertEquals(new ArrayList<>(), r2.getPhotos());
        assert(r2.getRecordID() instanceof String);
    }

    @Test
    public void testTitle() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Comment> comments = new ArrayList<>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Photo> images = new ArrayList<>();

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
        ArrayList<Comment> comments = new ArrayList<>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Photo> images = new ArrayList<>();

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
        ArrayList<Comment> comments = new ArrayList<>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Photo> images = new ArrayList<>();

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
        ArrayList<Comment> comments = new ArrayList<>();
        comments.add(new Comment("Hurts a lot", "243t"));
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Photo> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR COMMENTS
        assertEquals(r.getCommentList(), comments);
        ArrayList<Comment> newComments = new ArrayList<>();
        newComments.add(new Comment("Hurts a little less","243t"));
        r.setCommentList(newComments);
        assertEquals(newComments, r.getCommentList());
    }

    @Test
    public void testAddSingleComment() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Comment> comments = new ArrayList<>();
        comments.add(new Comment("Hurts a lot", "243t"));
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Photo> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);
        // TESTING ADDING SINGLE COMMENT
        int commentCount = r.getCommentList().size();
        r.addCommment(new Comment("new comment", "34f4"));
        assertEquals(commentCount+1, r.getCommentList().size());
    }

    @Test
    public void testRemovingSingleComment() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Comment> comments = new ArrayList<>();
        comments.add(new Comment("Hurts a lot", "243t"));
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Photo> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING REMOVING SINGLE COMMENT
        int commentCount = r.getCommentList().size();
        Comment newComment = new Comment("new comment", "34f4");
        r.addCommment(newComment);
        assertEquals(commentCount+1, r.getCommentList().size());

        commentCount = r.getCommentList().size();
        r.removeComment(newComment);
        assertEquals(commentCount-1, r.getCommentList().size());
    }

    @Test
    public void testBodyLocation() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Comment> comments = new ArrayList<>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Photo> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR BODYLOCATION
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
        ArrayList<Comment> comments = new ArrayList<>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Photo> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR GEOLOCATION
        assertEquals(r.getGeoLocation(), geoLoc);
        GeoLocation newGeoLoc = new GeoLocation(94.56, 1.678);
        r.setGeoLocation(newGeoLoc);
        assertEquals(newGeoLoc, r.getGeoLocation());
    }

    @Test
    public void testPhotos() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Comment> comments = new ArrayList<>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Photo> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR PHOTOS
        assertEquals(r.getPhotos(), images);
        ArrayList<Photo> newImages = new ArrayList<>();
        r.setPhotos(newImages);
        assertEquals(newImages, r.getPhotos());
    }

    @Test
    public void testAddSinglePhoto() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Comment> comments = new ArrayList<>();
        comments.add(new Comment("Hurts a lot", "243t"));
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Photo> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);
        // TESTING ADDING SINGLE COMMENT
        int photoCount = r.getPhotos().size();
        r.addPhoto(new Photo());
        assertEquals(photoCount+1, r.getPhotos().size());
    }

    @Test
    public void testRecordID() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Comment> comments = new ArrayList<>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Photo> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR RECORD ID
        String recordID = "23f45";
        r.setRecordID(recordID);
        assertEquals(recordID, r.getRecordID());
    }

    @Test
    public void testHeartRate() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        ArrayList<Comment> comments = new ArrayList<>();
        BodyLocation bodyLoc = new BodyLocation(1,1);
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ArrayList<Photo> images = new ArrayList<>();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR HEART RATE
        String heartRate = "74";
        r.setHeartRate(heartRate);
        assertEquals(heartRate, r.getHeartRate());
    }

}
