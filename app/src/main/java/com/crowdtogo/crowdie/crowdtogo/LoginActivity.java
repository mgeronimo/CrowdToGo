package com.crowdtogo.crowdie.crowdtogo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.crowdtogo.crowdie.model.AccessTokenResponse;
import com.crowdtogo.crowdie.model.Token;
import com.crowdtogo.crowdie.model.UserLoginResponse;
import com.crowdtogo.crowdie.model.UsersResponse;
import com.crowdtogo.crowdie.network.requests.AccessTokenRequest;
import com.crowdtogo.crowdie.network.requests.UserRequest;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import static com.crowdtogo.crowdie.model.UserLoginResponse.*;


public class LoginActivity extends BaseSpiceActivity {

    EditText emailAddress;
    EditText password;
    EditText oAuth;
    Button btnLogin;
    Button btnForgotPass;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        changeFonts();//change ui fonts

        emailAddress.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        oAuth.addTextChangedListener(textWatcher);

        //login button action
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                    // Start NewActivity.class

                Token token = new Token();
                token.setUsername(emailAddress.getText().toString());
                token.setPassword(password.getText().toString());
                token.setClient_secret(oAuth.getText().toString());//"04Ifd"
                token.setClient_id(emailAddress.getText().toString());
                token.setGrant_type("password");
                token.setScope("Crowdie");
                mProgressDialog = new ProgressDialog(LoginActivity.this);
                // Set progressdialog title
                mProgressDialog.setTitle("CrowdToGo");
                // Set progressdialog message
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.setIndeterminate(false);
                // Show progressdialog
                mProgressDialog.show();
                //perform login by providing valid parameters to get access token
                getAccessTokenSpiceManager().execute(new AccessTokenRequest(token), "getAccessToken", DurationInMillis.ALWAYS_EXPIRED, new AccessRequestListener());

            }
        });

        //forgot password action
        btnForgotPass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Toast.makeText(getApplicationContext(),
                        "Forgot Password Clicked", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void changeFonts(){

        Typeface LatoRegular = Typeface.createFromAsset(getAssets(),
                "fonts/Lato-Regular.ttf");

        Typeface LatoBold = Typeface.createFromAsset(getAssets(),
                "fonts/Lato-Bold.ttf");

        //declared forms
        emailAddress = (EditText)findViewById(R.id.emailEditText);
        password = (EditText)findViewById(R.id.passwordEditText);
        oAuth = (EditText)findViewById(R.id.oauthEditText);
        btnLogin = (Button) findViewById(R.id.loginButton);
        btnForgotPass = (Button) findViewById(R.id.forgotPasswordButton);

        //set custom font to forms
        emailAddress.setTypeface(LatoRegular);
        password.setTypeface(LatoRegular);
        oAuth.setTypeface(LatoRegular);
        btnLogin.setTypeface(LatoBold);
        btnForgotPass.setTypeface(LatoRegular);

    }

    //TextWatcher
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void checkFieldsForEmptyValues(){

            final String emailAdd = emailAddress.getText().toString();
            final String passWord = password.getText().toString();
            final String oAuthKey = oAuth.getText().toString();

            //if fields are empty
            if(emailAdd.equals("") && passWord.equals("") && oAuthKey.equals(""))
            {
                btnLogin.setEnabled(false);
            }

            else if(!emailAdd.equals("") && passWord.equals("") && oAuthKey.equals(""))
            {
                btnLogin.setEnabled(false);
            }

            else if(!passWord.equals("") && oAuthKey.equals("") && emailAdd.equals(""))
            {
                btnLogin.setEnabled(false);
            }

            else if(!oAuthKey.equals("") && emailAdd.equals("") && passWord.equals(""))
            {
                btnLogin.setEnabled(false);
            }

            else if(!emailAdd.equals("") && !passWord.equals("") && oAuthKey.equals(""))
            {
                btnLogin.setEnabled(false);
            }

            else if(!passWord.equals("") && !oAuthKey.equals("") && emailAdd.equals(""))
            {
                btnLogin.setEnabled(false);
            }

            else if(!oAuthKey.equals("") && !emailAdd.equals("") && passWord.equals(""))
            {
                btnLogin.setEnabled(false);
            }

            else
            {
                btnLogin.setEnabled(true);

            }
        }

    //Keypress event to prevent app to back from previous acivity
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
}

    //Request listener
    private class AccessRequestListener implements RequestListener<UserLoginResponse> {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(LoginActivity.this, "Failed:Incorrect username or password "+ spiceException.toString(), Toast.LENGTH_LONG).show();
        }
        @Override
        public void onRequestSuccess(UserLoginResponse userLoginResponse) {
            updateScreen(userLoginResponse);
        }
    }

    private void updateScreen(final UserLoginResponse response){
        if(response!=null){
            Toast.makeText(LoginActivity.this, "Access Token: "+ response.getAccess_token(), Toast.LENGTH_LONG).show();
            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainIntent);
        }

    }
}
