package team27.healthe;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class RecordTest {
    @Test
    public void testNewRecordContructor() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        CommentList comments = new CommentList();
        BodyLocation bodyLoc = new BodyLocation("head");
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ImageList images = new ImageList();

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
        CommentList comments = new CommentList();
        BodyLocation bodyLoc = new BodyLocation("head");
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ImageList images = new ImageList();

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
        CommentList comments = new CommentList();
        BodyLocation bodyLoc = new BodyLocation("head");
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ImageList images = new ImageList();

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
        CommentList comments = new CommentList();
        BodyLocation bodyLoc = new BodyLocation("head");
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ImageList images = new ImageList();

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
        CommentList comments = new CommentList();
        BodyLocation bodyLoc = new BodyLocation("head");
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ImageList images = new ImageList();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR DESCRIPTION
        assertEquals(r.getCommentList(), comments);
        CommentList newComments = new CommentList();
        r.setCommentList(newComments);
        assertEquals(newComments, r.getCommentList());
    }

    @Test
    public void testBodyLocation() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        CommentList comments = new CommentList();
        BodyLocation bodyLoc = new BodyLocation("head");
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ImageList images = new ImageList();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR DESCRIPTION
        assertEquals(r.getBodyLocation(), bodyLoc);
        BodyLocation newBodyLoc = new BodyLocation("head");
        r.setBodyLocation(newBodyLoc);
        assertEquals(newBodyLoc, r.getBodyLocation());
    }

    @Test
    public void testGeoLocation() {
        String title = "Initial Burn Mark";
        Date rdate = Calendar.getInstance().getTime();
        String descr = "This is a severe initial burn I got my touching the stove.";
        CommentList comments = new CommentList();
        BodyLocation bodyLoc = new BodyLocation("head");
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ImageList images = new ImageList();

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
        CommentList comments = new CommentList();
        BodyLocation bodyLoc = new BodyLocation("head");
        GeoLocation geoLoc = new GeoLocation(23.1313, 75.1235);
        ImageList images = new ImageList();

        Record r = new Record(title, rdate, descr, comments, bodyLoc, geoLoc, images);

        // TESTING SETTERS AND GETTERS FOR DESCRIPTION
        assertEquals(r.getImageList(), images);
        ImageList newImages = new ImageList();
        r.setImageList(newImages);
        assertEquals(newImages, r.getImageList());
    }



}
