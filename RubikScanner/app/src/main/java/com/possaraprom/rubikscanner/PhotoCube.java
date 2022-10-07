package com.possaraprom.rubikscanner;

import static android.opengl.GLES20.GL_UNSIGNED_SHORT;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class PhotoCube{
    public ArrayList<FloatBuffer> vertexBufferList = new ArrayList<>();
    public int[][] lists = {
            {0,1,0}, //Y
            {0,0,1}, //X
            {0,1,0}
    };

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;

    static final float[][] colorList = {
            {1f,0f,0f,1f},
            {0f,1f,0f,1f},
            {1f,1f,1f,1f},
            {1f,1f,0f,1f},
            {1f,0f,1f,1f},
            {0f,1f,1f,1f}
    };

    public int mProgram;
    private final short[] drawOrder = {0, 1, 2, 0, 2, 3};
    private ShortBuffer drawListBuffer;

    public PhotoCube(float w, float h, float d, float X, float Y, float Z) {
        float[][] coords = new float[][]{   // in counterclockwise order:
                {// X
                    w / 2, h / 2, d / 2, // top right
                    w / 2, h / 2, -d / 2, // top left
                    w / 2, -h / 2, -d / 2, // bottom left
                    w / 2, -h / 2, d / 2  // bottom right
                },
                {// Y
                    w / 2, h / 2, d / 2, // top right
                    -w / 2, h / 2, d / 2, // top left
                    -w / 2, h / 2, -d / 2, // bottom left
                    w / 2, h / 2, -d / 2  // bottom right
                },
                {// Z
                    w / 2, h / 2, d / 2, // top right
                    -w / 2, h / 2, d / 2, // top left
                    -w / 2, -h / 2, d / 2, // bottom left
                    w / 2, -h / 2, d / 2  // bottom right
                }
        };

        for(int j=0;j<3;j++){
            float[] currentCoords = coords[j].clone();
            int[] list = lists[j];
            for(int k=0;k<2;k++) {
                for (int i = 0; i < 12; i += 3) {
                    float x = coords[j][i];
                    float y = coords[j][i + 1];
                    float z = coords[j][i + 2];
                    double angle = Math.toRadians(k*180);
                    currentCoords[i] = (float) ((list[0] == 1) ? x : (list[1] == 1) ? x * Math.cos(angle) + z * Math.sin(angle) : x * Math.cos(angle) - y * Math.sin(angle))+X*w/2;
                    currentCoords[i + 1] = (float) ((list[0] == 1) ? y * Math.cos(angle) - z * Math.sin(angle) : (list[1] == 1) ? y : x * Math.sin(angle) + y * Math.cos(angle))+Y*h/2;
                    currentCoords[i + 2] = (float) ((list[0] == 1) ? z * Math.cos(angle) + y * Math.sin(angle) : (list[1] == 1) ? z * Math.cos(angle) - x * Math.sin(angle) : z)+Z*d/2;
                }

                ByteBuffer bb = ByteBuffer.allocateDirect(
                        // (number of coordinate values * 4 bytes per float)
                        currentCoords.length * 4);
                // use the device hardware's native byte order
                bb.order(ByteOrder.nativeOrder());
                // create a floating point buffer from the ByteBuffer
                FloatBuffer vertexBuffer = bb.asFloatBuffer();
                // add the coordinates to the FloatBuffer
                vertexBuffer.put(currentCoords);
                // set the buffer to read the first coordinate
                vertexBuffer.position(0);

                vertexBufferList.add(vertexBuffer);
                ByteBuffer dlb = ByteBuffer.allocateDirect(
                        // (# of coordinate values * 2 bytes per short)
                        drawOrder.length * 2);
                dlb.order(ByteOrder.nativeOrder());
                drawListBuffer = dlb.asShortBuffer();
                drawListBuffer.put(drawOrder);
                drawListBuffer.position(0);
                // creates OpenGL ES program executables
            }
        }
    }

    public void draw(int order) {
        mProgram=MyGLRenderer.program;
        final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, true,
                vertexStride, vertexBufferList.get(order));

        // get handle to fragment shader's vColor member
        int colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, colorList[order], 0);

        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                drawOrder.length,
                GL_UNSIGNED_SHORT,
                drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
