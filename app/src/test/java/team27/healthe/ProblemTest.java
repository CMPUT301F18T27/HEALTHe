package team27.healthe;

import org.elasticsearch.common.UUID;
import org.elasticsearch.common.mvel2.util.ArrayTools;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import team27.healthe.model.Comment;
import team27.healthe.model.Patient;
import team27.healthe.model.Problem;
import team27.healthe.model.Record;

import static android.os.SystemClock.sleep;
import static org.junit.Assert.*;

public class ProblemTest {

    Problem p;
    String title;
    Date pdate;
    String description;
    Collection<String> records;
    Patient patient;
    ArrayList<Comment> comments;

    @Before
    public void setup() {
        patient = new Patient("patient", "patient@gmail.com", "7802345678");
        title = "Hand Burn";
        pdate = Calendar.getInstance().getTime();
        description = "I have a burn on the palm of my hand.";
        comments = new ArrayList<>();
        comments.add(new Comment("newcomment", patient.getUserid()));
        records = new ArrayList<>();
        records.add(new Record(comments).getRecordID());

        p = new Problem(title, pdate, description, records);
    }

    @Test
    public void testNewProblemConstructor() {
        Patient patient = new Patient("patient", "patient@gmail.com", "7802345678");
        String title = "Hand Burn";
        Date pdate = Calendar.getInstance().getTime();
        String description = "I have a burn on the palm of my hand.";
        ArrayList<Comment> comments = new ArrayList<>();
        comments.add(new Comment("newcomment", patient.getUserid()));
        Collection<String> records = new ArrayList<>();
        records.add(new Record(comments).getRecordID());

        Problem p = new Problem();
        assertEquals("New Problem", p.getTitle());
        assert(p.getPdate() instanceof Date);
        assertEquals("", p.getDescription());
        assertEquals(new ArrayList<Record>(), p.getRecords());
        assert(p.getProblemID() instanceof String);
        assertEquals("", p.getPatientID());

        Problem p1 = new Problem(title, pdate, description);
        assertEquals(title, p1.getTitle());
        assertEquals(pdate, p1.getPdate());
        assertEquals(description, p1.getDescription());

        Problem p2 = new Problem(title, pdate, description, patient.getUserid());
        assertEquals(title, p2.getTitle());
        assertEquals(pdate, p2.getPdate());
        assertEquals(description, p2.getDescription());
        assertEquals(patient.getUserid(), p2.getPatientID());

        Problem p3 = new Problem(title, pdate, description, records);
        assertEquals(title, p3.getTitle());
        assertEquals(pdate, p3.getPdate());
        assertEquals(description, p3.getDescription());
        assertEquals(records, p3.getRecords());

        Problem p4 = new Problem(title, description, records);
        assertEquals(title, p4.getTitle());
        assertEquals(description, p4.getDescription());
        assertEquals(records, p4.getRecords());
    }

    @Test
    public void testTitle() {
        // TESTING SETTERS AND GETTERS FOR TITLE
        assertEquals(p.getTitle(), title);
        String newTitle = "Leg Burn";
        p.setTitle(newTitle);
        assertEquals(newTitle, p.getTitle());
    }

    @Test
    public void testDate() {
        // TESTING SETTERS AND GETTERS FOR DATE
        assertEquals(p.getPdate(), pdate);
        Date newDate = Calendar.getInstance().getTime();
        p.setPdate(newDate);
        assertEquals(newDate, p.getPdate());
    }

    @Test
    public void testDescription() {
        // TESTING SETTERS AND GETTERS FOR DESCRIPTION
        assertEquals(p.getDescription(), description);
        String newDesc = "I have a burn on the back of my hand.";
        p.setDescription(newDesc);
        assertEquals(newDesc, p.getDescription());
    }

    @Test
    public void testRecords() {
        // TESTING SETTERS AND GETTERS FOR RECORDS
        assertEquals(records, p.getRecords());
        Collection<String> newRecords = new ArrayList<>();
        ArrayList<Comment> newComments = new ArrayList<>();
        newComments.add(new Comment("Healed burn mark", patient.getUserid()));
        newRecords.add((new Record(newComments)).getRecordID());
        p.setRecords(newRecords);
        assertEquals(newRecords, p.getRecords());
    }

    @Test
    public void testAddSingleRecord() {
        // TESTING ADDING SINGLE RECORD
        int recordCount = p.getNumberOfRecords();
        Record newRecord = new Record("newrecord", new Date(), "desc");
        p.addRecord(newRecord.getRecordID());
        assertEquals(recordCount+1, p.getNumberOfRecords());
    }

    @Test
    public void testRemovingSingleRecord() {
        // TESTING REMOVING SINGLE RECORD
        int recordCount = p.getNumberOfRecords();
        Record newRecord = new Record("newerrecord", new Date(), "desc");
        p.addRecord(newRecord.getRecordID());
        assertEquals(recordCount+1, p.getNumberOfRecords());

        recordCount = p.getNumberOfRecords();
        p.removeRecord(newRecord.getRecordID());
        assertEquals(recordCount-1, p.getNumberOfRecords());
    }

    @Test
    public void testNumberOfRecords() {
        // TESTING GETTING NUMBER OF RECORDS
        assertEquals(p.getNumberOfRecords(), 1);
        ArrayList<Comment> newComments = new ArrayList<>();
        newComments.add(new Comment("Healed burn mark", patient.getUserid()));
        records.add(new Record(newComments).getRecordID());
        assertEquals(p.getNumberOfRecords(), 2);
    }

    @Test
    public void testProblemID() {
        // TESTING SETTERS AND GETTERS FOR PROBLEM ID
        String newProblemID = "123j45";
        p.setProblemID(newProblemID);
        assertEquals(newProblemID, p.getProblemID());
    }

    @Test
    public void testPatientID() {
        // TESTING SETTERS AND GETTERS FOR PATIENT ID
        String newPatientID = "168sds45";
        p.setPatientID(newPatientID);
        assertEquals(newPatientID, p.getPatientID());
    }

    @Test
    public void testCompareProblems() {
        sleep(3000);
        String title2 = "Hand Burn";
        Date pdate2 = Calendar.getInstance().getTime();
        String description2 = "I have a burn on the palm of my hand.";
        Collection<String> records2 = new ArrayList<>();
        ArrayList<Comment> comments2 = new ArrayList<>();
        comments2.add(new Comment("Hurts a lot", patient.getUserid()));
        records2.add(new Record(comments2).getRecordID());
        Problem p2 = new Problem(title2, pdate2, description2, records2);

        assertTrue(p.compareTo(p2) < 0);
    }


}
