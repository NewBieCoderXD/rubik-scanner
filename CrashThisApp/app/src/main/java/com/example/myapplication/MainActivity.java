package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    public void func1(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        savedInstanceState.getInt("haha");
    }
    public void func2(){
        int a = 2/0;
    }
    public void func3() throws UserException{
        throw new UserException("Something failed.");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b = findViewById(R.id.button);
        //b.setOnClickListener(view -> func1(savedInstanceState));
        //b.setOnClickListener(view -> func2());
        try {
            b.setOnClickListener(view -> func3());
        }
        catch(UserException e){
        }
    }
}