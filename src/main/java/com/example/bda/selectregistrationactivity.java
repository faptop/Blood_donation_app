package com.example.bda;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class selectregistrationactivity extends AppCompatActivity {
    private Button donor;
    private Button recipient;
    private TextView backbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectregistrationactivity);
        donor=findViewById(R.id.donorbutton);
        donor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(selectregistrationactivity.this,DonorActivity.class);
                startActivity(intent);
            }
        });
        recipient=findViewById(R.id.recipientbutton);
        recipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(selectregistrationactivity.this,Recipient_Activity.class);
                startActivity(intent);
            }
        });
        backbutton=findViewById(R.id.backbutton1);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(selectregistrationactivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}