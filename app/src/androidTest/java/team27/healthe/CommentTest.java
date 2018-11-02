package team27.healthe;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

import team27.healthe.model.CareProvider;
import team27.healthe.model.Comment;
import team27.healthe.model.Patient;

@RunWith(AndroidJUnit4.class)
public class CommentTest {
    @Test
    public void newCommentTest() {
        String text = "this is a new comment";
        Patient p = new Patient("imauser", "imauser@gmail.com", "1112223333");

        Comment c = new Comment(text, p);

        assertEquals(text, c.getText());
        assertEquals(p, c.getCommenter());
    }

    @Test
    public void editTextTest() {
        String text = "this is a new comment";
        CareProvider cp = new CareProvider("imaphysician", "imaphysician@gmail.com", "0000000000");
        Comment c = new Comment(text, cp);

        String newText = "This replaces the comment text";
        c.setText(newText);

        assertEquals(newText, c.getText());
    }
}
