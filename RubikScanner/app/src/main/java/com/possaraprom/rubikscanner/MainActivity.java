package com.possaraprom.rubikscanner;

import static java.lang.Math.ceil;

import android.opengl.GLSurfaceView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    String currentPhotoPath;
    Uri photoURI;
    Button button;
    Button button2;
    View progressBar;

    private ArrayList<PhotoCube> photoCubesList = new ArrayList<>();
    private boolean readyToExit=false;
    private Date backPressedTime;
    private GLSurfaceView surfaceView;
    private final byte[][][] orientations = new byte[6][4][3];
    private final byte[][] rubik = new byte[6][8];
    private final int[][] direction = new int[][]{
            {1, 3, 4, 2},
            {3, 0, 2, 5},
            {1, 0, 4, 5},
            {4, 0, 1, 5},
            {2, 0, 3, 5},
            {1, 2, 4, 3}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surfaceView);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        progressBar = findViewById(R.id.progressBar);
        surfaceView.setVisibility(View.INVISIBLE);

        button2.setOnClickListener((View v) -> {
            Log.v("BUTTON", "Button Triggered");
            pickImageFromGallery(Intent.ACTION_GET_CONTENT, 2);
            progressBar.setVisibility(View.VISIBLE);
            button.setVisibility(View.INVISIBLE);
            button2.setVisibility(View.INVISIBLE);
        });
        button.setOnClickListener((View v) -> {
            Log.v("BUTTON", "Button Triggered");
            dispatchTakePictureIntent(MediaStore.ACTION_IMAGE_CAPTURE);
            progressBar.setVisibility(View.VISIBLE);
            button.setVisibility(View.INVISIBLE);
            button2.setVisibility(View.INVISIBLE);
        });
    }
    @Override
    public void onBackPressed(){
        if(button.isShown()){
            if(readyToExit&&(new Date()).getTime()-backPressedTime.getTime()<3000){
                this.finishAffinity();
                return;
            }
            backPressedTime = new Date();
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            readyToExit = true;
            return;
        }
        surfaceView.setVisibility(View.GONE);
        progressBar.setVisibility(View.INVISIBLE);
        button.setVisibility(View.VISIBLE);
        button2.setVisibility(View.VISIBLE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if(resultCode!=RESULT_OK){
                onBackPressed();
                return;
            }

            //ImageView imgView = findViewById(R.id.img);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(),
                    (photoURI != null) ? photoURI : data.getData());
            File photoFile = new File(photoURI.getPath());
            //After fetching image processing
            photoFile.delete();
            /*if(bitmap.getWidth()>10 || bitmap.getHeight()>10){
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
            float m;*/
            {
                int side = 3;
                byte[][] orientaion = {
                        {4, 4, 4},
                        {0, 0, 0},
                        {1, 1, 1},
                        {5, 5, 5}
                };
                orientations[side] = orientaion;
                side = 0;
                orientaion = new byte[][]{
                        {1, 1, 1},
                        {3, 3, 3},
                        {4, 4, 4},
                        {2, 2, 2}
                };
                orientations[side] = orientaion;
                side = 4;
                orientaion = new byte[][]{
                        {2, 2, 2},
                        {0, 0, 0},
                        {3, 3, 3},
                        {5, 5, 5}
                };
                orientations[side] = orientaion;
                side = 2;
                orientaion = new byte[][]{
                        {1, 1, 1},
                        {0, 0, 0},
                        {4, 4, 4},
                        {5, 5, 5}
                };
                orientations[side] = orientaion;
                side = 5;
                orientaion = new byte[][]{
                        {1, 1, 1},
                        {2, 2, 2},
                        {4, 4, 4},
                        {3, 3, 3}
                };
                orientations[side] = orientaion;
                side = 1;
                orientaion = new byte[][]{
                        {3, 3, 3},
                        {0, 0, 0},
                        {2, 2, 2},
                        {5, 5, 5}
                };
                orientations[side] = orientaion;
            }
            int[] sides = new int[]{0, 1, 2, 3, 4, 5};
            for (int side : sides) {
                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 2; j++) {
                        int n = findIndex(direction[direction[side][i]], side);
                        rubik[side][2 * i + j] = orientations[direction[side][i]][n][j];
                    }
                }
                Log.i(side+"", Arrays.toString(rubik[side]));
            }
            draw(0);

            /*PhotoCube mPhotoCube = new PhotoCube(
                    0.5f,
                    0.5f,
                    0.7f,
                    -1,
                    1,
                    1
            );
            MyGLRenderer.add(mPhotoCube);*/
            surfaceView.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            Log.e("bitmap", "error" + e);
        }
    }

    private void draw(int side){
        float axisWidth = 0.9f;
        int[][] lists={
                {0,1,0}, //Y
                {1,0,0}, //X
                {0,0,1}, //Z
                {0,-1,0}, //-Y
                {-1,0,0}, //-X
                {0,0,-1} //-Z
        };

        //for(int i=0;i<4;i++) {
            int i=0;
            int currentSide=direction[side][i];
            int n = findIndex(direction[currentSide], side);
            for (int j = 0; j < 2; j++) {
                Log.i("GGrubik",rubik[(currentSide-1)%4][(2*n)%8]+"");
                Log.i("GG",(2*n+1+j)%8+"");
                Log.i("GGGG",""+rubik[currentSide][(2*(n+1) - j)%8]);
                Log.i("GGw",""+(rubik[currentSide][(2*(n+1) - j)%8]*lists[side][0]+((rubik[(currentSide-1)%4][(2*n)%8]-axisWidth)*j+axisWidth)*lists[(side-1+4)%4][0]));
                Log.i("GGh",""+(rubik[currentSide][(2*(n+1) - j)%8]*lists[side][1]+((rubik[(currentSide-1)%4][(2*n)%8]-axisWidth)*j+axisWidth)*lists[(side-1+4)%4][1]));
                PhotoCube mPhotoCube = new PhotoCube(
                        (rubik[currentSide][(2*(n+1) - j)%8]*lists[side][0]+((rubik[(currentSide-1)%4][(2*n)%8]-axisWidth)*j+axisWidth)*lists[(side-1+4)%4][0])*0.3f+axisWidth,
                        (rubik[currentSide][(2*(n+1) - j)%8]*lists[side][1]+((rubik[(currentSide-1)%4][(2*n)%8]-axisWidth)*j+axisWidth)*lists[(side-1+4)%4][1])*0.3f+axisWidth,
                        (rubik[currentSide][(2*(n+1) - j)%8]*lists[side][2]+((rubik[(currentSide-1)%4][(2*n)%8]-axisWidth)*j+axisWidth)*lists[(side-1+4)%4][2])*0.3f+axisWidth,
                        (float) ((1-j)+lists[side][0]-(1-j)*Math.pow(lists[side][0],2)), //0-> 1-j 1->1 -1->-1
                        (float) ((1-j)+lists[side][1]-(1-j)*Math.pow(lists[side][1],2)),
                        (float) ((1-j)+lists[side][2]-(1-j)*Math.pow(lists[side][2],2))
                );
                MyGLRenderer.add(mPhotoCube);
            }
        //}
        //surfaceView.requestRender();
    }

    private int findIndex(int[] list, int value) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == value) {
                return i;
            }
        }
        return 5;
    }

    private File createImageFile() throws IOException {
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

    private void pickImageFromGallery(String intent, Integer activityCode) {
        Intent takePictureIntent = new Intent();
        takePictureIntent.setType("image/*");
        takePictureIntent.setAction(intent);
        takePictureIntent = Intent.createChooser(takePictureIntent, "Select Picture");
        startActivityForResult(takePictureIntent, activityCode);
    }

    private void dispatchTakePictureIntent(String intent) {
        Intent takePictureIntent = new Intent(intent);
        // Ensure that there's a camera activity to handle the intent
        Log.v("PICTURE", "Dispatch Ran");

        //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        //Log.v("PICTURE Package", "Package's Available");
        File photoFile = null;
        // Create the File where the photo should go
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            Log.e("PICTURE Error", "Error" + ex);
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            photoURI = FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    photoFile);
            Log.i("PICTURE URI", photoURI.toString());
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, 1); //activity code = 1
        }
        //}
    }
}