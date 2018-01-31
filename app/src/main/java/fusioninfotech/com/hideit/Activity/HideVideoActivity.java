package fusioninfotech.com.hideit.Activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import fusioninfotech.com.hideit.Adapter.ImageAdapter;
import fusioninfotech.com.hideit.Adapter.VideoAdapter;
import fusioninfotech.com.hideit.Helper.Constant;
import fusioninfotech.com.hideit.R;

public class HideVideoActivity extends AppCompatActivity {

    FloatingActionButton menu_gallery;
    GridView gridView;
    ArrayList<Bitmap> arra_bitmap ;
    VideoAdapter videoAdapter;
    ProgressDialog pDialog, pDialog1;
    File myDir;
    Key key;
    static Uri contentUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_video);

        Bundle b = getIntent().getExtras();

                init();

                try {
                    if (b.getString("is_complete").equalsIgnoreCase("true")){

                        try {
                            System.out.println("Inside Try");
                            starting();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Inside do save ");
                        new doSaveEncrypt().execute();
                    }
                }catch (Exception e){
                    System.out.println(" excpetion "+e);
                }



    }

    
    public  void init(){
        menu_gallery = (FloatingActionButton) findViewById(R.id.menu_gallery);

        gridView = (GridView)findViewById(R.id.grdVideos);
        arra_bitmap = new ArrayList<>();


        pDialog = new ProgressDialog(HideVideoActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Encrypting Files! Please Wait..");

        pDialog1 = new ProgressDialog(HideVideoActivity.this);
        pDialog1.setCancelable(false);
        pDialog1.setMessage("Saving Files..");

        myDir = getFilesDir();
        menu_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(HideVideoActivity.this,VideoPickerActivity.class);
                startActivity(i);
            }
        });


        gridView.setChoiceMode(gridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                int checkedCount = gridView.getCheckedItemCount();
                actionMode.setTitle(checkedCount + " selected");
                videoAdapter.toggleSelection(i);

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.restore_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.restoreitem:

                        new StoreImage().execute();

                        actionMode.finish();


                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });



    }

    private void DeletefromData(int position){

        System.out.println("my position "+position);

        File root_file = new File(myDir + "/decrypted/videos/");
        File root_file_encrypt = new File(myDir + "/encrypted/videos/");
        File[] files = root_file.listFiles();
        File[] files_encrypt = root_file_encrypt.listFiles();

        System.out.println("file size "+files.length);
        System.out.println("file encrypt size "+files_encrypt.length);


        if (root_file.listFiles() != null) {
            boolean isdelete =  files[position].delete();
            files_encrypt[position].delete();
            System.out.println("is delete "+isdelete);
            Constant.videos_bitmap.remove(position);

        }
    }

    private void storeImage(byte[] content) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Toast.makeText(this, "Errorr ", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(content);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("photo", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("photo", "Error accessing file: " + e.getMessage());
        }
    }

    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Hide It/");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmssSSS").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".mp4";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }



    public class StoreImage extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {


            SparseBooleanArray selected = videoAdapter.getSelectedIds();
            for (int i = (selected.size() - 1); i >= 0; i--) {

                if (selected.valueAt(i)) {
                    File root_file = new File(myDir + "/decrypted/videos");

                    File[] files = root_file.listFiles();

                    System.out.println("file array size " + files.length);



                        byte[] content = getFile(getPath(HideVideoActivity.this, Uri.fromFile(files[i])));
                        System.out.println(content);

                        storeImage(content);



                    DeletefromData(selected.keyAt(i));

                }
            }





            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            videoAdapter.notifyDataSetChanged();
            pDialog.dismiss();

        }
    }


    public class doSaveEncrypt extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {



                for (int i = 0; i < Constant.selected_videos.size(); i++) {

                    /*byte[] content = getFile(getPath(HideVideoActivity.this, uri));
                    System.out.println(content);*/

                    String path = Constant.selected_videos.get(i).toString();

                    byte[] content = getFile(Constant.selected_videos.get(i).toString());
                    System.out.println(content);

                    Log.d("Enc_Key","in encrypt "+key);

                    byte[] encrypted = encryptPdfFile(key, content);
                    System.out.println(encrypted);

                    try {
                        saveFileencrypted(encrypted, i ,path.substring(path.lastIndexOf(".")) );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            videoAdapter = new VideoAdapter(HideVideoActivity.this);
            gridView.setAdapter(videoAdapter);
            videoAdapter.notifyDataSetChanged();

            pDialog.dismiss();
            new doSaveDecrypt().execute();
        }
    }


    public class doSaveDecrypt extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog1.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            File root_file = new File(myDir + "/encrypted/videos/");

            File[] files = root_file.listFiles();

            System.out.println("file's array " + root_file.listFiles());

            System.out.println("file array size " + files.length);
            for (int i = 0; i < files.length; i++) {

               /* byte[] content = getFile(getPath(HideVideoActivity.this, Uri.fromFile(files[i])));
                System.out.println(content);*/

                byte[] content = getFile(getPath(HideVideoActivity.this, Uri.fromFile(files[i])));
                System.out.println(content);

                Log.d("Enc_Key","in decrypt "+key);


                byte[] decrypted = decryptPdfFile(key, content);
                System.out.println("decrypted content " + decrypted);

                try {
                    saveFiledecrypted(decrypted, i,files[i].toString().substring(files[i].toString().lastIndexOf(".")));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog1.dismiss();


        }
    }



    public static byte[] getFile(String uri) {

        File f = new File(uri);
        InputStream is = null;
        try {
            is = new FileInputStream(f);
        } catch (FileNotFoundException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        byte[] content = null;
        try {
            content = new byte[is.available()];
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            is.read(content);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return content;
    }




    public static byte[] encryptPdfFile(Key key, byte[] content) {
        Cipher cipher;
        byte[] encrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            encrypted = cipher.doFinal(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;

    }




    public static byte[] decryptPdfFile(Key key, byte[] textCryp) {
        Cipher cipher;
        byte[] decrypted = null;
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            decrypted = cipher.doFinal(textCryp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decrypted;
    }




    public void saveFileencrypted(byte[] bytes, int count , String  extension) throws IOException {
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());

        File secondFile = new File(myDir + "/encrypted/videos/", format + count+extension);
        if (secondFile.getParentFile().mkdirs()) {
            secondFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(secondFile);
            fos.write(bytes);
            fos.close();
            System.out.println("enter in encrypt save file ");

        } else {
            FileOutputStream fos = new FileOutputStream(secondFile);
            fos.write(bytes);
            fos.close();
        }
    }


    public void saveFiledecrypted(byte[] bytes, int i , String extension) throws IOException {

        System.out.println(" my  bytes " + bytes);

        File secondFile = new File(myDir + "/decrypted/videos/", "decryptedvideos" + i +extension);
        if (secondFile.getParentFile().mkdirs()) {
            secondFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(secondFile);
            fos.write(bytes);
            fos.close();
            System.out.println("enter in decrypt save file ");

        } else {
            FileOutputStream fos = new FileOutputStream(secondFile);
            fos.write(bytes);
            fos.close();
        }

    }

    public void starting()
            throws NoSuchAlgorithmException, InstantiationException, IllegalAccessException, IOException {

        byte[] paswsword = "1234567891234567".getBytes("UTF-8");
        key = new SecretKeySpec(paswsword, "AES");

    }

    @Nullable
    public static String getPath(Context context, Uri uri) {
        // DocumentProvider
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                System.out.println("id in file path " + id);
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:", "");
                }
                try {
                    contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                } catch (NumberFormatException e) {
                    System.out.println("no mel aavyo " + e);
                    return "empty";
                }


            } else if (isMediaDocument(uri)) { // MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);

            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);

        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }



}
