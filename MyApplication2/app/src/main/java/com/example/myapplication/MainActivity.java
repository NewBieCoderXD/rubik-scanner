package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static String readFromWeb(String webURL) throws IOException {
        URL url = new URL(webURL);
        InputStream is =  url.openStream();
        try( BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                return (line);
            }
        }
        catch (MalformedURLException e) {
            return "error";
        }
        catch (IOException e) {
            return "error";
        }
        return "error";
    }
    public String TAG="ggg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView T = findViewById(R.id.text);

        Timer timer = new Timer();
        timer.schedule( new TimerTask() {
            public void run() {
                try {
                    String a = readFromWeb("https://test-okhrngngaan.uwugg.repl.co/getdata?token=5555555555");
                    runOnUiThread(() -> T.setText(a));
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        },0,10);
    }
}