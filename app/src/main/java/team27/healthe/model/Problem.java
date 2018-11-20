package team27.healthe.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Problem implements Comparable<Problem> {
    private String title;
    private Date pdate;
    private String description;
//    private RecordList records;
    private ArrayList<Integer> records;
    private Integer patient_id;
    // private Collection<Record> records;

    public Problem() {
        this.title = "New Problem";
        this.pdate = new Date();
        this.description = "";
        this.records = new ArrayList<>();//RecordList();
    }

    public Problem(String ttl, Date date, String desc, Integer user_id){
        //Note: user_id should be passed from intent - assumes only patient can create problems
        //  may wish to reduce number of constructors if not used.
        title = ttl;
        pdate = date;
        description = desc;
        records = new ArrayList<>();//RecordList();
        patient_id = user_id;
    }

    public Problem(String ttl, Date date, String desc, ArrayList<Integer> recs){
        title = ttl;
        pdate = date;
        description = desc;
        records = recs;

        // must have at least 1 record
        if (recs.isEmpty()) {
            throw new IllegalStateException();
        }
    }

    public Problem(String ttl, String desc, ArrayList<Integer> recs) {
        title = ttl;
        pdate = Calendar.getInstance().getTime();
        description = desc;
        records = recs;

        // must have at least 1 record
        if (recs.isEmpty()) {
            throw new IllegalStateException();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPdate() {
        return pdate;
    }

    public void setPdate(Date pdate) {
        this.pdate = pdate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Integer> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<Integer> records) {
        this.records = records;
    }

    public void addRecord(Integer record_id){
        records.add(record_id);
    }

    public void removeRecord(Integer record_id){
        records.remove(record_id);
    }

    public int compareTo(Problem compare_problem){
        return this.getPdate().compareTo(compare_problem.getPdate());
    }

    public int getNumberOfRecords() {
        return records.size();
    }
}
