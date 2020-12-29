package com.deeaboi.ring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.deeaboi.ring.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileInfoActivity extends AppCompatActivity
{
    private CircleImageView circleImageView;
    private TextInputEditText UserName;
    private Button Next_Btn;
    private Uri imageUri;
    private String checker = "";
    private DatabaseReference ref =  FirebaseDatabase.getInstance().getReference().child("ring");
    private String PhoneNumber="";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        PhoneNumber=getIntent().getExtras().get("phone").toString();

        progressDialog = new ProgressDialog(this);

        findViewById();

        setOnClickListeners();

    }



    private void findViewById()
    {
        circleImageView=findViewById(R.id.circular_image);
        UserName=findViewById(R.id.user_name);
        Next_Btn=findViewById(R.id.next_btn);

    }

    private void setOnClickListeners()
    {
        circleImageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(ProfileInfoActivity.this);
            }
        });

        Next_Btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                verify();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            circleImageView.setImageURI(imageUri);
        }
        else
        {
            Intent intent= new Intent(ProfileInfoActivity.this,ProfileInfoActivity.class);
            intent.putExtra("phone",PhoneNumber);
            startActivity(intent);
            finish();

        }
    }

    private void verify()
    {
        if(UserName.getText().toString().isEmpty())
        {
            Toast.makeText(this, "You are required to enter your name before continuing.", Toast.LENGTH_SHORT).show();
        }

        else
            {
                //show loading bar

                progressDialog.setMessage("Updating profile");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                //update database and go to Homeactivity
                if(checker.equals("clicked"))
                {
                    userInfoSaved();
                }
                else
                {
                    updateOnlyUserInfo();
                }
            }
    }



    private void userInfoSaved()
    {
        if (imageUri !=null)
        {
          StorageReference storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("ring").child("Profile pictures");


            final StorageReference fileRef = storageProfilePictureRef
                    .child(PhoneNumber).child(PhoneNumber+".jpg");

             StorageTask  uploadTask = fileRef.putFile(imageUri);

             uploadTask.continueWithTask(new Continuation()
             {
                 @Override
                 public Object then(@NonNull Task task) throws Exception
                 {
                     if (!task.isSuccessful())
                     {
                         throw task.getException();
                     }

                     return fileRef.getDownloadUrl();
                 }
             })
                     .addOnCompleteListener(new OnCompleteListener<Uri>()
             {
                 @Override
                 public void onComplete(@NonNull Task task)
                 {
                     if (task.isSuccessful())
                     {
                         Uri downloadUrl = (Uri) task.getResult();
                         HashMap<String, Object> userMap = new HashMap<>();
                         userMap.put("username",UserName.getText().toString());
                         userMap. put("profilepicture", downloadUrl.toString());
                         userMap.put("phone",PhoneNumber);
                         ref.child("users").child(PhoneNumber).child("details").updateChildren(userMap)
                                 .addOnCompleteListener(new OnCompleteListener<Void>()
                                 {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task)
                                     {
                                         if(task.isSuccessful())
                                         {
                                             Intent intent = new Intent(ProfileInfoActivity.this,HomeActivity.class);
                                             intent.putExtra("phone",PhoneNumber);
                                             startActivity(intent);
                                             finish();
                                             progressDialog.dismiss();

                                         }

                                     }
                                 });

                     }
                     else
                     {
                         Toast.makeText(ProfileInfoActivity.this, "Error, Try Again.", Toast.LENGTH_SHORT).show();
                         progressDialog.dismiss();
                     }
                 }
             });


        }
        else
        {
            Toast.makeText(this, "Profile picture is not selected.", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }


    }

    private void updateOnlyUserInfo()
    {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("username",UserName.getText().toString());
        userMap.put("phone",PhoneNumber);

        ref.child("users").child(PhoneNumber).child("details").updateChildren(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Intent intent = new Intent(ProfileInfoActivity.this,HomeActivity.class);
                    intent.putExtra("phone",PhoneNumber);
                    startActivity(intent);
                    finish();
                    progressDialog.dismiss();
                }

            }
        });


    }


}