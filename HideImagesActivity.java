package fusioninfotech.com.hideit.Activity;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

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
import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_images);


        menu_gallery = (FloatingActionButton) findViewById(R.id.menu_gallery);
        linear_image = (LinearLayout) findViewById(R.id.linear_image);
        gridView = (GridView) findViewById(R.id.grdImages);
        myDir = getFilesDir();


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

    @Override
    protected void onResume() {
        super.onResume();
        if (iscomplete){
            byte[] content = getFile(myDir + "/encrypted/myencryptedimage");
            System.out.println(content);

            byte[] decrypted = decryptPdfFile(key, content);
            System.out.println("decrypted content "+decrypted);


            try {
                saveFiledecrypted(decrypted);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public  void saveFileencrypted(byte[] bytes) throws IOException {


        File secondFile = new File(myDir + "/encrypted/", "myencryptedimage");
        if (secondFile.getParentFile().mkdirs()) {
            secondFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(secondFile);
            fos.write(bytes);
            fos.close();
            System.out.println("enter in encrypt save file ");

        }
    }



    public  void saveFiledecrypted(byte[] bytes) throws IOException {

        System.out.println(" my  bytes "+bytes);

        File secondFile = new File(myDir + "/decrypted/", "decrypted.jpeg");
        if (secondFile.getParentFile().mkdirs()) {
            secondFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(secondFile);
            fos.write(bytes);
            fos.close();
            System.out.println("enter in decrypt save file ");

        }

    }

    public void starting ()
            throws NoSuchAlgorithmException, InstantiationException, IllegalAccessException, IOException {

        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
         key = keyGenerator.generateKey();
        System.out.println(key);


    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                imagesEncodedList = new ArrayList<String>();
                if (data.getData() != null) {

                    System.out.println("inside if");

                    Uri mImageUri = data.getData();
                    System.out.println("inside if uri "+mImageUri);
                    // Get the cursor
                    //encrypt(new File(getPath(HideImagesActivity.this, mImageUri)));


                    byte[] content = getFile(getPath(HideImagesActivity.this, mImageUri));
                    System.out.println(content);

                    byte[] encrypted = encryptPdfFile(key, content);
                    System.out.println(encrypted);


                    saveFileencrypted(encrypted);

                    iscomplete = true;

                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        System.out.println("inside else ");
                        ArrayList<String> mArrayUri = new ArrayList<String>();
                        System.out.println("data count " + mClipData.getItemCount());
                        for (int i = 0; i < mClipData.getItemCount(); i++) {

                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            System.out.println("uri from path " + getPath(HideImagesActivity.this, uri));
                            mArrayUri.add(getPath(HideImagesActivity.this, uri));


                        }
                        Log.v("LOG_TAG", "Selected Images data " + mArrayUri);
                        System.out.println("array size " + mArrayUri.size());
                        File file = new File(mArrayUri.get(0));
                       // encrypt(file);
                        array_bitmap = new ArrayList<>();
                        for (int j = 0; j < mArrayUri.size(); j++) {
                            yourbitmap = BitmapFactory.decodeFile(mArrayUri.get(j));
                            array_bitmap.add(yourbitmap);
                        }
                        imageAdapter = new ImageAdapter(this, array_bitmap);
                        gridView.setAdapter(imageAdapter);
                    }
                }
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.d("EXCEPTION ","ERROR in get data "+e);
            Toast.makeText(this, "Something went wrong" +e, Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

   /* void encrypt(File file) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        // Here you read the cleartext.
        FileInputStream fis = new FileInputStream(file);
        // This stream write the encrypted text. This stream will be wrapped by another stream.
        File secondFile = new File(myDir + "/text/", "myfilessssss");
        if (secondFile.getParentFile().mkdirs()) {
            secondFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(secondFile);

            System.out.println("ITS in encrypted");
            // Length is 16 byte
            // Careful when taking user input!!! https://stackoverflow.com/a/3452620/1188357
            SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");
            // Create cipher
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            // Wrap the output stream
            CipherOutputStream cos = new CipherOutputStream(fos, cipher);
            // Write bytes
            int b;
            byte[] d = new byte[8];
            while ((b = fis.read(d)) != -1) {
                cos.write(d, 0, b);
            }
            // Flush and close streams.
            cos.flush();
            cos.close();
            fis.close();
        }


    }*/

     /*void decrypt() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
         File inpfile = new File(myDir + "/text/", "myfilessssss");
        FileInputStream fis = new FileInputStream(inpfile);

         File outfile  = new File(myDir + "/output/");

         if (outfile.getParentFile().mkdirs()) {
             outfile.createNewFile();

             FileOutputStream fos = new FileOutputStream(outfile);

             System.out.println("ITS in decrypted");
             // Length is 16 byte
             // Careful when taking user input!!! https://stackoverflow.com/a/3452620/1188357

             SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");
             Cipher cipher = Cipher.getInstance("AES");
             cipher.init(Cipher.DECRYPT_MODE, sks);
             CipherInputStream cis = new CipherInputStream(fis, cipher);
             int b;
             byte[] d = new byte[8];
             while((b = cis.read(d)) != -1) {
                 fos.write(d, 0, b);
             }

             Bitmap bitmap = BitmapFactory.decodeByteArray(d, 0, d.length);
             System.out.println("my bitmap :--- "+bitmap);

             fos.flush();
             fos.close();
             cis.close();
         }





    }*/


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

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


}
