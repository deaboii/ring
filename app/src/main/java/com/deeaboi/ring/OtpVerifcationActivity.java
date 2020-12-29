package com.deeaboi.ring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.deeaboi.ring.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;


public class OtpVerifcationActivity extends AppCompatActivity
{
    private TextView verify,wrong_number;
    //text watcher
    private EditText editText1,editText2,editText3,editText4,editText5,editText6;
    private EditText[] editTexts;

    private String phonenumber;
    private FirebaseAuth mAuth;
    private ProgressDialog LoadingBar;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String mVerificationId ;
    public static final String SHARED_PREFS="sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verifcation);

        Paper.init(this);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#9E9E9E"));      // status bar color grey

        phonenumber= getIntent().getExtras().get("phone").toString();

        LoadingBar=new ProgressDialog(this);

        findViewById();

        verify.setText("Verify "+phonenumber);

        mAuth=FirebaseAuth.getInstance();

        callbacks();

        getOTP();

        wrong_number.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                   Intent intent= new Intent(OtpVerifcationActivity.this,Signup.class);
                   startActivity(intent);
                   finish();
            }
        });

        new OTPReceiver().get_otp(editText1,editText2,editText3,editText4,editText5,editText6);

    }

    private void callbacks()
    {

        callbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
            {
//                LoadingBar.dismiss();
//                Toast.makeText(OtpVerifcationActivity.this, "Registered Successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e)
            {
                LoadingBar.dismiss();
                Toast.makeText(OtpVerifcationActivity.this, "Verification Failed, Try Again.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken)
            {

                LoadingBar.dismiss();
                mVerificationId = verificationId;
                Toast.makeText(OtpVerifcationActivity.this, "Code Send Successfully", Toast.LENGTH_SHORT).show();

            }

        };

    }

    private void findViewById()
    {

        verify=findViewById(R.id.verify_text);
        wrong_number=findViewById(R.id.wrong_number);

        editText1=findViewById(R.id.otpEdit1);
        editText2=findViewById(R.id.otpEdit2);
        editText3=findViewById(R.id.otpEdit3);
        editText4=findViewById(R.id.otpEdit4);
        editText5=findViewById(R.id.otpEdit5);
        editText6=findViewById(R.id.otpEdit6);

        editTexts = new EditText[]{editText1, editText2, editText3, editText4,editText5,editText6};

        editText1.addTextChangedListener(new OtpVerifcationActivity.PinTextWatcher(0));
        editText2.addTextChangedListener(new OtpVerifcationActivity.PinTextWatcher(1));
        editText3.addTextChangedListener(new OtpVerifcationActivity.PinTextWatcher(2));
        editText4.addTextChangedListener(new OtpVerifcationActivity.PinTextWatcher(3));
        editText5.addTextChangedListener(new OtpVerifcationActivity.PinTextWatcher(4));
        editText6.addTextChangedListener(new OtpVerifcationActivity.PinTextWatcher(5));

        editText1.setOnKeyListener(new OtpVerifcationActivity.PinOnKeyListener(0));
        editText2.setOnKeyListener(new OtpVerifcationActivity.PinOnKeyListener(1));
        editText3.setOnKeyListener(new OtpVerifcationActivity.PinOnKeyListener(2));
        editText4.setOnKeyListener(new OtpVerifcationActivity.PinOnKeyListener(3));
        editText5.setOnKeyListener(new OtpVerifcationActivity.PinOnKeyListener(4));
        editText6.setOnKeyListener(new OtpVerifcationActivity.PinOnKeyListener(5));


    }

    private void getOTP()
    {
        LoadingBar.setMessage("Connecting");
        LoadingBar.setCanceledOnTouchOutside(false);
        LoadingBar.show();

        PhoneAuthOptions options =
                PhoneAuthOptions
                        .newBuilder(mAuth)
                        .setPhoneNumber("+"+phonenumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(OtpVerifcationActivity.this)                 // Activity (for callback binding)
                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }


    public class PinTextWatcher implements TextWatcher
    {

        private int currentIndex;
        private boolean isFirst = false, isLast = false;
        private String newTypedString = "";

        PinTextWatcher(int currentIndex)
        {
            this.currentIndex = currentIndex;

            if (currentIndex == 0)
                this.isFirst = true;
            else if (currentIndex == editTexts.length - 1)
                this.isLast = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            newTypedString = s.subSequence(start, start + count).toString().trim();
        }


        @Override
        public void afterTextChanged(Editable s)
        {

            String text = newTypedString;

            /* Detect paste event and set first char */
            if (text.length() > 1)
                text = String.valueOf(text.charAt(0)); // TODO: We can fill out other EditTexts

            editTexts[currentIndex].removeTextChangedListener(this);
            editTexts[currentIndex].setText(text);
            editTexts[currentIndex].setSelection(text.length());
            editTexts[currentIndex].addTextChangedListener(this);

            if (text.length() == 1)
                moveToNext();
            else if (text.length() == 0)
                moveToPrevious();
        }

        private void moveToNext()
        {
            if (!isLast)
                editTexts[currentIndex + 1].requestFocus();

            if (isAllEditTextsFilled() && isLast)
            {
                //  isLast is optional

                String newOtp="";

                for(EditText editText : editTexts)
                        {
                            String otp = editText.getText().toString();
                            newOtp = newOtp.concat(otp);

                        }

                verifyOtp(newOtp);


                editTexts[currentIndex].clearFocus();
                hideKeyboard();

            }
        }

        private void moveToPrevious()
        {
            if (!isFirst)
                editTexts[currentIndex - 1].requestFocus();
        }


        private void hideKeyboard()
        {
            if (getCurrentFocus() != null)
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }

    }


    private boolean isAllEditTextsFilled()
    {
        for (EditText editText : editTexts)
            if (editText.getText().toString().trim().length() == 0)
                return false;
        return true;
    }

    public class PinOnKeyListener implements View.OnKeyListener
    {

        private int currentIndex;

        PinOnKeyListener(int currentIndex)
        {
            this.currentIndex = currentIndex;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event)
        {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN)
            {
                if (editTexts[currentIndex].getText().toString().isEmpty() && currentIndex != 0)
                    editTexts[currentIndex - 1].requestFocus();
            }
            return false;
        }

    }

    private void verifyOtp(String newOtp)
    {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,newOtp);
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                          .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                          {
                              @Override
                              public void onComplete(@NonNull Task<AuthResult> task)
                              {
                                  if(task.isSuccessful())
                                  {
                                      // save the phone number in database

                                      String rphone=phonenumber.substring(2,12);

                                      savePhone(rphone);

                                      Intent intent= new Intent(OtpVerifcationActivity.this,ProfileInfoActivity.class);
                                      intent.putExtra("phone",rphone);
                                      startActivity(intent);
                                      startActivity(intent);
                                      finish();

                                      //save the number here to paper db for auto auto login
                                      Paper.book().write(Prevalent.PhoneNumber,rphone);

                                  }
                                  else
                                  {
                                      String message =task.getException().toString();
                                      Toast.makeText(OtpVerifcationActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                                  }

                              }
                          });


    }

    private void savePhone(String rphone)
    {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("ring");

        HashMap<String, Object> NewuserMap = new HashMap<>();
        NewuserMap.put("phone",rphone);
        ref.child("allusers").child(rphone).updateChildren(NewuserMap);

    }


}