package team27.healthe;
import org.junit.Test;

import team27.healthe.model.Problem;
import team27.healthe.model.ProblemList;

import static org.junit.Assert.*;
public class ProblemListTest {
    ProblemList list = new ProblemList();
    @Test
    public void addProblems(){
        Problem p1 = new Problem();
        Problem p2 = new Problem();
        list.add(p1);
        list.add(p2);
        System.out.println("Test!");
        assertTrue(list.size() == 2);
    }
}
