package team27.healthe.model;

import java.util.ArrayList;
import java.util.Collection;

public class Patient extends User {
    private Collection<Problem> problems;
    private String user_type = "patient";

    public Patient(String userID, String email, String phone){
        super(userID, email, phone);
        this.problems = new ArrayList();
    }

    public Collection<Problem> getProblemList() {
        return this.problems;
    }

    public void addProblem(Problem p) {problems.add(p);}

    public Boolean hasProblem(Problem p) { return problems.contains(p); }

    // @TODO: problem title is not unique. finalize collection class to determine addressing
    public Problem getProblem(String problemTitle) {
        return null;
    }

    public void removeProblem(Problem p) {problems.remove(p);}

}
