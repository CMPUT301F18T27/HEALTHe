package team27.healthe.model;

import java.util.Collection;

public class Patient extends User {
    private Collection<Problem> problems;

    public Patient(String userID, String email, String phone){
        super(userID, email, phone);
    }

    public Collection<Problem> getProblemList() {
        return this.problems;
    }

    public void addProblem(Problem p) {}

    // @TODO: problem title is not unique. finalize collection class to determine addressing
    public Problem getProblem(String problemTitle) {
        return null;
    }

    public void removeProblem(Problem p) {}

}
