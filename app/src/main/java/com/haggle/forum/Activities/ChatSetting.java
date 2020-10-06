package com.haggle.forum.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.icu.text.Edits;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import com.haggle.forum.Utilty.FileSave;
import com.haggle.forum.Utilty.Utils;
import com.haggle.forum.Popup.ImagePopup;
import com.haggle.forum.Popup.QR_Popup;
import com.haggle.forum.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ChatSetting extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView dp;
    private TextView Name;
    private TextView Desc;
    private TextView Count;
    private TextView Report;
    private TextView Exit;
    private ImageButton Camera;
    private ImageButton Share;
    private ImageButton Edit;

    private static final int PICK_IMAGE_REQUEST = 29;

    private String UID;
    private String chatID;
    private String path;
    private String Dp;
    private Boolean Admin;

    private FirebaseAuth firebaseAuth;

    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_setting);

        utils = new Utils(getApplicationContext());

        chatID = getIntent().getStringExtra("ChatID");
        Admin = getIntent().getBooleanExtra("Admin", false);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Chat settings");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getCurrentUser().reload();

        UID = firebaseAuth.getCurrentUser().getUid();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.button_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        Share = findViewById(R.id.share_button);

        Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QR_Popup qrPopup = new QR_Popup(UID, chatID);
                qrPopup.show(getSupportFragmentManager(), "QR");
            }
        });

        Linking();

        Report = findViewById(R.id.text3);
        Report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReportGroup.class);
                startActivity(intent);
            }
        });
    }

    private void Linking() {
        dp = findViewById(R.id.image);
        Camera = findViewById(R.id.CameraButton);
        Edit = findViewById(R.id.edit_button);
        Name = findViewById(R.id.text_1);
        Desc = findViewById(R.id.sub_text_1);
        Count = findViewById(R.id.sub_text_2);
        Exit = findViewById(R.id.exit);

        final FileSave fileSave = new FileSave();
        final StorageReference mstorage = FirebaseStorage.getInstance().getReference("DP");



        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DataBase");

        reference.child("Rooms").child(chatID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Name.setText(dataSnapshot.child("Meta").child("Name").getValue(String.class));
                Desc.setText(dataSnapshot.child("Meta").child("Desc").getValue(String.class));

                Dp = dataSnapshot.child("Meta").child("Dp").getValue(String.class);
                final File file = new  File (getCacheDir(),Dp);

                if (file.exists()) {
                    dp.setImageURI(Uri.fromFile(file));

                    dp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new ImagePopup(getApplicationContext(), v, Uri.fromFile(file), getWindow());
                        }
                    });
                }
                else {
                    fileSave.createCustomFile(getApplicationContext(),Dp,mstorage.child(Dp));

                    mstorage.child(Dp).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            Glide.with(getApplicationContext()).load(uri).into(dp);
                            dp.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new ImagePopup(getApplicationContext(), v, uri, getWindow());
                                }
                            });

                        }
                    });
                }


                if(dataSnapshot.child("Users").getChildrenCount() == 1){
                    Count.setText( "1 Member");
                }
                else{
                    Count.setText( dataSnapshot.child("Users").getChildrenCount()+ " Members");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (!Admin) {
            Edit.setVisibility(View.INVISIBLE);
            Edit.setClickable(false);

            Camera.setVisibility(View.INVISIBLE);
            Camera.setClickable(false);

        }
        else {
            Edit.setVisibility(View.VISIBLE);
            Edit.setClickable(true);
            Camera.setVisibility(View.VISIBLE);
            Camera.setClickable(true);

            Camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(
                            Intent.createChooser(
                                    intent,
                                    "Select Image from here..."),
                            PICK_IMAGE_REQUEST);
                }
            });

            Edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),EditGroupDesc.class);
                    intent.putExtra("ChatId",chatID);
                    startActivity(intent);
                }
            });
        }

        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (utils.isConnected()) {
                    if (!Admin) {
                        reference.child("Rooms").child(chatID).child("Users").child(UID).setValue(null);
                        reference.child("User table").child(UID).child("Group").child("list").child(chatID).setValue(null);

                    }
                    else if (Admin) {
                        reference.child("Rooms").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String Name = dataSnapshot.child(chatID).child("Meta").child("Name").getValue(String.class);
                                String Type = dataSnapshot.child(chatID).child("Meta").child("Type").getValue(String.class);
                                Object data = dataSnapshot.child(chatID).getValue();

                                if (Type.equals("Public")) {
                                    reference.child("Public List").child(Name).setValue(null);
                                }

                                reference.child("Closed Rooms").child(chatID).setValue(data);
                                reference.child("Closed Rooms").child(chatID).child("Closed Data").child("BY").setValue(UID);
                                reference.child("Closed Rooms").child(chatID).child("Closed Data").child("Time").setValue(utils.getCurrentTime());
                                reference.child("Closed Rooms").child(chatID).child("Closed Data").child("Date").setValue(utils.getCurrentDate());
                                reference.child("Rooms").child(chatID).setValue(null);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        reference.child("User table").child(UID).child("Group").child("list").child(chatID).setValue(null);
                    }

                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                    finishAfterTransition();
                } else {
                    Toast.makeText(getApplicationContext(), "Check network connection", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            path = new File(getRealPathFromURI(data.getData())).getPath();

            StorageReference mstorage = FirebaseStorage.getInstance().getReference("DP").child(Dp);

            UploadTask uploadTask = mstorage.putFile(Uri.fromFile(new File(compressImage(Uri.fromFile(new File(path)).toString())).getAbsoluteFile()));
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Glide.with(getApplicationContext()).load(path).into(dp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Unable to Change Dp try after sometime ", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null
                , MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        if (bmp == null) {
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

    public String getFilename() {
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

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

