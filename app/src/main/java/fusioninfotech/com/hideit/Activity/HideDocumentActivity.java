package fusioninfotech.com.hideit.Activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.support.design.widget.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import fusioninfotech.com.hideit.Adapter.DocumentAdapter;
import fusioninfotech.com.hideit.Adapter.ImageAdapter;
import fusioninfotech.com.hideit.Helper.Constant;
import fusioninfotech.com.hideit.R;

public class HideDocumentActivity extends AppCompatActivity {

    FloatingActionButton menu_gallery;
    GridView gridView;
    static Uri contentUri;
    ArrayList fileImagePath;
    ClipData mClipData;
    ProgressDialog pDialog, pDialog1;
    DocumentAdapter documentAdapter;
    File myDir;
    ArrayList array_filename;
    Key key;
    int image_count;
    Uri mImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_document);

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


        menu_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("*/*");
                Intent i = Intent.createChooser(intent, "File");
                startActivityForResult(i, 1);

            }
        });
    }

    private void loadOldData() {
        File root_file = new File(myDir + "/decrypted/documents/");

        File[] files = root_file.listFiles();


        if (root_file.listFiles() != null) {
            for (int i = 0; i < files.length; i++) {

                array_filename.add(files[i].getName());
            }
        }


        documentAdapter = new DocumentAdapter(HideDocumentActivity.this, array_filename);
        gridView.setAdapter(documentAdapter);


    }

    public void init() {

        menu_gallery = (FloatingActionButton) findViewById(R.id.menu_gallery);
        gridView = (GridView) findViewById(R.id.grdImages);
        fileImagePath = new ArrayList();

        array_filename = new ArrayList();

        pDialog = new ProgressDialog(HideDocumentActivity.this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Encrypting Files! Please Wait..");

        pDialog1 = new ProgressDialog(HideDocumentActivity.this);
        pDialog1.setCancelable(false);
        pDialog1.setMessage("Saving Files..");

        myDir = getFilesDir();

        loadOldData();

    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {


                if (data.getClipData() == null) {

                    image_count = 1;
                    mImageUri = data.getData();
                    System.out.println("my uri " + mImageUri);

                    new doSaveEncrypt().execute();

                } else {
                    if (data.getClipData() != null) {
                        mClipData = data.getClipData();
                        image_count = 2;

                        new doSaveEncrypt().execute();


                    }
                }
            }
        }
    }


    public void starting()
            throws NoSuchAlgorithmException, InstantiationException, IllegalAccessException, IOException {

        byte[] paswsword = "1234567891234567".getBytes("UTF-8");

        key = new SecretKeySpec(paswsword, "AES");
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


    public void saveFileencrypted(byte[] bytes, String name, String extension) throws IOException {
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String format = s.format(new Date());


        File secondFile = new File(myDir + "/encrypted/documents/", name);
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


    public void saveFiledecrypted(byte[] bytes, String name, String extension) throws IOException {

        System.out.println(" my  bytes " + bytes);

        File secondFile = new File(myDir + "/decrypted/documents/", name);
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


    public class doSaveEncrypt extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {


            if (image_count == 2) {
                System.out.println("data count " + mClipData.getItemCount());
                System.out.println("im inside iffff ");


                for (int i = 0; i < mClipData.getItemCount(); i++) {


                    String path = getPath(HideDocumentActivity.this, mClipData.getItemAt(i).getUri());
                    array_filename.add(path.substring(path.lastIndexOf("/") + 1));

                    byte[] content = getFile(path);
                    System.out.println(content);

                    byte[] encrypted = encryptPdfFile(key, content);
                    System.out.println(encrypted);


                    try {
                        saveFileencrypted(encrypted, path.substring(path.lastIndexOf("/") + 1), path.substring(path.lastIndexOf(".")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            } else {


                System.out.println("im inside else");

                String path = getPath(HideDocumentActivity.this, mImageUri);

                array_filename.add(path.substring(path.lastIndexOf("/") + 1));

                byte[] content = getFile(path);
                System.out.println(content);


                byte[] encrypted = encryptPdfFile(key, content);
                System.out.println(encrypted);

                System.out.println("my extension " + path.substring(path.lastIndexOf(".")));

                try {
                    saveFileencrypted(encrypted, path.substring(path.lastIndexOf("/") + 1), path.substring(path.lastIndexOf(".")));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            documentAdapter.notifyDataSetChanged();
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

            File root_file = new File(myDir + "/encrypted/documents/");

            File[] files = root_file.listFiles();

            System.out.println("file's array " + root_file.listFiles());

            System.out.println("file array size " + files.length);
            for (int i = 0; i < files.length; i++) {

                System.out.println("get first file " + files[i].toURI().toString());
                System.out.println("get first file get path " + getPath(HideDocumentActivity.this, Uri.fromFile(files[i])));

                byte[] content = getFile(getPath(HideDocumentActivity.this, Uri.fromFile(files[i])));
                System.out.println(content);

                byte[] decrypted = decryptPdfFile(key, content);
                System.out.println("decrypted content " + decrypted);

                try {
                    saveFiledecrypted(decrypted, files[i].getName().toString(), files[i].toString().substring(files[i].toString().lastIndexOf(".")));
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

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
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
