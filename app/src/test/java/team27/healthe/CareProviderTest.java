package team27.healthe;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;

import team27.healthe.model.CareProvider;
import team27.healthe.model.Patient;

import static org.junit.Assert.*;

public class CareProviderTest {
    @Test
    public void testNewCareProviderConstructor() {
        String userId = "robinwilliams";
        String email = "rwilliams@ualberta.ca";
        String phone = "4445556666";
        CareProvider cp = new CareProvider(userId, email, phone);

        assertEquals(userId, cp.getUserid());
        assertEquals(email, cp.getEmail());
        assertEquals(phone, cp.getPhone_number());
    }

    @Test
    public void testAddPatient() {
        CareProvider cp = new CareProvider("robinwilliams", "rwilliams@ualberta.ca", "4445556666");
        Patient p = new Patient("johntitor", "jtitor@ualberta.ca", "7778889999");

        cp.addPatient(p.getUserid());
        assertTrue(cp.hasPatient(p.getUserid()));
    }

    @Test
    public void testGetPatient() {
        CareProvider cp = new CareProvider("robinwilliams", "rwilliams@ualberta.ca", "4445556666");
        String patientID = "johntitor";
        Patient p = new Patient(patientID, "jtitor@ualberta.ca", "7778889999");
        cp.addPatient(p.getUserid());

        assertEquals(p.getUserid(), cp.getPatients().iterator().next());
    }

    @Test
    public void testGetPatientCount() {
        CareProvider cp = new CareProvider("robinwilliams", "rwilliams@ualberta.ca", "4445556666");
        Patient p1 = new Patient("johntitor", "jtitor@ualberta.ca", "7778889999");
        Patient p2 = new Patient("johnsmith", "johnsmith@gmail.com", "7803481738");
        cp.addPatient(p1.getUserid());
        cp.addPatient(p2.getUserid());

        assertEquals(cp.getPatientCount(), 2);
    }

    @Test
    public void testRemovePatient() {
        CareProvider cp = new CareProvider("robinwilliams", "rwilliams@ualberta.ca", "4445556666");
        Patient p1 = new Patient("johntitor", "jtitor@ualberta.ca", "7778889999");
        Patient p2 = new Patient("johnsmith", "johnsmith@gmail.com", "7803481738");
        cp.addPatient(p1.getUserid());
        cp.addPatient(p2.getUserid());

        Collection<String> actual = new ArrayList<>();
        actual.add(p2.getUserid());

        cp.removePatient(p1.getUserid());
        assertEquals(actual, cp.getPatients());

        cp.removePatient(p2.getUserid());
        assertTrue(cp.getPatients().isEmpty());
    }
}
