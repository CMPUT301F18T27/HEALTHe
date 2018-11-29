package team27.healthe.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImageController {
    File file;
    ArrayList<String> image_list;

    public ImageController(Context c, String name){

//        file = new File(c.getExternalFilesDir(Environment.DIRECTORY_PICTURES) +
//                File.separator + "body_locations");
        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+
                File.separator + "body_locations");
        if (!file.exists()){
            file.mkdirs();
        }
//        file = new File(c.getFilesDir(), name);
//        file = c.getFilesDir();
//        if(!file.exists()){
//            try{
//                file.mkdir();
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        }
        System.out.println(file.isDirectory());
        System.out.println("DEBUG---"+file.getAbsolutePath());
        image_list = new ArrayList<>();
        refreshImageList();
//        file = c.getFilesDir(name, Context.MODE_PRIVATE);
    }

    public ArrayList<String> getImageList(){
//        image_list.clear();
//        if (file.isDirectory()){
//            File[] array_files = file.listFiles();
//            for (File f: array_files){
//                image_list.add(f.getAbsolutePath());
//            }
//        }
        return image_list;
    }

    public void refreshImageList(){
        image_list.clear();
        if (file.isDirectory()){
            File[] array_files = file.listFiles();
            for (File f: array_files){
                image_list.add(f.getAbsolutePath());
            }
            System.out.println("image files:");
            for(String filename: image_list){
                System.out.println("image files: "+filename);
            }
        }
    }

    public File getImageFile(String name){
//        File image_file = null;
////        try {
////            image_file = File.createTempFile(name, ".jpg", file);
//        image_file = new File(file, name);
//        return image_file;
        File image_file = null;
        try{
            image_file = File.createTempFile(name, ".jpg", file);
        } catch (java.io.IOException e){
            e.printStackTrace();
        }
//        cur_image_path = image_file.getAbsolutePath();
        return image_file;
    }

    public void saveImage(Context c, Bitmap b, String n){
        File image_file = null;
        try {
            image_file = File.createTempFile(n, ".jpg", file);
            System.out.println("DEBUG---"+image_file.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(image_file);//new File(file.getAbsolutePath()+File.separator+n));//c.openFileOutput(file.getAbsolutePath()+File.separator+n, c.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (IOException e){
            e.printStackTrace();
        }

//        try  {
//            FileOutputStream fos = c.openFileOutput((new File(c.getFilesDir(),n)).getAbsolutePath(), c.MODE_PRIVATE);
////            FileOutputStream out = new FileOutputStream(getImageFile(n));
//            b.compress(Bitmap.CompressFormat.PNG, 100, fos); // bmp is your Bitmap instance
//            System.out.println("SUCCESS!!!!");
//            // PNG is a lossless format, the compression factor (100) is ignored
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }

}
