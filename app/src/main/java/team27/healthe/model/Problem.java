package team27.healthe.model;

import java.util.Date;

public class Problem implements Comparable<Problem> {
    private String title;
    private Date pdate;
    private String description;
    private RecordList records;

    public Problem(String ttl, Date date, String desc){
        title = ttl;
        pdate = date;
        description = desc;
    }

    public Problem(){
        pdate = new Date();
        records = new RecordList();
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

    protected void setRecords(RecordList r){
        records = r;
    }

    public int compareTo(Problem compare_problem){
        return this.getPdate().compareTo(compare_problem.getPdate());
    }
}
