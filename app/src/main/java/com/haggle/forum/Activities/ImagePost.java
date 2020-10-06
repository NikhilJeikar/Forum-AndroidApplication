package com.haggle.forum.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.haggle.forum.Utilty.Utils;
import com.haggle.forum.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImagePost extends AppCompatActivity {
    private String path = "";
    private  Toolbar toolbar;
    private ImageView imageView;
    private EditText text;
    private ImageButton Send;

    private String ChatID ="";
    private String UID;
    private Boolean Log;
    private String Stream ="";

    private FirebaseAuth firebaseAuth;

    private Utils utils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_post);

        utils = new Utils(getApplicationContext());


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().reload();

        UID = firebaseAuth.getCurrentUser().getUid();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.button_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        ChatID = getIntent().getStringExtra("ChatID");
        path = getIntent().getStringExtra("File");
        Stream = getIntent().getStringExtra("Stream");
        Log = getIntent().getBooleanExtra("Source",false);

        imageView = findViewById(R.id.image);
        text = findViewById(R.id.Text);
        Send = findViewById(R.id.button);


        if(!Log){
            Glide.with(getApplicationContext()).load(Uri.fromFile(new File(path)).toString()).into(imageView);
        }
        else {
            Glide.with(getApplicationContext()).load(new File(path)).into(imageView);
        }
       text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    //do what you want on the press of 'done'
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Send.performClick();
                    text.setText("");
                    return true;
                }
                return false;
            }
        });
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(text.getText().toString().trim().length() != 0){
                    if(!Log){

                        final DatabaseReference databaseReference;
                        databaseReference = FirebaseDatabase.getInstance().getReference("DataBase");
                        final String key = databaseReference.push().getKey();

                        StorageReference mstorage = FirebaseStorage.getInstance().getReference("ChatImages").child(key);

                        UploadTask uploadTask = mstorage.putFile(Uri.fromFile(new File(compressImage(Uri.fromFile(new File(path)).toString())).getAbsoluteFile()));
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                databaseReference.child("Rooms").child(ChatID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        try{

                                            String count = String.valueOf(dataSnapshot.child("Public Message").getChildrenCount()+1);
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Value").setValue(text.getText().toString().trim());
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Message Id").setValue(key);
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Type").setValue("Image");
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Time").setValue(utils.getCurrentTime());
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Date").setValue(utils.getCurrentDate());
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Rand").setValue(UID);


                                            databaseReference.child("Rooms").child(ChatID).child("Time").child("Time").setValue(utils.getCurrentTime());
                                            databaseReference.child("Rooms").child(ChatID).child("Time").child("Date").setValue(utils.getCurrentDate());

                                            databaseReference.child("Rooms").child(ChatID).child("Users").child(UID).setValue(String.valueOf(count));

                                            databaseReference.child("Rooms").child(ChatID).child("Count").child(ChatID).setValue(count);
                                        }
                                        catch (Exception e){
                                            String key = databaseReference.push().getKey();
                                            String count = "0";

                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Value").setValue(text.getText().toString().trim());
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Message Id").setValue(key);
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Type").setValue("Image");
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Time").setValue(utils.getCurrentTime());
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Date").setValue(utils.getCurrentDate());
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Rand").setValue(UID);

                                            databaseReference.child("Rooms").child(ChatID).child("Time").child("Time").setValue(utils.getCurrentTime());
                                            databaseReference.child("Rooms").child(ChatID).child("Time").child("Date").setValue(utils.getCurrentDate());

                                            databaseReference.child("Rooms").child(ChatID).child("Users").child(UID).setValue(String.valueOf(count));

                                            databaseReference.child("Rooms").child(ChatID).child("Count").child(ChatID).setValue(count);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }
                    else {
                        final DatabaseReference databaseReference;
                        databaseReference = FirebaseDatabase.getInstance().getReference("DataBase");
                        final String key = databaseReference.push().getKey();

                        Glide.with(getApplicationContext()).load(new File(path)).into(imageView);

                        StorageReference mstorage = FirebaseStorage.getInstance().getReference("ChatImages").child(key);

                        UploadTask uploadTask = mstorage.putFile(Uri.fromFile(new File(compressImage(Uri.fromFile(new File(path)).toString())).getAbsoluteFile()));

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                databaseReference.child("Rooms").child(ChatID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        try{

                                            String count = String.valueOf(dataSnapshot.child("Public Message").getChildrenCount()+1);

                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Message Id").setValue(key);
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Type").setValue("Image");
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Time").setValue(utils.getCurrentTime());
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Date").setValue(utils.getCurrentDate());
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Rand").setValue(UID);
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Value").setValue(text.getText().toString().trim());

                                            databaseReference.child("Rooms").child(ChatID).child("Time").child("Time").setValue(utils.getCurrentTime());
                                            databaseReference.child("Rooms").child(ChatID).child("Time").child("Date").setValue(utils.getCurrentDate());

                                            databaseReference.child("Rooms").child(ChatID).child("Users").child(UID).setValue(String.valueOf(count));

                                            databaseReference.child("Rooms").child(ChatID).child("Count").child(ChatID).setValue(count);

                                        }
                                        catch (Exception e){
                                            String key = databaseReference.push().getKey();
                                            String count = "0";

                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Message Id").setValue(key);
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Type").setValue("Image");
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Time").setValue(utils.getCurrentTime());
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Date").setValue(utils.getCurrentDate());
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Rand").setValue(UID);
                                            databaseReference.child("Rooms").child(ChatID).child("Public Message").child(count).child("Value").setValue(text.getText().toString().trim());

                                            databaseReference.child("Rooms").child(ChatID).child("Time").child("Time").setValue(utils.getCurrentTime());
                                            databaseReference.child("Rooms").child(ChatID).child("Time").child("Date").setValue(utils.getCurrentDate());

                                            databaseReference.child("Rooms").child(ChatID).child("Users").child(UID).setValue(String.valueOf(count));

                                            databaseReference.child("Rooms").child(ChatID).child("Count").child(ChatID).setValue(count);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                    onBackPressed();
                    finish();
                }
                else {
                    text.setError("Desc");
                }

            }
        });

    }

    private String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        if(bmp ==null){
            bmp = BitmapFactory.decodeFile(String.valueOf((new File(String.valueOf(Uri.parse(path))).getPath())));
        }
        int actualHeight = bmp.getHeight();
        int actualWidth = bmp.getWidth();

//      max Height and width values of the compressed image is taken as 816x612
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);

            } else if (orientation == 3) {
                matrix.postRotate(180);

            } else if (orientation == 8) {
                matrix.postRotate(270);

            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;
    }

    private String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

}
