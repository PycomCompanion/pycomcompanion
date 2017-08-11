package com.dayman.poiot;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

public class DatePickerActivity extends AppCompatActivity {
    private String startDate, endDate;
    private int ID;
    public String getStartDate(){
        return startDate;
    }
    public String getEndDate(){
        return endDate;
    }
    public int getID(){
        return ID;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);
        final String LoginID = getIntent().getExtras().getString("LoginID");
        final String Name = getIntent().getExtras().getString("Name");
        final String Password = getIntent().getExtras().getString("Password");


        final Button buttonStart = (Button)findViewById(R.id.BtnStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment dialogfragment = new DatePickerDialogClass();
                ID = 1;
                dialogfragment.show(getFragmentManager(), "Date Picker Dialog");
            }
        });
        final Button buttonEnd = (Button)findViewById(R.id.BtnEnd);
        buttonEnd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment dialogfragment = new DatePickerDialogClass();
                ID = 0;
                dialogfragment.show(getFragmentManager(), "Date Picker Dialog");
            }
        });

        final Button buttonConfirm = (Button)findViewById(R.id.BtnConfirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MessagesActivity.class);
                String[] message = new String[]{startDate,endDate};

                intent.putExtra("loginID", LoginID);
                intent.putExtra("name", Name);
                intent.putExtra("password", Password);
                intent.putExtra("startDate", startDate);
                intent.putExtra("endDate", endDate);
                startActivity(intent);
            }
        });
    }


    public static class DatePickerDialogClass extends DialogFragment implements DatePickerDialog.OnDateSetListener{



        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);

            return datepickerdialog;
        }



        public void onDateSet(DatePicker view, int year, int month, int day){
            String Date = day + "/" + (month+1) + "/" + year;

            if (((DatePickerActivity)this.getActivity()).ID == 1) {

                ((DatePickerActivity)this.getActivity()).startDate = Date;
                Toast.makeText(getActivity(), "Start: " + Date, Toast.LENGTH_LONG).show();
            }
            if (((DatePickerActivity)this.getActivity()).ID == 0) {

                ((DatePickerActivity)this.getActivity()).endDate = Date;
                Toast.makeText(getActivity(), "End: " + Date, Toast.LENGTH_LONG).show();
            }
        }
    }
}
