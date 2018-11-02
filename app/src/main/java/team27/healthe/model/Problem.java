package team27.healthe.model;

import java.util.Calendar;
import java.util.Date;

public class Problem implements Comparable<Problem> {
    private String title;
    private Date pdate;
    private String description;
    private RecordList records;

    // private Collection<Record> records;

    public Problem(String ttl, Date date, String desc){
        title = ttl;
        pdate = date;
        description = desc;
        records = new RecordList();
    }

    public Problem(String ttl, Date date, String desc, RecordList recs){
        title = ttl;
        pdate = date;
        description = desc;
        records = recs;

        // must have at least 1 record
        if (recs.empty()) {
            throw new IllegalStateException();
        }
    }

    public Problem(String ttl, String desc, RecordList recs) {
        title = ttl;
        pdate = Calendar.getInstance().getTime();
        description = desc;
        records = recs;

        // must have at least 1 record
        if (recs.empty()) {
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

    public RecordList getRecords() {
        return records;
    }

    public void setRecords(RecordList records) {
        this.records = records;
    }

    public int compareTo(Problem compare_problem){
        return this.getPdate().compareTo(compare_problem.getPdate());
    }

    public int getNumberOfRecords() {
        return records.size();
    }
}
