package team27.healthe;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.*;

import team27.healthe.model.Patient;
import team27.healthe.model.Problem;

public class PatientTest {
    @Test
    public void testNewPatientConstructor() {
        String userId = "johntitor";
        String email = "jtitor@ualberta.ca";
        String phone = "7778889999";
        Patient p = new Patient(userId, email, phone);

        assertEquals(userId, p.getUserid());
        assertEquals(email, p.getEmail());
        assertEquals(phone, p.getPhoneNumber());
    }

    @Test
    public void testAddProblem() {
        Patient patient = new Patient("fromfuture", "ff@google.com", "7539521462");
        Problem problem = new Problem("i am a problem", new Date(), "I just got created");

        patient.addProblem(problem.getProblemID());
        assertTrue(patient.hasProblem(problem.getProblemID()));
    }

    @Test
    public void testGetProblemList() {
        Patient patient = new Patient("apple", "apple@google.com", "1515151515");
        String problemTitle = "im am green";
        Problem problem = new Problem(problemTitle, new Date(), "should I be red?");
        Collection<String> problemList = new ArrayList<>();
        problemList.add(problem.getProblemID());

        patient.addProblem(problem.getProblemID());

        assertEquals(problemList, patient.getProblemList());
    }

    @Test
    public void testGetProblemCount() {
        Patient patient = new Patient("apple", "apple@google.com", "1515151515");
        Problem p1 = new Problem("im am green", new Date(), "should I be red?");
        Problem p2 = new Problem("im am red", new Date(), "should I be green?");
        patient.addProblem(p1.getProblemID());
        patient.addProblem(p2.getProblemID());

        assertEquals(2, patient.getProblemCount());
    }

    @Test
    public void testAddBodyLocations() {
        Patient patient = new Patient("apple", "apple@google.com", "1515151515");
        ArrayList<String> bodyLoc = new ArrayList<>();

        // TESTING SETTERS AND GETTERS FOR BODY LOCATION
        assertEquals(bodyLoc, patient.getBodyLocations());
        bodyLoc.add("13456");
        patient.addBodyLocation("13456");
        assertEquals(bodyLoc, patient.getBodyLocations());
    }

    @Test
    public  void testGetBodyLocationCount() {
        Patient patient = new Patient("apple", "apple@google.com", "1515151515");
        ArrayList<String> bodyLoc = new ArrayList<>();

        // TESTING SETTERS AND GETTERS FOR BODY LOCATION
        assertEquals(0, patient.getBodyLocationCount());
        bodyLoc.add("13456");
        patient.addBodyLocation("13456");
        assertEquals(1, patient.getBodyLocationCount());
    }

    @Test
    public void removeProblemTest() {
        Patient patient = new Patient("apple", "apple@google.com", "1515151515");
        Problem p1 = new Problem("im am green", new Date(), "should I be red?");
        Problem p2 = new Problem("im am red", new Date(), "should I be green?");
        patient.addProblem(p1.getProblemID());
        patient.addProblem(p2.getProblemID());

        Collection<String> actual = new ArrayList<>();
        actual.add(p2.getProblemID());

        patient.removeProblem(p1.getProblemID());
        assertEquals(actual, patient.getProblemList());

        patient.removeProblem(p2.getProblemID());
        assertTrue(patient.getProblemList().isEmpty());
    }
}
