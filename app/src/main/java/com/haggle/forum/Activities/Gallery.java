package com.haggle.forum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.haggle.forum.Adapter.ImageGroupList;
import com.haggle.forum.Holder.ImageGroupListHolder;
import com.haggle.forum.Utilty.FilePaths;
import com.haggle.forum.R;

import java.io.File;
import java.util.ArrayList;

public class Gallery extends AppCompatActivity {
    private String path = "";
    private  Toolbar toolbar;
    private ArrayList<ImageGroupListHolder> listHolders = new ArrayList<ImageGroupListHolder>();
    private ImageGroupList Adapter;
    private final int PICK_IMAGE_REQUEST = 27;
    private String ChatID ="";
    private String Stream = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ChatID = getIntent().getStringExtra("ChatID");
        Stream = getIntent().getStringExtra("Stream");


        toolbar.setNavigationIcon(R.drawable.button_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });


        Button GalleryButton = findViewById(R.id.GalleryButton);
        GalleryButton.setOnClickListener(new View.OnClickListener() {
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
        Run();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            path = new File(getRealPathFromURI(data.getData())).getPath();
            Intent intent = new Intent(getApplicationContext(),ImagePost.class);
            intent.putExtra("File",path);
            intent.putExtra("URI",data.getData().toString());
            intent.putExtra("ChatID",ChatID);
            intent.putExtra("Stream",Stream);
            intent.putExtra("Source",false);
            startActivity(intent);
            finishAndRemoveTask();

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public void getFile(File dir) {

        File listFile[] = dir.listFiles();
        String temp = "";
        if (listFile != null && listFile.length > 0) {
            ArrayList<String> fileList = new ArrayList<String>();
            for (File file : listFile) {
                if (file.isDirectory()) {
                    if(!file.getName().contains(".thumbnails")){
                        getFile(file);
                    }
                }
                else {

                    if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".bmp"))
                    {
                        temp = file.getPath().substring(0, file.getPath().lastIndexOf('/'));
                        if (!fileList.contains(file.getPath()))
                            fileList.add(file.getPath());
                    }
                }

            }
            if(fileList.size() != 0){
                int index = temp.lastIndexOf("/");
                String string = temp.substring(index);
                string = string.replace("/","");
                ImageGroupListHolder holder = new ImageGroupListHolder(string,fileList);
                listHolders.add(holder);
            }
        }

    }


    private void Run(){
        FilePaths filePaths = new FilePaths();
        getFile(new File(filePaths.CAMERA));
        getFile(new File(filePaths.DOWNLOAD));
        getFile(new File(filePaths.DOCUMENTS));
        getFile(new File(filePaths.PICTURES));
        ListView lv = findViewById(R.id.list);
        Adapter = new ImageGroupList(getApplicationContext() , listHolders , ChatID ,Stream);
        lv.setAdapter(Adapter);

    }

    public String getRealPathFromURI( Uri contentUri) {
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,null
                , MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

}
