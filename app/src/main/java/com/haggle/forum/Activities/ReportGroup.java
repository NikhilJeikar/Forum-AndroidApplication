package com.haggle.forum.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.haggle.forum.R;

public class ReportGroup extends AppCompatActivity {
    private Toolbar toolbar;
    private RadioGroup Reports;
    private EditText Report;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_group);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");

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

        Reports =  findViewById(R.id.Report_1);
        Report = findViewById(R.id.Text);
        button = findViewById(R.id.ReportButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Reports.getCheckedRadioButtonId() == R.id.Report_Spam){

                }
                if(Reports.getCheckedRadioButtonId() == R.id.Report_Violence){

                }
                if(Reports.getCheckedRadioButtonId() == R.id.Report_Bullying){

                }
                if(Reports.getCheckedRadioButtonId() == R.id.Report_Sexual_content){

                }
                if(Reports.getCheckedRadioButtonId() == R.id.Report_Child_abuse){

                }
                if(Reports.getCheckedRadioButtonId() == R.id.Report_Drug_abuse){

                }
                if(Reports.getCheckedRadioButtonId() == R.id.Report_Others){
                    if(Report.getText().toString().trim().length() > 0){

                    }
                    else {
                        Report.setError("Can you please explain the issue");
                    }
                }

            }
        });



    }
}
