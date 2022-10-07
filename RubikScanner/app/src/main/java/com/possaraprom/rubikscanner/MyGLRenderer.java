package com.possaraprom.rubikscanner;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.provider.ContactsContract;
import android.util.Log;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    //private ArrayList<PhotoCube> photoCubesList;
    private static ArrayList<PhotoCube> PhotoCubes = new ArrayList<>();
    public final float[] vPMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    public static int program;
    private int vPMatrixHandle = -1;
    private volatile float mAngleX = 0;
    private volatile float mAngleY = 0;
    private float[] rotationMX = new float[16];
    private float[] rotationMY = new float[16];
    private float[] scratch = new float[16];

    public MyGLRenderer(Context context){
    }

    public static void createProgram(){
        final String vertexShaderCode =
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 vPosition;" +
                        "void main() {" +
                        // the matrix must be included as a modifier of gl_Position
                        // Note that the uMVPMatrix factor *must be first* in order
                        // for the matrix multiplication product to be correct.
                        "  gl_Position = uMVPMatrix * vPosition;" +
                        "}";
        final String fragmentShaderCode =
                "precision mediump float;" +
                        "uniform vec4 vColor;" +
                        "void main() {" +
                        "  gl_FragColor = vColor;" +
                        "}";
        // create empty OpenGL ES Program
        program = GLES20.glCreateProgram();
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // add the vertex shader to program
        GLES20.glAttachShader(program, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(program, fragmentShader);
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public static void add(PhotoCube element){
        PhotoCubes.add(element);
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        createProgram();
        GLES20.glLinkProgram(program);
        //mPhotoCube = new PhotoCube(1f,1.5f,1.5f, 0,0,0);
        vPMatrixHandle = GLES20.glGetUniformLocation(program, "uVPMatrix");
        onDrawFrame(unused);
    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        //GLES20.glEnable(GLES20.GL_BLEND);
        //GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ZERO, GLES20.GL_ONE);

        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 5, 0f, 0f, -5f, 0f, 1.0f, 0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.setRotateM(rotationMX, 0, -mAngleX, vPMatrix[1], vPMatrix[5], vPMatrix[9]);
        Matrix.multiplyMM(scratch, 0, vPMatrix, 0, rotationMX, 0);
        Matrix.setRotateM(rotationMY, 0, mAngleY, -scratch[0], -scratch[4], -scratch[8]);
        Matrix.multiplyMM(scratch, 0, scratch, 0, rotationMY, 0);

        vPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, scratch, 0);
        if(PhotoCubes.size()==0){
            Log.i("gg","gggdesfs");
            return;
        }

        for(int i=0;i<PhotoCubes.size();i++) {
            for (int j = 0; j < 6; j++) {
                PhotoCubes.get(i).draw(j);
            }
        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public float getAngleX() {
        return mAngleX;
    }

    public float getAngleY() {
        return mAngleY;
    }

    public void setAngleX(float angle) {
        mAngleX = angle;
    }

    public void setAngleY(float angle) {
        mAngleY = angle;
    }
}