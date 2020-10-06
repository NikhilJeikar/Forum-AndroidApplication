package com.haggle.forum.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.haggle.forum.Popup.ConformationPopup;
import com.haggle.forum.R;

import java.io.IOException;
import java.util.ArrayList;

public class QR_Scanner extends AppCompatActivity {

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private  Toolbar toolbar;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private TextView textView;
    private String Intentdata = "";
    private ArrayList<Integer> points = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scanner);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Join");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.button_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        surfaceView = findViewById(R.id.surface);

        textView = findViewById(R.id.text);
    }

    private void initialiseDetectorsAndSources() {

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    Intentdata= barcodes.valueAt(0).displayValue;
                    Runnable runnable =
                            new Runnable() {
                                @Override
                                public void run() {
                                    textView.setText(Intentdata);
                                    try {
                                        Interpret(Intentdata);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                    textView.post(runnable);

                }

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    private void Interpret(String data) throws IOException {
        cameraSource.stop();

        final String[] Name = new String[1];
        final ArrayList<String > list = new ArrayList<String>();
        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG).show();

        for(int i = 0;i<data.length();i++){
            if(data.charAt(i) =='|'){
                points.add(i);
            }
        }

        if(points.size()%2 == 0){
            for (int i =0 ; i<points.size() ;i = i +2){
                list.add(data.substring(points.get(i),points.get(i+1)));
            }
        }

        if(list.size() == 2){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DataBase");
            cameraSource.stop();
            reference.child("Rooms").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(list.get(0)).child("Meta") != null) {
                        Name[0] = dataSnapshot.child(list.get(0).replace("|","")).child("Meta").child("Name").getValue(String.class);
                        if(Name[0] != null || Name[0] == ""){
                           /* ConformationPopup popup = new ConformationPopup(Name[0],list.get(0).replace("|",""),list.get(1).replace("|",""));
                            popup.show(getSupportFragmentManager(),"Join");*/
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Invalid Room",Toast.LENGTH_LONG).show();
                        }

                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Invalid QR",Toast.LENGTH_LONG).show();
                        try {
                            cameraSource.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else if(list.size() == 3){
            Toast.makeText(getApplicationContext(),"3",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getApplicationContext(),"QR missmatch",Toast.LENGTH_LONG).show();
            cameraSource.start();
        }

    }
}
