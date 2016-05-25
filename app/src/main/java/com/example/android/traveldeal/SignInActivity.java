package com.example.android.traveldeal;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by b1rd on 15.04.16.
 */
public class SignInActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        View backgroundImage = findViewById(R.id.signin_screen);
        Drawable background = backgroundImage.getBackground();
        background.setAlpha(100);
    }
    public void buttonSignIn(View v){
        System.out.println(((EditText) findViewById(R.id.email)).getText().toString());
        System.out.println(((EditText) findViewById(R.id.password)).getText().toString());
        if (!((EditText) findViewById(R.id.email)).getText().toString().equals("notverysmartbird@gmail.com")){
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout,
                    (ViewGroup) findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("User with this email doesn't exist");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 400);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
        else if (((EditText) findViewById(R.id.email)).getText().toString().equals("notverysmartbird@gmail.com")
                && (!((EditText) findViewById(R.id.password)).getText().toString().equals("fancypass"))){
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_layout,
                    (ViewGroup) findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Wrong pass. Try again");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 400);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

//    public void buttonSignUp(View v){
//        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
//        startActivity(intent);
//    }
}
