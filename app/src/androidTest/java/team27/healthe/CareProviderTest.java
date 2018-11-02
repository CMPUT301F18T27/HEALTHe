package team27.healthe;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import team27.healthe.model.CareProvider;
import team27.healthe.model.Patient;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class CareProviderTest {
    @Test
    public void newCareProviderTest() {
        String userId = "robinwilliams";
        String email = "rwilliams@ualberta.ca";
        String phone = "4445556666";
        CareProvider c = new CareProvider(userId, email, phone);

        assertEquals(userId, c.getUserid());
        assertEquals(email, c.getEmail());
        assertEquals(phone, c.getPhone_number());
    }

    @Test
    public void addPatientTest() {
        CareProvider c = new CareProvider("robinwilliams", "rwilliams@ualberta.ca", "4445556666");
        Patient p = new Patient("johntitor", "jtitor@ualberta.ca", "7778889999");

        c.addPatient(p);
        assertTrue(c.hasPatient(p));
    }

    @Test
    public void getPatientTest() {
        CareProvider c = new CareProvider("robinwilliams", "rwilliams@ualberta.ca", "4445556666");
        String patientID = "johntitor";
        Patient p = new Patient(patientID, "jtitor@ualberta.ca", "7778889999");
        c.addPatient(p);

        assertEquals(p, c.getPatient(patientID));
    }

    @Test
    public void removePatientTest() {
        CareProvider c = new CareProvider("robinwilliams", "rwilliams@ualberta.ca", "4445556666");
        Patient p = new Patient("johntitor", "jtitor@ualberta.ca", "7778889999");
        c.addPatient(p);

        c.removePatient(p);
        assertTrue(c.getPatients().isEmpty());
    }
}
