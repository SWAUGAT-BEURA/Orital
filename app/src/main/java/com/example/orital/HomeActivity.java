package com.example.orital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.annotation.Nullable;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SharedPreferences prefManager;
    private SharedPreferences.Editor editor;
    private DrawerLayout drawerLayout;
    private FragmentTransaction fm;
    private ImageView profileimage;
    private TextView username;
    private static final int PICK_IMAGE = 1;
    Uri imageUri;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storagehome;
    private StorageReference storageReferencehome;
    private FirebaseAuth firebaseAuthhome;
    public static final String TAG = "TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        prefManager = getApplicationContext().getSharedPreferences("LOGIN", MODE_PRIVATE);
        editor = prefManager.edit();
        profileimage=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);

        Toolbar toolbar = findViewById(R.id.tl_main);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        firebaseAuthhome=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        String uid=firebaseAuthhome.getCurrentUser().getUid();
        DocumentReference documentReference=firebaseFirestore.collection("users").document(uid);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                username.setText(documentSnapshot.getString("Name"));
            }
        });


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//            getSupportActionBar().setDisplayShowTitleEnabled(false); // for not showing the title.
            getSupportActionBar().setTitle("Orital");
        }
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            Glide.with(this)
                    .load(user.getPhotoUrl())
                    .into(profileimage);
            username.setText(user.getDisplayName());
        }
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerToggle.syncState();
        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "Sellect Picture"), PICK_IMAGE);
            }
        });


        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.action_home);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {


            case R.id.action_search:

                Intent browserintent = new Intent(Intent.ACTION_VIEW);
                browserintent.setData(Uri.parse("http://www.google.com"));
                startActivity(browserintent);

                break;

            case R.id.action_logout:
                new AlertDialog.Builder(HomeActivity.this).setTitle("Alert")
                        .setMessage("Are you sure you want to Logout")
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                editor.putBoolean("ISLOGGEDIN", false);

                                editor.apply();

                                FirebaseAuth.getInstance().signOut();

                                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                finish();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
                break;
            case R.id.action_rating:
                Toast.makeText(HomeActivity.this, "please  us your review through gmail account", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_report:
                Toast.makeText(HomeActivity.this, "report us for solving your problems", Toast.LENGTH_SHORT).show();
                String data3 = "swaugatkumarbeura5@gmail.com";
                Intent email1=new Intent(Intent.ACTION_SEND);
                email1.putExtra(Intent.EXTRA_EMAIL,new String[]{data3});
                email1.putExtra(Intent.EXTRA_SUBJECT,"Help");
                email1.putExtra(Intent.EXTRA_TEXT,"HELLO GUYS, I have a trouble shooting in the app");
                email1.setType("message/rfc822");
                startActivity(Intent.createChooser(email1,"Email Client"));
                break;

            case  R.id.action_help:
                String data1 = "swaugatkumarbeura5@gmail.com";
                Intent email=new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL,new String[]{data1});
                email.putExtra(Intent.EXTRA_SUBJECT,"Help");
                email.putExtra(Intent.EXTRA_TEXT,"HELLO GUYS, I have a trouble shooting in the app");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email,"Email Client"));
                break;
            case R.id.action_settings:
                Toast.makeText(HomeActivity.this, "settings changed", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        fm = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.action_order:
                startActivity(new Intent(HomeActivity.this, OrderActivity.class));
                break;
            case R.id.action_appointer:
                startActivity(new Intent(HomeActivity.this, AppointerActivity.class));
                break;
            case R.id.action_search:

                Intent browserintent = new Intent(Intent.ACTION_VIEW);
                browserintent.setData(Uri.parse("http://www.google.com"));
                startActivity(browserintent);

                break;

            case R.id.action_logout:
                new AlertDialog.Builder(HomeActivity.this).setTitle("Alert")
                        .setMessage("Are you sure you want to Logout")
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                editor.putBoolean("ISLOGGEDIN", false);

                                editor.apply();

                                FirebaseAuth.getInstance().signOut();

                                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                finish();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
                break;
            case R.id.action_rating:
                Toast.makeText(HomeActivity.this, "please  us your review through gmail account", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_report:
                Toast.makeText(HomeActivity.this, "report us for solving your problems", Toast.LENGTH_SHORT).show();
                String data3 = "swaugatkumarbeura5@gmail.com";
                Intent email1=new Intent(Intent.ACTION_SEND);
                email1.putExtra(Intent.EXTRA_EMAIL,new String[]{data3});
                email1.putExtra(Intent.EXTRA_SUBJECT,"Help");
                email1.putExtra(Intent.EXTRA_TEXT,"HELLO GUYS, I have a trouble shooting in the app");
                email1.setType("message/rfc822");
                startActivity(Intent.createChooser(email1,"Email Client"));
                break;

            case  R.id.action_help:
                String data1 = "swaugatkumarbeura5@gmail.com";
                Intent email=new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL,new String[]{data1});
                email.putExtra(Intent.EXTRA_SUBJECT,"Help");
                email.putExtra(Intent.EXTRA_TEXT,"HELLO GUYS, I have a trouble shooting in the app");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email,"Email Client"));
                break;



        }

        return false;

    }
    public void onappointerclicked(View view){
        startActivity(new Intent(HomeActivity.this, AppointerActivity.class));

    }
    public void onorderclicked(View view){
        startActivity(new Intent(HomeActivity.this, OrderActivity.class));

    }
    public void onbookambulance(View view){
        notify_ambulance();
    }

    private void notify_ambulance() {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(HomeActivity.this)
                .setSmallIcon(R.drawable.ambulance)
                .setContentTitle("Ambulance Booking Confirmed")
                .setContentText("Your Ambulance will be reaching soon,turn on your location, in case of any trouble contact in the toll free number");
        Intent notifyIntent= new Intent(this,Ambulance_order.class);
        PendingIntent contentintent=PendingIntent.getActivity(this,0,notifyIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentintent);
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                profileimage.setImageBitmap(bitmap);
                handleupload(bitmap);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }

    private void handleupload(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        String UID=FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference=FirebaseStorage.getInstance().getReference()
                .child("ProfileImage")
                .child(UID+".jpeg");
        reference.putBytes(byteArrayOutputStream.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"on failure: ",e.getCause());
                    }
                });


    }
    private void getDownloadUrl(StorageReference reference){
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG,"on success: "+uri);
                        setUserProfileUrl(uri);
                    }
                });

    }

    private void setUserProfileUrl(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();
        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(HomeActivity.this,"updated successfully",Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "profile image upload failed", Toast.LENGTH_SHORT).show();

                    }
                });

    }


}
