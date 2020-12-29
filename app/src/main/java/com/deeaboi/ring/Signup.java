package com.deeaboi.ring;

import androidx.appcompat.app.AppCompatActivity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.material.textfield.TextInputEditText;

public class Signup extends AppCompatActivity
{
   private TextInputEditText country_code,phone_number;
   private Button next;
   private ProgressDialog LoadingBar;

    private static final int CREDENTIAL_PICKER_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#9E9E9E"));      // status bar color grey

        country_code=findViewById(R.id.country_code);
        phone_number=findViewById(R.id.phone_number);
        next=findViewById(R.id.next_btn);
        LoadingBar=new ProgressDialog(this);

        //select phone no dialog
        selectPhone();

        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                checkFields();
            }
        });


    }

    private void selectPhone()
    {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent = Credentials.getClient(getApplicationContext()).getHintPickerIntent(hintRequest);
        try
        {
            startIntentSenderForResult(intent.getIntentSender(), CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0,new Bundle());
        }
        catch (IntentSender.SendIntentException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK)
        {
            // Obtain the phone number from the result
            Credential credentials = data.getParcelableExtra(Credential.EXTRA_KEY);

            phone_number.setText(credentials.getId().substring(3));

            //get the selected phone number
            //Do what ever you want to do with your selected phone number here


        }
        else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE)
        {
            // *** No phone numbers available ***
            Toast.makeText(getApplicationContext(), "No phone numbers found", Toast.LENGTH_LONG).show();
        }

    }



    private void checkFields()
    {

        if(country_code.getText().toString().isEmpty())
        {
            Toast.makeText(Signup.this, "Enter country code", Toast.LENGTH_SHORT).show();
        }
        else
        if(phone_number.getText().toString().isEmpty())
        {
            Toast.makeText(Signup.this, "Enter phone number", Toast.LENGTH_SHORT).show();
        }
        else
        {
            LoadingBar.setMessage("Connecting");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();

            Intent intent= new Intent(Signup.this,OtpVerifcationActivity.class);
            intent.putExtra("phone",country_code.getText().toString()+phone_number.getText().toString());
            startActivity(intent);
            finish();

        }

    }

}