package com.deeaboi.ring;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import static com.deeaboi.ring.App.CHANNEL_ID;

public class RingService extends Service
{
    MediaPlayer mediaPlayer;


    @Override
    public void onCreate()
    {
        super.onCreate();





    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Intent notificationIntent=new Intent(this, MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,
                0,notificationIntent,0);

       // Intent stopSelf = new Intent(this, StopServiceReceiver.class);

   //     PendingIntent pStopSelf = PendingIntent.getBroadcast(this,0,stopSelf,PendingIntent.FLAG_UPDATE_CURRENT); //FLAG_CANCEL_CURRENT

       // Bitmap largeicon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);    //right icon on notification i.e logo

        Notification notification= new NotificationCompat.Builder(this,CHANNEL_ID)
//                .setContentTitle("Reelsica is ON")
//                .setContentText("Copy link to Reelsica for download")
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                //.setLargeIcon(largeicon)
//                .setColor(Color.parseColor("#5C7480"))
//               // .addAction(R.mipmap.ic_launcher,getString(R.string.stop),pStopSelf)
////                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        //do heavy work on a background thread

        StartService();  // get data from clipboard
        return START_NOT_STICKY;
    }

    private void StartService()
    {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ring").child("users").child("user1");

        ref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String audio= snapshot.child("recording").getValue().toString();
                Toast.makeText(RingService.this, audio, Toast.LENGTH_SHORT).show();

                playaudio(audio);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });


        Toast.makeText(this, "Omm Namah Shivay", Toast.LENGTH_SHORT).show();
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;

    }
}
