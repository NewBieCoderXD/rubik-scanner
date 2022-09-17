package com.example.rubikscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        Log.v("PICTURE","Dispatch Runned");
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
                startActivityForResult(takePictureIntent, 1);
            }
        //}
    }

    //protected File imageProcessing() throws IOException{

    //}

    private static Context context;

    private int abs(int N){
        if(N<0){
            return -N;
        }
        return N;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        button.setOnClickListener((View v) -> {
                Log.v("BUTTON","Button Runned");
                dispatchTakePictureIntent();
                View progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                Log.v("bitmap",String.valueOf(photoURI));


        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            ImageView imgView = findViewById(R.id.img);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                    photoURI);
            Log.v("bitmap",String.valueOf(bitmap));
            int w = bitmap.getWidth()/3;
            int h = bitmap.getHeight()/3;
            bitmap = Bitmap.createScaledBitmap(bitmap, w, h, false);
            Log.i("dimension: ",Integer.toString(w)+","+h);
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap render = Bitmap.createBitmap(w,h,conf);
            render.setHasAlpha(true);
            for(int i=0;i<w;i++){
                for(int j=0;j<h;j++) {
                    String colorCode = String.format("%06x", (0xffffff & bitmap.getPixel(i,j)));
                    if (abs(colorCode.compareTo("0xffa1a2a3")) > 38) {
                        render.setPixel(i, j, 0xffffffff);
                        continue;
                    }
                    render.setPixel(i, j, 0xff000000);
                }
            }
            imgView.setImageBitmap(render);
        }
        catch (NullPointerException|IOException e){
            Log.e("bitmap","error"+e);
            return;
        }
    }
}