package com.example.android.traveldeal;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by b1rd on 15.04.16.
 */
public class SignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        View backgroundImage = findViewById(R.id.signup_screen);
        Drawable background = backgroundImage.getBackground();
        background.setAlpha(100);
    }
    public void buttonSignedUp(View v){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
