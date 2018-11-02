package team27.healthe;

import android.os.Parcel;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import team27.healthe.model.ObjectList;

import static org.junit.Assert.*;//assertTrue;

public class ObjectListTest {
    ObjectList list;
    int test_list_size = 0;
    boolean _DEBUG = false;
    //note: sort test relies on the second item being the first item alphabetically - should change this
    String[] str_objs = {"cba", "abc"};
    // tests add
    @Before
    public void testSetup(){
        list = new ObjectList();
        addTests();
    }

    @Test
    public void sizeTest(){
        assertTrue(list.size() == test_list_size);
    }

    @Test
    public void setGetTest(){
        String sget1 = (String) list.get(0);
        String s3 = "123";
        list.set(0, s3);
        String sget2 = (String) list.get(0);
        assertFalse(sget1.equals(sget2));
    }

    @Test
    public void removeTest(){
        list.remove(0);
        assertTrue(list.size() == test_list_size - 1);
    }

    @Test
    public void sortTest(){
        list.sort();
        assertTrue(list.get(0).equals(str_objs[1]));
    }

    @Test
    public void getListTest(){
        assertTrue(list instanceof List);
        assertTrue(list.size() >= 0);
    }

    //tests writeToParcel, ObjectList(Parcel p) and Creator<ObjectList>
    @Test
    public void writeConstructParcelTest(){
        try {
            Parcel p = Parcel.obtain();
            list.writeToParcel(p, 0);
            p.setDataPosition(0);
            assertTrue(p.dataAvail() > 0);
            ObjectList list2 = new ObjectList(p);
            assertTrue(list2.equals(list));
        }
        catch (Exception e){
            if (_DEBUG){
                e.printStackTrace();
            }

        }
        finally{
            assertTrue("write/construct fail", false);
        }
    }


    private void addTests(){
        for(String s: str_objs){
            list.add(s);
            test_list_size++;
        }
    }

}
