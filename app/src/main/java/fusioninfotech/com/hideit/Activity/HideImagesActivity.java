package fusioninfotech.com.hideit.Activity;

import android.annotation.TargetApi;
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
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import fusioninfotech.com.hideit.Adapter.ImageAdapter;
import fusioninfotech.com.hideit.R;

public class HideImagesActivity extends AppCompatActivity {
    FloatingActionButton menu_gallery;
    private int PICK_IMAGE_MULTIPLE = 0;
    String imageEncoded;
    private List<String> imagesEncodedList;
    static Uri contentUri;
    LinearLayout linear_image;
    Bitmap yourbitmap;
    GridView gridView;
    ImageAdapter imageAdapter;
    ArrayList<Bitmap> array_bitmap;
    File myDir;
    boolean iscomplete = false;
    Key key;
    int image_count;
    ProgressDialog pDialog, pDialog1;
    ClipData mClipData;
    ArrayList<String> mArrayUri = new ArrayList<String>();
    Uri mImageUri;
    String TAG = "My Log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_images);

        init();

        try {
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


    }

    public void init() {
        menu_gallery = (FloatingActionButton) findViewById(R.id.menu_gallery);
        linear_image = (LinearLayout) findViewById(R.id.linear_image);
        gridView = (GridView) findViewById(R.id.grdImages);

        array_bitmap = new ArrayList<>();

        pDialog = new ProgressDialog(HideImagesActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Encrypting Files! Please Wait..");

        pDialog1 = new ProgressDialog(HideImagesActivity.this);
        pDialog1.setCancelable(false);
        pDialog1.setMessage("Saving Files..");

        myDir = getFilesDir();

        loadOldData();


        gridView.setChoiceMode(gridView.CHOICE_MODE_MULTIPLE_MODAL);
        gridView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                int checkedCount = gridView.getCheckedItemCount();
                actionMode.setTitle(checkedCount + " selected");
                imageAdapter.toggleSelection(i);

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
                        SparseBooleanArray selected = imageAdapter.getSelectedIds();
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                storeImage(imageAdapter.getItem(selected.keyAt(i)));
                                DeletefromData(selected.keyAt(i));

                                imageAdapter.notifyDataSetChanged();
                            }
                        }
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


        menu_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);

            }
        });

    }


    private void DeletefromData(int position){

        File root_file = new File(myDir + "/decrypted/images/");
        File root_file_encrypt = new File(myDir + "/encrypted/images/");
        File[] files = root_file.listFiles();
        File[] files_encrypt = root_file_encrypt.listFiles();


        if (root_file.listFiles() != null) {
            boolean isdelete =  files[position].delete();
            files_encrypt[position].delete();
            System.out.println("is delete "+isdelete);
            array_bitmap.remove(position);
        }
    }


    private void loadOldData() {
        File root_file = new File(myDir + "/decrypted/images/");

        File[] files = root_file.listFiles();


        if (root_file.listFiles() != null) {
            for (int i = 0; i < files.length; i++) {

                byte[] content = getFile(getPath(HideImagesActivity.this, Uri.fromFile(files[i])));

                yourbitmap = BitmapFactory.decodeByteArray(content, 0, content.length);
                array_bitmap.add(yourbitmap);
            }
        }

        Log.d(TAG, "load old data " + array_bitmap);

        imageAdapter = new ImageAdapter(HideImagesActivity.this, array_bitmap);
        gridView.setAdapter(imageAdapter);


    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Toast.makeText(this, "Errorr ", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("photo", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("photo", "Error accessing file: " + e.getMessage());
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

    public void saveFileencrypted(byte[] bytes, int count) throws IOException {

        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());

        File secondFile = new File(myDir + "/encrypted/images/", format + count);
        if (secondFile.getParentFile().mkdirs()) {
            secondFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(secondFile);
            fos.write(bytes);
            fos.close();


        } else {
            FileOutputStream fos = new FileOutputStream(secondFile);
            fos.write(bytes);
            fos.close();
        }
    }


    public void saveFiledecrypted(byte[] bytes, int i) throws IOException {


        File secondFile = new File(myDir + "/decrypted/images/", "decrypted" + i + ".jpeg");
        if (secondFile.getParentFile().mkdirs()) {
            secondFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(secondFile);
            fos.write(bytes);
            fos.close();


        } else {
            FileOutputStream fos = new FileOutputStream(secondFile);
            fos.write(bytes);
            fos.close();
        }

    }

    public void starting()
            throws NoSuchAlgorithmException, InstantiationException, IllegalAccessException, IOException {
        /* KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);*/
        byte[] paswsword = "1234567891234567".getBytes("UTF-8");

        key = new SecretKeySpec(paswsword, "AES");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();


                if (data.getClipData() == null) {

                    image_count = 1;
                    mImageUri = data.getData();
                    System.out.println("my uri "+mImageUri);

                    new doSaveEncrypt().execute();

                } else {
                    if (data.getClipData() != null) {
                        mClipData = data.getClipData();
                        image_count = 2;

                        new doSaveEncrypt().execute();


                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d("EXCEPTION ", "ERROR in get data " + e);
            Toast.makeText(this, "Something went wrong" + e, Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public class doSaveEncrypt extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {

            Log.d(TAG, " in background array bitmap " + array_bitmap);


            if (image_count == 2) {

                Log.d(TAG, " mclipdata count" + mClipData.getItemCount());


                for (int i = 0; i < mClipData.getItemCount(); i++) {

                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();

                   // mArrayUri.add(getPath(HideImagesActivity.this, uri));

                    byte[] content = getFile(getPath(HideImagesActivity.this, uri));

                    byte[] encrypted = encryptPdfFile(key, content);


                    yourbitmap = BitmapFactory.decodeFile(getPath(HideImagesActivity.this, uri));
                    Log.d(TAG, " my bitmap " + yourbitmap);
                    array_bitmap.add(yourbitmap);


                    try {
                        saveFileencrypted(encrypted, i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                iscomplete = true;

                Log.v("LOG_TAG", "Selected Images data " + array_bitmap);


            } else {


               // mArrayUri.add(getPath(HideImagesActivity.this, mImageUri));

                byte[] content = getFile(getPath(HideImagesActivity.this, mImageUri));

                Log.d("Enc_Key", "in encrypt " + key);

                byte[] encrypted = encryptPdfFile(key, content);

                yourbitmap = BitmapFactory.decodeFile(getPath(HideImagesActivity.this, mImageUri));
                array_bitmap.add(yourbitmap);

                try {
                    saveFileencrypted(encrypted, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            Log.d(TAG, "In post execute " + array_bitmap);

            imageAdapter.notifyDataSetChanged();
            gridView.setAdapter(imageAdapter);
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

            File root_file = new File(myDir + "/encrypted/images/");

            File[] files = root_file.listFiles();


            for (int i = 0; i < files.length; i++) {

                byte[] content = getFile(getPath(HideImagesActivity.this, Uri.fromFile(files[i])));


                Log.d("Enc_Key", "in decrypt " + key);

                byte[] decrypted = decryptPdfFile(key, content);


                try {
                    saveFiledecrypted(decrypted, i);
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

                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:", "");
                }
                try {
                    contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                } catch (NumberFormatException e) {

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


    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Hide It/");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmssSSS").format(new Date());
                File mediaFile;
                String mImageName = "MI_" + timeStamp + ".jpg";
                mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
                return mediaFile;

            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmssSSS").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }


}
