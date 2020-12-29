package com.deeaboi.ring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.deeaboi.ring.Model.Users;
import com.deeaboi.ring.ViewHolder.userListViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity
{

    private String PhoneNumber="";
    ArrayList<String> arrayList,fireList,newarray;
    private DatabaseReference userref,dp_ref;
    RecyclerView recyclerView;

    String profileUrl,profileName;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#054D44"));

        PhoneNumber=getIntent().getExtras().get("phone").toString();

        String state="online";

        online_offline(state);

        recyclerView=findViewById(R.id.contactsList_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));

        arrayList=new ArrayList<>();
        fireList=new ArrayList<>();
        newarray=new ArrayList<>();


        getcontactlist(); //from firebase ,all users


    }

    private void online_offline(final String state)
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


    private void getcontactlist()
    {

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("ring").child("allusers");

        rootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                for (DataSnapshot ds :dataSnapshot.getChildren())
                {
                    String num =ds.getKey();
                    fireList.add(num);
                }
                //   tv.setText(fireList.toString());

                getusercontact();  //all contacts of simcard

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });

    }


    private void getusercontact()
    {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null,null);

        while (cursor.moveToNext())
        {
            // String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            //remove blank space nad county code and special charecters first
            phone = phone.replaceAll("[^0-9]","");

            //cheak for 91 or not
            if(phone.length()>10)
            {
                phone=phone.substring(2); //delete 91 if have otherwise not
            }

            //addd contacts to array list
            arrayList.add(phone);
            //  contact.setText(arrayList.toString()+"\n");
            // remove duplicate
            //make this upadte later
            for(String nwphn: arrayList)      //remove dulicates
            {
                if(!newarray.contains(nwphn) && !(nwphn.length()<10) )
                {
                    newarray.add(nwphn);
                }
            }

          //  tv.setText(newarray.toString()+"\n");

        }

        compairboth();  //compair both firebase and sim contact

    }



    private void compairboth()
    {
        newarray.retainAll(fireList);
        //get phone name later;by using name cusor later
        //upadte new contact in database
        //tv.setText(newarray.toString()+"\n");
        update(newarray);

    }

    private void update(final ArrayList<String> newarray)   //update the contact list in the database accordingly new list
    {
        int arraysize=newarray.size();
        for(int i=0;i<arraysize;i++)
        {
            final String phone=newarray.get(i).toString();
            userref=FirebaseDatabase.getInstance().getReference().child("ring").child("users").child(PhoneNumber).child("contacts");
            userref.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {

                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap. put("phone",phone);
                    userref.child(phone).updateChildren(userMap);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });

        }

        showrv();

    }

    private void showrv()
    {
        userref=FirebaseDatabase.getInstance().getReference().child("ring").child("users").child(PhoneNumber).child("contacts");

        userref.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                       displayFriends();
                }
                else
                {
                    Toast.makeText(HomeActivity.this, "No contacts using  app share with friends", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });


    }

    private void displayFriends()
    {
        FirebaseRecyclerOptions<Users> options=
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(userref,Users.class)
                        .build();

        FirebaseRecyclerAdapter<Users, userListViewHolder> adapter=
                           new FirebaseRecyclerAdapter<Users, userListViewHolder>(options)
                           {
                               @Override
                               protected void onBindViewHolder(@NonNull final userListViewHolder holder, int position, @NonNull Users model)
                               {

                                   //display all contacts of the user

                                   final String number=model.getPhone();
                                   holder.number.setText(number);

                                  // display the profile picture also

                                   dp_ref=FirebaseDatabase.getInstance().getReference().child("ring").child("users").child(number).child("details");
                                   dp_ref.addValueEventListener(new ValueEventListener()
                                   {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot snapshot)
                                       {

                                        if(snapshot.child("profilepicture").exists())
                                        {
                                            profileUrl=snapshot.child("profilepicture").getValue().toString();
                                            Glide.with(getApplicationContext()).load(profileUrl).into(holder.dp);
                                        }

                                        profileName=snapshot.child("username").getValue().toString();

                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError error)
                                       {

                                       }
                                   });

                                   holder.cardView.setOnClickListener(new View.OnClickListener()
                                   {
                                       @Override
                                       public void onClick(View v)
                                       {
                                           Intent intent= new Intent(HomeActivity.this,ChatActivity.class);
                                           intent.putExtra("myphone",PhoneNumber);
                                           intent.putExtra("userPhone",number);
                                           intent.putExtra("imageurl",profileUrl);
                                           intent.putExtra("name",profileName);
                                           startActivity(intent);
                                       }
                                   });

                               }

                               @NonNull
                               @Override
                               public userListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                               {
                                   View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.no_list,parent,false);
                                   userListViewHolder viewHolder= new userListViewHolder(view);
                                   return  viewHolder;
                               }
                           };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    @Override
    protected void onPause()
    {
        super.onPause();
        String state = "offline";
        online_offline(state);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        String state = "online";
        online_offline(state);

    }
}



// all contacts in database and your contacts compair them if some are same e.i those contacts you have using the app