package com.example.orital;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OrderActivity extends AppCompatActivity {
    Button mcaptureBtn,confirmbtn;
    ImageView mImageView;
    EditText mmedicine,quantity;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    FirebaseAuth firebaseAuthorder;
    private Uri filepath;
    private ProgressBar progressBar;
    FirebaseFirestore firestoreorder;
    String userid;
    public static final String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mImageView=findViewById(R.id.image_view);
        mcaptureBtn=findViewById(R.id.upload);
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        firebaseAuthorder=FirebaseAuth.getInstance();
        confirmbtn=findViewById(R.id.confirm);
        mmedicine=findViewById(R.id.medicines);
        quantity=findViewById(R.id.quantity);
        progressBar=findViewById(R.id.progressbar2);
        firestoreorder=FirebaseFirestore.getInstance();

        mcaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadimage_firebase();
                final String Medicines=mmedicine.getText().toString().trim();
                final String Quantity=quantity.getText().toString().trim();
                if (TextUtils.isEmpty(Medicines)){
                    mmedicine.setError("Medicines required");
                    return;
                }
                if (TextUtils.isEmpty(Quantity)){
                    quantity.setError("Quantities required");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                userid=firebaseAuthorder.getCurrentUser().getUid();
                DocumentReference documentReference=firestoreorder.collection("Order").document(userid);
                Map<String,Object> order=new HashMap<>();
                order.put("Medicine",Medicines);
                order.put("Quantity",Quantity);
                documentReference.set(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OrderActivity.this,"Order Placed Succesfully",Toast.LENGTH_SHORT).show();
                        notify_order();
                    }
                });progressBar.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void notify_order() {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(OrderActivity.this)
                .setSmallIcon(R.drawable.ambulance)
                .setContentTitle("Medicines Order Confirmed")
                .setContentText("Your order will be reaching soon,turn on your location, in case of any trouble contact in the toll free number");

        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

    }

    private void chooseImage() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode==RESULT_OK){
            filepath=data.getData();
            try {
                Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                mImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void uploadimage_firebase() {
        if (filepath!=null){

            userid = firebaseAuthorder.getCurrentUser().getUid();
            StorageReference refernce=storageReference.child(userid+"images/"+ UUID.randomUUID().toString());
            refernce.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(OrderActivity.this,"added succesfully",Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }


    }

}
