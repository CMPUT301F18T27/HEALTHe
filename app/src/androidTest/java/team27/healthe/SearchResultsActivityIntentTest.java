//package team27.healthe;
//
//import android.Manifest;
//import android.content.Intent;
//import android.support.test.espresso.core.internal.deps.guava.collect.Maps;
//import android.support.test.espresso.intent.rule.IntentsTestRule;
//import android.support.test.rule.GrantPermissionRule;
//
//import com.google.gson.Gson;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import io.searchbox.core.SearchResult;
//import team27.healthe.controllers.ElasticSearchSearchController;
//import team27.healthe.controllers.ProblemElasticSearchController;
//import team27.healthe.controllers.RecordElasticSearchController;
//import team27.healthe.controllers.UserElasticSearchController;
//import team27.healthe.model.Patient;
//import team27.healthe.model.Problem;
//import team27.healthe.model.Record;
//import team27.healthe.ui.LoginActivity;
//import team27.healthe.ui.ProblemInfoActivity;
//import team27.healthe.ui.RecordActivity;
//import team27.healthe.ui.SearchResultsActivity;
//
//import static android.support.test.espresso.Espresso.onData;
//import static android.support.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.intent.Intents.intended;
//import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
//import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static org.hamcrest.CoreMatchers.anything;
//
//public class SearchResultsActivityIntentTest {
//
//    private Patient p;
//    private Problem pr;
//    private Record r;
//
//    @Rule
//    public IntentsTestRule<SearchResultsActivity> intentsTestRule =
//            new IntentsTestRule<>(SearchResultsActivity.class, false, false);
//
//    @Before
//    public void setup() {
//        String user_id = "johnsmith";
//        String email = "johnsmith@example.com";
//        String number = "7801234567";
//        p = new Patient(user_id, email, number);
//        pr = new Problem("hand burn", new Date(), "description", p.getUserid());
//        r = new Record("newrecord", new Date(), "description");
//
//        RecordElasticSearchController res = new RecordElasticSearchController();
//        ProblemElasticSearchController pres = new ProblemElasticSearchController();
//        UserElasticSearchController pes = new UserElasticSearchController();
//
//        if (pes.getUser(p.getUserid()) == null) {
//            pes.addUser(p);
//        }
//
//        if (pres.getProblem(pr.getProblemID()) == null) {
//            pres.addProblem(pr);
//            p.addProblem(pr.getProblemID());
//            pes.addUser(p);
//        }
//        if (res.getRecord(r.getRecordID()) == null) {
//            res.addRecord(r);
//            pr.addRecord(r.getRecordID());
//            pres.addProblem(pr);
//        }
//
//        ArrayList<Map<String,String>> hits = new ArrayList<>();
//        Map<String, String> tmp = new HashMap<String, String>();
//        tmp.put("description", pr.getDescription());
//        tmp.put("patient_id", pr.getDescription());
//        tmp.put("pdate", pr.getPdateAsString());
//        tmp.put("problem_id", pr.getProblemID());
//        tmp.put("records", pr.getRecords().toString());
//        tmp.put("title", pr.getTitle());
//        //hits.addAll(tmp);
//
//        waitForES();
//        Gson gson = new Gson();
//        Intent i = new Intent();
//        i.putExtra(LoginActivity.USER_MESSAGE, gson.toJson(p));
//        i.putExtra(SearchResultsActivity.SEARCH_MESSAGE, hits);
//        intentsTestRule.launchActivity(i);
//    }
//
//    @Test
//    public void testDisplayProblemResult() {
//        onData(anything()).inAdapterView(withId(R.id.search_results_list)).atPosition(0).perform(click());
//        intended(hasComponent(ProblemInfoActivity.class.getName()));
//    }
//
//    @Test
//    public void testDisplayRecordResult() {
//        onData(anything()).inAdapterView(withId(R.id.search_results_list)).atPosition(0).perform(click());
//        intended(hasComponent(RecordActivity.class.getName()));
//    }
//
//    @After
//    public void after() {
//        RecordElasticSearchController res = new RecordElasticSearchController();
//        ProblemElasticSearchController pres = new ProblemElasticSearchController();
//        UserElasticSearchController pes = new UserElasticSearchController();
//
//        if (pes.getUser(p.getUserid()) != null) {
//            pes.removeUser(p.getUserid());
//        }
//
//        if (pres.getProblem(pr.getProblemID()) != null) {
//            pres.removeProblem(pr.getProblemID());
//        }
//        if (res.getRecord(r.getRecordID()) != null) {
//            res.removeRecord(r.getRecordID());
//        }
//        waitForES();
//    }
//
//
//    private void waitForES() {
//        try {
//            Thread.sleep(500); // make sure new ids are loaded properly
//        }
//        catch (InterruptedException e) {
//        }
//    }
//}
//
//
//
