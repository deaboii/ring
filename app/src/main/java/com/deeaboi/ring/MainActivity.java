package com.deeaboi.ring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class MainActivity extends AppCompatActivity
{

   private Button button ;
    private Button playbutton ;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button=findViewById(R.id.unmute);
        playbutton=findViewById(R.id.getaudio);
        playbutton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)

            {
                getaudio();
            }
        });


        NotificationManager notificationManager =
                (NotificationManager) MainActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !notificationManager.isNotificationPolicyAccessGranted())
        {

            Intent intent = new Intent(
                    android.provider.Settings
                            .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

            startActivity(intent);
        }


        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {



                startservice();



//                AudioManager audioManager = (AudioManager)MainActivity.this.getSystemService(Context.AUDIO_SERVICE);
//                audioManager.setRingerMode(AudioManager.MODE_NORMAL);
//
//
//
//                // To set full volume
//                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
//                audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume,0);
////                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
//                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

            }
        });


    }


    private void getaudio()
    {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ring").child("users").child("user1");

          ref.addValueEventListener(new ValueEventListener()
          {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot)
              {
                 String audio= snapshot.child("recording").getValue().toString();
                  Toast.makeText(MainActivity.this, audio, Toast.LENGTH_SHORT).show();

                 playaudio(audio);
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error)
              {

              }
          });

    }

    private void playaudio(String audio)
    {

        mediaPlayer= new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(audio);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(this, "playing music", Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
        }


    }

    private void startservice()
    {
        Intent serviceIntent =new Intent(MainActivity.this, RingService.class);
        startService(serviceIntent);

    }



    public void onClick(View view)
    {
       if(view.getId()==R.id.mic)
       {
           //record
           record();
       }
       else if(view.getId()==R.id.chat)
       {
           Intent intent = new Intent(MainActivity.this,ChatActivity.class);
           startActivity(intent);


       }
    }

    private void record()
    {




    }


}