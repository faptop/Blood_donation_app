package com.example.bda;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DonorActivity extends AppCompatActivity {
    private TextView backbutton;
    private CircleImageView profile_image;
    private TextInputEditText registerfullname,registerid,registerphonenumber,registeremailid,registerpassword;
    private Spinner bloodgroupsspinner;
    private Button registerbutton;
    // to pass image to gallery
    private Uri resulturi;
    // establishing loader
    private ProgressDialog loader;
    // database
    private FirebaseAuth mauth;
    // to save data to database
    private DatabaseReference userdatavaseref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);
        backbutton=findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(DonorActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        registerfullname =findViewById(R.id.registerfullname);
        registerid=findViewById(R.id.registerId);
        registerphonenumber=findViewById(R.id.registerphonenumber);
        registeremailid=findViewById(R.id.registeremailid);
        registerpassword=findViewById(R.id.registerpassword);
        profile_image=findViewById(R.id.profile_image);
        bloodgroupsspinner=findViewById(R.id.bloodgroupsspinner);
        registerbutton=findViewById(R.id.registerbutton);
        // initialising loader
        loader=new ProgressDialog(this);
        // initialising database
        mauth=FirebaseAuth.getInstance();

        //to connect gallery to image profile
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting element by trimming both side spaces
                final String email=registeremailid.getText().toString().trim();
                final String password=registerpassword.getText().toString().trim();
                final String name=registerfullname.getText().toString().trim();
                final String id=registerid.getText().toString().trim();
                final String bloodgroup=bloodgroupsspinner.getSelectedItem().toString();
                final String phonenumber=registerphonenumber.toString().trim();
                System.out.println(email);
                System.out.println(password);
                // checking for empty
                if(TextUtils.isEmpty(email)){
                    registeremailid.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    registerpassword.setError("Password is required");
                    return;
                }
                if(TextUtils.isEmpty(name)){
                    registerfullname.setError("UserName is Required");
                    return;
                }
                if(TextUtils.isEmpty(id)){
                    registerid.setError("Userid is Required");
                    return;
                }
                if(TextUtils.isEmpty(phonenumber)){
                    registerphonenumber.setError("phone number is Required");
                    return;
                }
                if(bloodgroup.equals("Select Your blood group")){
                    Toast.makeText(DonorActivity.this, "select Blood group", Toast.LENGTH_SHORT).show();
                    return;
                }
                // when everything is fine
                else{
                    loader.setMessage("Registering You....");
                    // loader should not cancel screen
                    loader.setCanceledOnTouchOutside(false);
                    // showing loader
                    loader.show();
                    // creating user
                    mauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                String error= task.getException().toString();
                                Toast.makeText(DonorActivity.this, "Error"+error, Toast.LENGTH_SHORT).show();
                            }
                            else{
                                // getting data to database
                                // with folder Users-->currentuserid
                                String currentuserid=mauth.getCurrentUser().getUid();
                                userdatavaseref= FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child("currrentuserid");
                                // Hashmap uses <key,value> pair to store data in database
                                HashMap userinfo=new HashMap();
                                userinfo.put("id",currentuserid);
                                userinfo.put("name",name);
                                userinfo.put("email",email);
                                userinfo.put("idnumber",id);
                                userinfo.put("bloodgroup",bloodgroup);
                                userinfo.put("Type","donor");
                                userinfo.put("search","donor"+bloodgroup);
                                userdatavaseref.updateChildren(userinfo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(DonorActivity.this, "Data set successful", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            Toast.makeText(DonorActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                        finish();

                                    }
                                });
                                //now working with image -->saving image to firebase database
                                // * process to save image--> we save image in cloud firebase-->get a link by cloud and save link in firebase database
                                if(resulturi!=null){
                                    final StorageReference filepath= FirebaseStorage.getInstance().getReference()
                                            .child("Profile images").child(currentuserid);
                                    Bitmap bitmap=null;
                                    try {
                                        bitmap= MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resulturi);

                                    }catch (IOException e){
                                        e.printStackTrace();
                                    }
                                    ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG,20,byteArrayOutputStream);
                                    byte[] data=byteArrayOutputStream.toByteArray();
                                    UploadTask uploadTask= filepath.putBytes(data);
                                    uploadTask.addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DonorActivity.this, "Image uploaded failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            if(taskSnapshot.getMetadata()!=null && taskSnapshot.getMetadata().getReference()!=null){
//                                                getting url of image saved in cloud firebase -->to save in database
                                                Task<Uri> result= taskSnapshot.getStorage().getDownloadUrl();
                                                result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        String imageurl=uri.toString();
                                                        Map newimagemap= new HashMap();
//                                                        geting imageurl in Hashmap format
                                                        newimagemap.put("profileimageurl",imageurl);

                                                        // saving image url to database
                                                        userdatavaseref.updateChildren(newimagemap).addOnCompleteListener(new OnCompleteListener() {
                                                            @Override
                                                            public void onComplete(@NonNull Task task) {
                                                                if(task.isSuccessful()){
                                                                    Toast.makeText(DonorActivity.this, "image url added to databse", Toast.LENGTH_SHORT).show();
                                                                }
                                                                else{
                                                                    Toast.makeText(DonorActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                        finish();
                                                    }
                                                });
                                            }
                                        }
                                    });
                                    Intent intent=new Intent(DonorActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    loader.dismiss();
                                }
                            }
                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null){
            resulturi=data.getData();
            // setting image(gallery) to profile_image
            profile_image.setImageURI(resulturi);
        }
    }
}