package team27.healthe;
import org.junit.Before;
import org.junit.Test;

import team27.healthe.model.*;


public class ProblemListTest {
    ProblemList test_list;
    String tag1 = "tag1";
    Problem p1, p2;
    @Before
    public void testSetup(){
        test_list = new ProblemList();
        p1 = new Problem();
        p1.setTitle("tag1 tag2");
        p1.setDescription("tag1 tag3");

        p2 = new Problem();
        p2.setTitle("tag2 tag 3");
        p2.setDescription("tag4");
        test_list.add(p1);
        test_list.add(p2);
    }

    @Test
    public void searchProblemsTest(){
        ProblemList results = test_list.searchProblems(tag1);
        assert(results.getObjects().contains(p1));
    }

}
