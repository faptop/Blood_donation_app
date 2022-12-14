package com.example.bda;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private CircleImageView nav_profile_image;
    private TextView nav_full_name,nav_type,nav_blood_group,nav_email_id;
    private DatabaseReference userdatabaseref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        --------------------------------------------
        drawerLayout=findViewById(R.id.drawerLayout);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Blood donation app");
        navigationView=findViewById(R.id.nav_bar);
        // attaching nav_bar and drawer
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(MainActivity.this,drawerLayout
        ,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
//        ----------------------------------------------------------------------------------
        // fetching data from firebase to show data in nav_bar
        nav_profile_image= navigationView.getHeaderView(0).findViewById(R.id.nav_user_image);
        nav_full_name=navigationView.getHeaderView(0).findViewById(R.id.nav_user_fullname);
        nav_email_id=navigationView.getHeaderView(0).findViewById(R.id.nav_user_email);
        nav_blood_group=navigationView.getHeaderView(0).findViewById(R.id.nav_user_bloodgroup);
        nav_type=navigationView.getHeaderView(0).findViewById(R.id.nav_user_type);

        userdatabaseref= FirebaseDatabase.getInstance().getReference().child("users").child(
                FirebaseAuth.getInstance().getCurrentUser().getUid()
        );
        userdatabaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //fetching name value from snapshot(database)
                    String name= snapshot.child("name").getValue().toString();
//                    here snapshot.child(firebase-->name of text given)
                    // setting name data-->nav_name
                    nav_full_name.setText(name);
                    String email=snapshot.child("email").getValue().toString();
                    nav_email_id.setText(email);
                    String type=snapshot.child("Type").getValue().toString();
                    nav_type.setText(type);
                    String blood_grp=snapshot.child("bloodgroup").getValue().toString();
                    nav_blood_group.setText(blood_grp);
                 
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}