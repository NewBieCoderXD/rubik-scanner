package com.possaraprom.rubikscanner;

import static java.lang.Math.sqrt;

import android.app.Activity;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    String currentPhotoPath;
    Uri photoURI = null;
    Button button = null;
    Button button2 = null;
    Context context = MainActivity.this;
    int w;
    int h;

    private File createImageFile() throws IOException{
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void pickImageFromGallery(String intent, Integer activityCode){
        Intent takePictureIntent = new Intent();
        takePictureIntent.setType("image/*");
        takePictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        takePictureIntent = Intent.createChooser(takePictureIntent, "Select Picture");
        startActivityForResult(takePictureIntent, activityCode);
    }

    private void dispatchTakePictureIntent(String intent, Integer activityCode) {
        Intent takePictureIntent = new Intent(intent);
        // Ensure that there's a camera activity to handle the intent
        Log.v("PICTURE","Dispatch Ran");
        //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            Log.v("PICTURE","Package's Available");
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("PICTURE","Error"+ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                Log.i("PICTURE",photoURI.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, activityCode);
            }
        //}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button2.setOnClickListener((View v) -> {
            Log.v("BUTTON","Button Triggered");
            pickImageFromGallery(Intent.ACTION_GET_CONTENT, 2);
            View progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            button.setVisibility(View.INVISIBLE);
            button2.setVisibility(View.INVISIBLE);
        });
        button.setOnClickListener((View v) -> {
            Log.v("BUTTON","Button Triggered");
            dispatchTakePictureIntent(MediaStore.ACTION_IMAGE_CAPTURE,  1);
            View progressBar = findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            button.setVisibility(View.INVISIBLE);
            button2.setVisibility(View.INVISIBLE);
        });
    }

    protected double colorDistance(String hex1, String hex2){
        int dec1 = Color.parseColor(hex1);
        int dec2 = Color.parseColor(hex2);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            float meanR = (Color.red(dec1) + Color.red(dec2))/2/255;
            float dR = Color.red(dec1) - Color.red(dec2);
            float dG = Color.green(dec1) - Color.green(dec2);
            float dB = Color.blue(dec1) - Color.blue(dec2);
            return sqrt((2 + meanR) * dR * dR + dG * dG + (3 - meanR) * dB * dB)/sqrt(255);
        }
        return 0;
    }

    protected Bitmap createLine(Bitmap bitmap, int x1, int y1, int x2, int y2){
        if(x1==x2){
            for(int j=y1;j<y2;j++){
                bitmap.setPixel(x1,j,Color.GREEN);
            }
            return bitmap;
        }
        for(int i=x1;i<x2;i++){
            bitmap.setPixel(i,(i-x1)*(y2-y1)/(x2-x1)+y1,Color.GREEN);
        }
        return bitmap;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            ImageView imgView = findViewById(R.id.img);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                    (photoURI!=null)?photoURI:data.getData());
            int scale=1;
            if(bitmap.getWidth()>10 || bitmap.getHeight()>10){
                scale=10;
            }
            int sumX = 0;
            int sumY = 0;
            int numberOfPixels = 0;
            w = bitmap.getWidth()/scale;
            h = bitmap.getHeight()/scale;
            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, false);
            Log.i("dimension: ",w+","+h);
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap render = Bitmap.createBitmap(w,h,conf);
            render.setHasAlpha(true);
            for(int i=0;i<w;i++){
                for(int j=0;j<h;j++) {
                    String colorCode = String.format("#%06x", (0xffffffff & bitmap.getPixel(i,j)));
                    //Log.i("distance",Double.toString(colorDistance(colorCode,"#ffb7c4d7")));
                    Double.toString(colorDistance(colorCode,"#ffb7c4d7"));
                    if(colorDistance(colorCode,"#ffb7c4d7") < 10){
                        numberOfPixels++;
                        sumX+=i;
                        sumY+=j;
                        render.setPixel(i, j, 0xffffffff);
                        continue;
                    }
                    render.setPixel(i, j, 0xff000000);
                }
            }
            final int avgX = sumX/numberOfPixels;
            final int avgY = sumY/numberOfPixels;
            Log.i("average: ",sumX/numberOfPixels+" "+sumY/numberOfPixels);
            render = createLine(render,0,avgY,w,avgY);
            render = createLine(render,avgX,0,avgX,h);
            float m;
        }
        catch (NullPointerException|IOException e){
            Log.e("bitmap","error"+e);
        }
    }
}