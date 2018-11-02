package team27.healthe.model;

public class Comment {
    private String text;
    private User commenter;

    public Comment(String text, User commenter){
        this.text = text;
        this.commenter = commenter;
    }

    public void setText(String text) {}

    public String getText() {
        return null;
    }

    public void setCommenter(User commenter) {}

    public String getCommenter() {
        return null;
    }
}
