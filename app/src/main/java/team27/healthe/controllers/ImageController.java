package team27.healthe.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import team27.healthe.model.BodyLocationPhoto;
import team27.healthe.model.Patient;
import team27.healthe.model.User;

/**
 * Controls operations for loading/storing of images associated with a user
 * @author Chris
 */
public class ImageController {
    File file;
    ArrayList<String> image_list;
    UserElasticSearchController uesc;
    BodyLocationImageAdapter ia;

    /**
     * Creates a new directory in the app's default picture directory with folder name: 'name'
     * and creates an empty list for holding filepaths for associated images
     * @param c activity context
     * @param name for directory to be created/selected
     */
    public ImageController(Context c, String name){
        uesc = new UserElasticSearchController();
        file = new File(c.getExternalFilesDir(Environment.DIRECTORY_PICTURES) +
                File.separator + name);
        if (!file.exists()){
            file.mkdirs();
        }
        image_list = new ArrayList<>();

    }

    public int size(){
        return image_list.size();
    }

    public String get(int index){
        return image_list.get(index);
    }
    /**
     * helper function for returning the absolute path of a file (using the path to the associated
     * file (folder)
     * @param name (filename to get path for)
     * @return absolute path (String)
     */
    public String getAbsolutePath(String name){
        File tmp = new File(file+File.separator+name);
        return tmp.getAbsolutePath();
    }

    public ArrayList<String> getImageList(){
        return image_list;
    }

    /**
     * function for refreshing the image list (called after user is retrieved from elasticsearch
     * server
     *
     * collects images from the associated user
     * @param cur_user (Patient class
     */
    public void refresh(Patient cur_user){
        if (cur_user == null){
            System.out.println("ERROR-- NO USER");
            return;
        }
        image_list.clear();
        if (file.isDirectory()){
            File[] array_files = file.listFiles();
            if (array_files != null){
                for (File f: array_files){
                    if(cur_user.getBodyLocations().contains(f.getName())){
                        image_list.add(f.getAbsolutePath());
                    }
                    else{
                        System.out.println("Skipping: "+f.getName());
                    }
                }
                System.out.println("image files:");
                for(String filename: image_list){
                    System.out.println("image files: "+filename);
                }
            }
        }
        ia.notifyDataSetChanged();
    }

    /**
     * asynchronously refreshes the list of images for the associated user
     * @param current_user (String current users id)
     */
    public void refreshImageList(String current_user){
        new getUserAsync().execute(current_user);
    }

    /**
     * Creates a new file with the specifed name 'name' and suffix "jpg"
     * @param name (String - filename of file to be created)
     * @return image_file (File - newly created file)
     */
    public File getImageFile(String name){
        File image_file = null;
        try{
            image_file = File.createTempFile(name, ".jpg", file);
        } catch (java.io.IOException e){
            e.printStackTrace();
        }
        return image_file;
    }

    /**
     * Saves an image to local storage
     * @param b (Bitmap - image)
     * @param n (String - name)
     * @return filepath (String - path to saved image)
     */
    public String saveImage(Bitmap b, String n) {
        File image_file = null;
        try {
            System.out.println("DEBUG---" + file.getAbsolutePath());
            image_file = File.createTempFile(n, ".jpg", file);
            System.out.println("DEBUG---" + image_file.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(image_file);
            b.compress(Bitmap.CompressFormat.PNG, 25, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (image_file != null) {
            return image_file.getAbsolutePath();
        } else {
            return null;
        }
    }

    public void deleteImage(BodyLocationPhoto blp){
        //TODO: check for records and ask for delete
        System.out.println("Attempting to delete: "+blp.getBodyLocation()+":"+blp.getBodyLocationPhotoId());
    }

    /**
     * Renames a file with path 'old_filename' to 'new_filename'
     * TODO: this could be moved to somewhere else
     * @param old_filename (String)
     * @param new_filename (String)
     */
    public void renameImage(String old_filename, String new_filename){
        File file = new File(old_filename);
        File file2 = new File(new_filename);
        if(file2.exists()){
            System.out.println("ERROR: file already exists for renaming");
        }
        else{
            file.renameTo(file2);
        }
    }

    /**
     * Sets the associated image adapter (for notifying refreshed list)
     * @param ia (BodyLocationImageAdapter)
     */
    public void setImageAdapter(BodyLocationImageAdapter ia){
        this.ia = ia;
    }

    /**
     * Async class for getting user from elastic search server
     * -refreshes users associated image list
     * -notifies image adapter that changes have been made
     */
    private class getUserAsync extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... user_ids) {
            UserElasticSearchController es_controller = new UserElasticSearchController();

            for (String user_id: user_ids) {
                User user = es_controller.getUser(user_id);
                return user;
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            refresh((Patient)user);
            ia.notifyDataSetChanged();
        }
    }
}
