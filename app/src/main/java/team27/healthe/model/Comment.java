package team27.healthe.model;

public class Comment {
    private String text;
    private String commenter;

    public Comment(String text, String commenter){
        this.text = text;
        this.commenter = commenter;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public String getCommenter() {
        return this.commenter;
    }
}
