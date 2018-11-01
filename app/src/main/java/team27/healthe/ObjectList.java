//package team27.healthe;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class ObjectList<T extends Comparable<T>> implements Parcelable {
//    private List<T> item_list;
//
//    public ObjectList(){
//        item_list = new ArrayList<>();
//    }
//
//    public ObjectList(Parcel p){
//        // solution from: https://stackoverflow.com/questions/18797399/is-it-possible-to-parcel-a-generic-class
//        // Note: note complete implementation
//        int size = p.readInt();
//        if (size == 0){
//            item_list = null;
//        }
//        else{
//            Class<T> type = (Class<T>) p.readSerializable();
//            item_list = p.readArrayList(type.getClassLoader());
//        }
//
//    }
//    //List implementations
//    // Note: chose not to subclass list to prevent some operations
//    public void add(T item){
//        item_list.add(item);
//    }
//
//    public int size(){
//        return item_list.size();
//    }
//
//    public void set(int i, T item){
//        item_list.set(i, item);
//    }
//
//    public void remove(int i){
//        item_list.remove(i);
//    }
//
//    public T get(int i){
//        return item_list.get(i);
//    }
//
//    public void sort(){
//        Collections.sort(item_list);
//    }
//
//    public List<T> getFeelings(){
//        return item_list;
//    }
//
//
//
//    //Implementation for parcelable
//    @Override
//    public int describeContents(){
//        return 0;
//    }
//    @Override
//    public void writeToParcel(Parcel dest, int flags){
//        dest.writeList(item_list);
//    }
//    public static Creator<ObjectList> CREATOR = new Creator<ObjectList>(){
//        @Override
//        public ObjectList createFromParcel(Parcel source){
//            return new ObjectList(source);
//        }
//        @Override
//        public ObjectList[] newArray(int size){
//            return new ObjectList[size];
//        }
//    };

//}
