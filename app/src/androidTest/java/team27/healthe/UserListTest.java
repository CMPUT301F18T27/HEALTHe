package team27.healthe;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import team27.healthe.model.Patient;
import team27.healthe.model.UserList;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class UserListTest {
    @Test
    public void addUserTest() {
        UserList user_list = new UserList();
        Patient p = new Patient("gregor", "gregorian@calendar.com", "2018110200");
        user_list.addUser(p);

        assertTrue(user_list.hasUser(p));
    }

    @Test
    public void getUserTest() {
        UserList user_list = new UserList();
        String patientID = "gregor";
        Patient p = new Patient(patientID, "gregorian@calendar.com", "2018110200");
        user_list.addUser(p);

        assertEquals(p, user_list.getUser(patientID));
    }

    @Test
    public void removeUserTest() {
        UserList user_list = new UserList();
        Patient p = new Patient("gregor", "gregorian@calendar.com", "2018110200");
        user_list.addUser(p);

        user_list.removeUser(p);
        assertTrue(user_list.getUsers().isEmpty());
    }
}
