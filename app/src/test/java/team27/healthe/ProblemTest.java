package team27.healthe;

import android.util.ArraySet;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import team27.healthe.model.ElasticSearchController;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;

import static android.os.SystemClock.sleep;
import static org.junit.Assert.*;

public class ProblemTest {
    @Test
    public void testNewProblemConstructor() {
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        Collection<String> records = new ArrayList<>();
        ArrayList<String> comments = new ArrayList<>();
        comments.add("Hurts a lot");
        records.add(new Record(comments).getRecordID());
        Problem p1 = new Problem(title, pdate, description, records);

        assertEquals(title, p1.getTitle());
        assertEquals(pdate, p1.getPdate());
        assertEquals(description, p1.getDescription());

        Problem p2 = new Problem(title, description, records);
        assertEquals(title, p2.getTitle());
        assertEquals(description, p2.getDescription());
    }

    @Test
    public void testTitle() {
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        Collection<String> records = new ArrayList<>();
        ArrayList<String> comments = new ArrayList<>();
        comments.add("Hurts a lot");
        records.add(new Record(comments).getRecordID());
        Problem p = new Problem(title, pdate, description, records);

        // TESTING SETTERS AND GETTERS FOR TITLE
        assertEquals(p.getTitle(), title);
        String newTitle = "Leg Burn";
        p.setTitle(newTitle);
        assertEquals(newTitle, p.getTitle());
    }

    @Test
    public void testDate() {
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        Collection<String> records = new ArrayList<>();
        ArrayList<String> comments = new ArrayList<>();
        comments.add("Hurts a lot");
        records.add(new Record(comments).getRecordID());
        Problem p = new Problem(title, pdate, description, records);

        // TESTING SETTERS AND GETTERS FOR DATE
        assertEquals(p.getPdate(), pdate);
        Date newDate = Calendar.getInstance().getTime();
        p.setPdate(newDate);
        assertEquals(newDate, p.getPdate());
    }

    @Test
    public void testDescription() {
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        Collection<String> records = new ArrayList<>();
        ArrayList<String> comments = new ArrayList<>();
        comments.add("Hurts a lot");
        records.add(new Record(comments).getRecordID());
        Problem p = new Problem(title, pdate, description, records);

        // TESTING SETTERS AND GETTERS FOR DESCRIPTION
        assertEquals(p.getDescription(), description);
        String newDesc = "I have a burn on the back of my hand.";
        p.setDescription(newDesc);
        assertEquals(newDesc, p.getDescription());
    }

    @Test
    public void testRecords() {
        /* Problem must contain at least 1 record. */
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        Collection<String> records = new ArrayList<>();
        ArrayList<String> comments = new ArrayList<>();
        comments.add("Hurts a lot");
        records.add(new Record(comments).getRecordID());
        Problem p = new Problem(title, pdate, description, records);

        // TESTING SETTERS AND GETTERS FOR RECORDS
        assertEquals(p.getRecords(), records);
        Collection<String> newRecords = new ArrayList<>();
        ArrayList<String> newComments = new ArrayList<>();
        newComments.add("Healed burn mark");
        newRecords.add((new Record(newComments)).getRecordID());
        p.setRecords(newRecords);
        assertEquals(newRecords, p.getRecords());
    }

    @Test
    public void testNumberOfRecords() {
        /* Problem must contain at least 1 record. */
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        Collection<String> records = new ArrayList<>();
        ArrayList<String> comments = new ArrayList<>();
        comments.add("Hurts a lot");
        records.add(new Record(comments).getRecordID());
        Problem p = new Problem(title, pdate, description, records);

        // TESTING GETTING NUMBER OF RECORDS
        assertEquals(p.getNumberOfRecords(), 1);
        ArrayList<String> newComments = new ArrayList<>();
        newComments.add("Healed burn mark");
        records.add(new Record(newComments).getRecordID());
        assertEquals(p.getNumberOfRecords(), 2);
    }

    @Test
    public void testCompareProblems() {
        String title1 = "Hand Burn";
        Date pdate1 = Calendar.getInstance().getTime();
        String description1 = "I have a burn on the palm of my hand.";
        Collection<String> records1 = new ArrayList<>();
        ArrayList<String> comments1 = new ArrayList<>();
        comments1.add("Initial burn mark");
        records1.add(new Record(comments1).getRecordID());
        Problem p1 = new Problem(title1, pdate1, description1, records1);

        sleep(3000);

        String title2 = "Hand Burn";
        Date pdate2 = Calendar.getInstance().getTime();
        String description2 = "I have a burn on the palm of my hand.";
        Collection<String> records2 = new ArrayList<>();
        ArrayList<String> comments2 = new ArrayList<>();
        comments2.add("Hurts a lot");
        records2.add(new Record(comments2).getRecordID());
        Problem p2 = new Problem(title2, pdate2, description2, records2);

        assertTrue(p1.compareTo(p2) < 0);
    }


}
