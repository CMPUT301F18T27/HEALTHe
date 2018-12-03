package team27.healthe;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.view.View;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import team27.healthe.controllers.UserElasticSearchController;
import team27.healthe.model.Patient;
import team27.healthe.ui.LoginActivity;
import team27.healthe.ui.ViewBodyLocationsActivity;

import static android.support.test.espresso.contrib.ActivityResultMatchers.hasResultCode;
import static android.support.test.espresso.contrib.ActivityResultMatchers.hasResultData;
import static org.hamcrest.MatcherAssert.assertThat;

public class ViewBodyLocationsActivityIntentTest {

    private Patient p;

    @Rule
    public IntentsTestRule<ViewBodyLocationsActivity> intentsTestRule =
            new IntentsTestRule<>(ViewBodyLocationsActivity.class, false, false);

    @Before
    public void setup() {
        String user_id = "johnsmith";
        String email = "johnsmith@example.com";
        String number = "7801234567";
        p = new Patient(user_id, email, number);
        UserElasticSearchController pes = new UserElasticSearchController();

        if (pes.getUser(p.getUserid()) == null) {
            pes.addUser(p);
        }

        waitForES();
        Gson gson = new Gson();
        Intent i = new Intent();
        i.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(p));
        i.putExtra(ViewBodyLocationsActivity.SET_FAB_MESSAGE, true);
        i.putExtra(ViewBodyLocationsActivity.BODY_LOCATION_MESSAGE, false);
        intentsTestRule.launchActivity(i);
    }

//    @Test
//    public void testTakePhoto() {
//        assertThat(intentsTestRule.getActivityResult(), hasResultCode(ViewBodyLocationsActivity.REQUEST_IMAGE_CAPTURE));
//        assertThat(intentsTestRule.getActivityResult(),
//                hasResultData(IntentMatchers.hasExtraWithKey("geoLocation")));
//    }

    private void waitForES() {
        try {
            Thread.sleep(500); // make sure new ids are loaded properly
        }
        catch (InterruptedException e) {
        }
    }
}
