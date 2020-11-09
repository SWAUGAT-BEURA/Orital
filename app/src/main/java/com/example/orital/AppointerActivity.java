package com.example.orital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AppointerActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    FirebaseAuth firebaseAuthappointer;
    FirebaseFirestore firestoreappointer;
    String userId;
    EditText Hospital_name,Patient_Name,Patient_age,Patient_Address,Doctor_name,date_time;
    Button confirm;
    ProgressBar progressBar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointer);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firebaseAuthappointer=FirebaseAuth.getInstance();
        firestoreappointer=FirebaseFirestore.getInstance();
        date_time=findViewById(R.id.Date);
        Doctor_name=findViewById(R.id.Doctor);
        Patient_Address=findViewById(R.id.Patient_address);
        Patient_age=findViewById(R.id.Patient_age);
        Patient_Name=findViewById(R.id.Patient_name);
        Hospital_name=findViewById(R.id.Hospital_name);
        confirm=findViewById(R.id.confirm);
        progressBar1=findViewById(R.id.progressbar1);

        date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker();

            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String patientname = Patient_Name.getText().toString().trim();
                String hospitalname = Hospital_name.getText().toString().trim();
                final String patientaddress = Patient_Address.getText().toString().trim();
                String patientage = Patient_age.getText().toString().trim();
                final String doctorname = Doctor_name.getText().toString().trim();
                final String date = date_time.getText().toString().trim();

                if (TextUtils.isEmpty(patientname)) {
                    Patient_Name.setError("Name required");
                    return;
                }
                if (TextUtils.isEmpty(hospitalname)) {
                    Hospital_name.setError("Hospital required");
                    return;
                }
                if (TextUtils.isEmpty(patientaddress)) {
                    Patient_Address.setError("Address required");
                    return;
                }
                if (TextUtils.isEmpty(patientage)) {
                    Patient_age.setError("Age required");
                    return;
                }
                if (TextUtils.isEmpty(date)) {
                    date_time.setError("date and time required");
                    return;
                }

                progressBar1.setVisibility(View.VISIBLE);
                userId = firebaseAuthappointer.getCurrentUser().getUid();
                DocumentReference documentReference = firestoreappointer.collection("Appointments").document(userId);
                Map<String, Object> appointments = new HashMap<>();
                appointments.put("Name", patientname);
                appointments.put("Hospital", hospitalname);
                appointments.put("Address", patientaddress);
                appointments.put("Age", patientage);
                appointments.put("Date", date);
                appointments.put("Doctor", doctorname);
                documentReference.set(appointments).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG,"on successfull user created for"+userId);
                    }
                });progressBar1.setVisibility(View.INVISIBLE);
                Toast.makeText(AppointerActivity.this,"Appointment Fixed Successfully",Toast.LENGTH_SHORT).show();
                notify_appointement();
            }
        });
    }

    private void notify_appointement() {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(AppointerActivity.this)
                .setSmallIcon(R.drawable.ambulance)
                .setContentTitle("Appointment has been Confirmed")
                .setContentText("We are looking forward to serve you, in case of any trouble contact in the toll free number");

        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }
    private void openDatePicker() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            DatePickerDialog datePickerDialog = null;

            datePickerDialog = new DatePickerDialog(AppointerActivity.this);

            datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    String date = String.valueOf(day).concat("-").concat(String.valueOf(month + 1)).concat("-").concat(String.valueOf(year));

                    TimePickerDialog timePickerDialog = new TimePickerDialog(AppointerActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hours, int min) {
                            String duration = "";
                            int hourvalue;
                            if (hours < 12) {
                                hourvalue = hours;
                                duration = "AM";
                            } else if (hours == 12) {
                                hourvalue = hours;
                                duration = "PM";
                            } else {
                                hourvalue = hours % 12;
                                duration = "PM";
                            }
                            String date = String.valueOf(hourvalue).concat(":").concat(String.valueOf(min)).concat(" ").concat(duration);

                        }
                    }, Calendar.HOUR_OF_DAY, Calendar.MINUTE, false);
                    timePickerDialog.show();

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM YYYY");
                    String currenTime = timeFormat.format(calendar.getTime());
                    String currentDate = dateFormat.format(calendar.getTime());

                    date_time.setText(date + "-" + currenTime);
                }
            });

            datePickerDialog.show();
        }

    }
}
