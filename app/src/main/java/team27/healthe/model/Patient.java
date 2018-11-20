package team27.healthe.model;

import java.util.ArrayList;
import java.util.Collection;

public class Patient extends User {
    private ArrayList<Integer> problems;
    private String user_type = "patient";

    public Patient(String userID, String email, String phone){
        super(userID, email, phone);
        this.problems = new ArrayList();
    }

    public Collection<Integer> getProblemList() {
        return this.problems;
    }

    public void addProblem(Integer p) {problems.add(p);}

    public Boolean hasProblem(Integer p) { return problems.contains(p); }

    // @TODO: problem title is not unique. finalize collection class to determine addressing
    public Problem getProblem(String problemTitle) {
        return null;
    }

    public void removeProblem(Integer p) {problems.remove(p);}

    public Integer getProblemCount() {return problems.size();}

}
