package com.deeaboi.ring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.deeaboi.ring.Model.messages;
import com.deeaboi.ring.ViewHolder.messageListViewHolder;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity
{

    private MediaRecorder mediaRecorder;
  //  private String filename="recorded.3gp";
  //  String file= Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +filename;

    //----------------------------------------------

//    File input_file;
//    String file= Environment.getExternalStorageDirectory().getAbsolutePath() + input_file;

    String file;
    String root_file_name,my_dp_url;
    //----------------------------------------------

    private String PhoneNumber,ProfileUrl,ProfileName,UserPhone;
    private CircleImageView dp;
    private TextView name;
    private ImageView online_offline;
    private TextView status;

    DatabaseReference online_ref,chat_ref,display_chat_ref,my_dp;

    private RecyclerView chat_list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#054D44"));

        Intent intent =getIntent();
        Bundle bundle =intent.getExtras();
        if(bundle!=null)
        {

            PhoneNumber = getIntent().getExtras().get("myphone").toString();
            UserPhone   = getIntent().getExtras().get("userPhone").toString();
            ProfileUrl = getIntent().getExtras().get("imageurl").toString();
            ProfileName = getIntent().getExtras().get("name").toString();

        }

        chat_ref=FirebaseDatabase.getInstance().getReference().child("ring").child("users");

        String state="online";

        myonline_offline(state);

        findViewById();

        setInfo();


        //----------------------reocrd----audio----------------------------------------------------------


//        file=Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +saveCurrentDate+saveCurrentTime+".3gp";

//        final File new_folder= new File("sdcard/"+"Music"+"/Ring");
//
//        input_file= new File(new_folder,saveCurrentDate+saveCurrentTime);



        RecordView recordView = (RecordView) findViewById(R.id.record_view);
        RecordButton recordButton = (RecordButton) findViewById(R.id.record_button);

        //IMPORTANT
        recordButton.setRecordView(recordView);


        recordView.setOnRecordListener(new OnRecordListener()
        {
            @Override
            public void onStart()
            {
                //Start Recording..

                //   make u unique file name every time record
                final String saveCurrentTime,saveCurrentDate;

                Calendar calendar=Calendar.getInstance();

                SimpleDateFormat currentDate=new SimpleDateFormat("MMM_dd_yyyy");
                saveCurrentDate=currentDate.format(calendar.getTime());

                SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm:ss_a");
                saveCurrentTime=currentTime.format(calendar.getTime());

                root_file_name=saveCurrentDate+saveCurrentTime+".3gp";

                file=Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +root_file_name;

                mediaRecorder= new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                mediaRecorder.setOutputFile(file);


                Log.d("RecordView", "onStart");
                record();

            }

            @Override
            public void onCancel()
            {
                //On Swipe To Cancel
                Log.d("RecordView", "onCancel");
                mediaRecorder.stop();
                mediaRecorder.release();
                Toast.makeText(ChatActivity.this, "Recording Canceled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFinish(long recordTime)
            {

                mediaRecorder.stop();
                mediaRecorder.release();
                Toast.makeText(ChatActivity.this, "Recording Finished", Toast.LENGTH_SHORT).show();

                voiceChat();

               // play_recording();

            }

            @Override
            public void onLessThanSecond()
            {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");
                mediaRecorder.release();
                Toast.makeText(ChatActivity.this, "Recording is lessthan a minute", Toast.LENGTH_SHORT).show();
            }
        });



        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd()
        {
            @Override
            public void onAnimationEnd() {
                Log.d("RecordView", "Basket Animation Finished");
            }
        });

       // voiceChat();

        my_dp=FirebaseDatabase.getInstance().getReference()
                .child("ring").child("users").child(PhoneNumber).child("details");

        my_dp.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
               if( snapshot.child("profilepicture").exists())
               {
                   my_dp_url=snapshot.child("profilepicture").getValue().toString();
               }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
            }
        });

        chat_list=findViewById(R.id.private_message_list);
        chat_list.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        display_chat_ref=FirebaseDatabase.getInstance().getReference()
                .child("ring").child("users").child(PhoneNumber).child("messages").child(UserPhone);

        showrv();

    }

    private void showrv()
    {

        FirebaseRecyclerOptions<messages> options=
                new FirebaseRecyclerOptions.Builder<messages>()
                        .setQuery(display_chat_ref,messages.class)
                        .build();
        FirebaseRecyclerAdapter<messages, messageListViewHolder>adapter=
                new FirebaseRecyclerAdapter<messages, messageListViewHolder>(options)
                {
                    @Override
                    protected void onBindViewHolder(@NonNull final messageListViewHolder holder, final int position, @NonNull final messages model)
                    {


                        display_chat_ref.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                                if(snapshot.exists())
                                {


                                    String type=model.getType().toString();
                                    final String rec_url=model.getRecord().toString();

                                    if(type.equals("send"))
                                    {

                                       Glide.with(getApplicationContext()).load(my_dp_url).into(holder.dp_send);

                                        holder.rv_receive.setVisibility(View.GONE);
                                        holder.rv_send.setVisibility(View.VISIBLE);

                                        holder.btn_send.setOnClickListener(new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                MediaPlayer mediaPlayer= new MediaPlayer();
                                                try {

                                                    mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(rec_url));
                                                    mediaPlayer.prepare();
                                                    mediaPlayer.start();

                                                    if(mediaPlayer.isPlaying())
                                                    {
                                                        holder.btn_send.setBackgroundResource(R.drawable.ic_pause_24);
                                                    }


                                                } catch (IOException e)
                                                {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });

                                    }
                                    else if(type.equals("receive"))
                                    {
                                        Glide.with(getApplicationContext()).load(ProfileUrl).into(holder.dp_receive);

                                        holder.rv_receive.setVisibility(View.VISIBLE);
                                        holder.rv_send.setVisibility(View.GONE);
                                    }





                                }
                                else
                                {
                                    //no messages start chat
                                    Toast.makeText(ChatActivity.this, "Start Conversation", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error)
                            {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public messageListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                    {

                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout,parent,false);
                        messageListViewHolder viewHolder= new messageListViewHolder(view);
                        return  viewHolder;

                    }
                };

        chat_list.setAdapter(adapter);
        adapter.startListening();


    }

    private void voiceChat()
    {

        StorageReference storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("ring")
                .child(PhoneNumber).child("voicechat").child(UserPhone);

        final StorageReference voicefileRef = storageProfilePictureRef
                        .child(root_file_name);

//        StorageTask uploadTask = voicefileRef.putFile(Uri.fromFile(new File("/sdcard/recorded.3gp")));
        StorageTask uploadTask = voicefileRef.putFile(Uri.fromFile(new File("/sdcard/"+root_file_name)));

        uploadTask.continueWithTask(new Continuation()
        {
            @Override
            public Object then(@NonNull Task task) throws Exception
            {
                if (!task.isSuccessful())
                {
                    throw task.getException();
                }

                return voicefileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener()
        {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {
                    final Uri downloadUrl = (Uri) task.getResult();

                    chat_ref.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            HashMap<String,Object> record_map=new HashMap<>();
                            record_map.put("record",downloadUrl.toString());
                            record_map.put("type","send");
                            chat_ref.child(PhoneNumber).child("messages").child(UserPhone).push().updateChildren(record_map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            HashMap<String,Object> record_map=new HashMap<>();
                                            record_map.put("record",downloadUrl.toString());
                                            record_map.put("type","receive");
                                            chat_ref.child(UserPhone).child("messages").child(PhoneNumber).push().updateChildren(record_map);
                                        }
                                    });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error)
                        {

                        }
                    });

                }
                else
                {
                    Toast.makeText(ChatActivity.this, "Error, Try Again.ewifuwuefiwef", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void myonline_offline(final String state)   // my online state
    {
        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("ring").child("users").child(PhoneNumber);

        reference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {

                HashMap<String, Object> online_offline = new HashMap<>();
                online_offline.put("status",state);
                reference.child("details").updateChildren(online_offline);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }


    private void findViewById()
    {
        dp=findViewById(R.id.user_profile_image);
                name=findViewById(R.id.contact_name);
                        online_offline=findViewById(R.id.online_icon);
                              status=findViewById(R.id.online_status);

    }

    private void setInfo()
    {
        Glide.with(getApplicationContext()).load(ProfileUrl).into(dp);
        name.setText(ProfileName);

        online_ref=FirebaseDatabase.getInstance().getReference().child("ring").child("users").child(UserPhone).child("details");

        online_ref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                String state=snapshot.child("status").getValue().toString();

                status.setText(state);

                if(state.equals("online"))
                {
                    online_offline.setVisibility(View.VISIBLE);
                    online_offline.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                            R.color.green), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
                else
                    if(state.equals("offline"))
                    {
                        online_offline.setVisibility(View.VISIBLE);
                        online_offline.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                                R.color.red), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }

    private void record()
    {
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private void play_recording()
    {
        MediaPlayer mediaPlayer= new MediaPlayer();
        try {
            mediaPlayer.setDataSource(file);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        Toast.makeText(this, "filename"+file, Toast.LENGTH_SHORT).show();


    }


    @Override
    protected void onPause()
    {
        super.onPause();
        String state = "offline";
        myonline_offline(state);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        String state = "online";
        myonline_offline(state);

    }





}