package team27.healthe;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.junit.Assert.*;

import team27.healthe.model.Patient;
import team27.healthe.model.Problem;

@RunWith(AndroidJUnit4.class)
public class PatientTest {
    @Test
    public void newPatient() {
        String userId = "johntitor";
        String email = "jtitor@ualberta.ca";
        String phone = "7778889999";
        Patient p = new Patient(userId, email, phone);

        assertEquals(userId, p.getUserid());
        assertEquals(email, p.getEmail());
        assertEquals(phone, p.getPhone_number());
    }

    @Test
    public void addProblemTest() {
        Patient patient = new Patient("fromfuture", "ff@google.com", "7539521462");

        String title = "i am a problem";
        Date date = new Date();
        String description = "I just got created";

        Problem problem = new Problem(title, date, description);
        patient.addProblem(problem);

        assertEquals(problem, patient.getProblem(title));
    }

    @Test
    public void removeProblemTest() {
        Patient patient = new Patient("apple", "apple@google.com", "1515151515");
        Problem problem = new Problem("im am green", new Date(), "should I be red?");
        patient.addProblem(problem);

        patient.removeProblem(problem);

        assertTrue(patient.getProblemList().isEmpty());
    }
}
