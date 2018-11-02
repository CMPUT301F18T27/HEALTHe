package team27.healthe;

import java.util.Collection;

public class Patient extends User {
    private Collection<Problem> problems;

    public Patient(String userID, String email, String phone){
        super(userID, email, phone);
    }

    public void addProblem(Problem p) {}

    public Problem getProblem() {
        return null;
    }

    public Collection<Problem> getProblemList() {
        return null;
    }
}
