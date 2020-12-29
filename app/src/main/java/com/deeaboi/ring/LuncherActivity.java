package com.deeaboi.ring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.deeaboi.ring.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.paperdb.Paper;

public class LuncherActivity extends AppCompatActivity
{
    private static  int SPLASH_SCREEN=1000;
    private String PhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luncher);

        Paper.init(this);
        PhoneNumber=Paper.book().read(Prevalent.PhoneNumber);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#00695C"));



        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if(PhoneNumber!=null)
                {



                    Intent intent = new Intent(LuncherActivity.this, HomeActivity.class);
                    intent.putExtra("phone",PhoneNumber);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(LuncherActivity.this, Signup.class);
                 //   Intent intent = new Intent(LuncherActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        },SPLASH_SCREEN);

    }



}