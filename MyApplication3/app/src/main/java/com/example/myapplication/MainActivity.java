package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.lang.reflect.Method;
import java.lang.NoSuchMethodException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;


public class MainActivity extends AppCompatActivity {
    public final String STAGE = "stage";
    public int stage=0;
    @FunctionalInterface
    public interface Method{
        void execute();
    }
    public void function1(){
        Button button = findViewById(R.id.button1);
        button.setText("Now is 2nd stage");
    }
    public void function2(){
        Button button = findViewById(R.id.button1);
        button.setText("Now is 3rd stage naja");
    }
    private Method[] methods = {
            this::function1,
            this::function2
    };
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(STAGE,stage);
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        if (savedInstanceState!=null){
            stage = savedInstanceState.getInt(STAGE);
            methods[stage-1].execute();
        }
        TextView T = findViewById(R.id.main_text);
        Button button = findViewById(R.id.button1);
        button.setOnClickListener(view -> {
            if (savedInstanceState==null){
                methods[stage].execute();
                stage += 1;
            }
            else{
                stage = savedInstanceState.getInt(STAGE);
                methods[stage].execute();
                stage += 1;
            }
        });
    }
}
