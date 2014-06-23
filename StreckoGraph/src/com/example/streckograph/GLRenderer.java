package com.example.streckograph;






import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;


public class GLRenderer implements Renderer {

	// Matritzen
	private final float[] mtrxProjection = new float[16];
	private final float[] mtrxView = new float[16];
	private final float[] mtrxProjectionAndView = new float[16];
	
	// Variablen
		
	
	int y = 0;
	float[] pathArray;
	public short[] indices;
	public FloatBuffer vertexBuffer;
	public ShortBuffer drawListBuffer;
	
	//Bildschirmaufl�sung
	float	mScreenWidth = 1280;
	float	mScreenHeight = 768;

	
	Context mContext;
	long mLastTime;
	int mProgram;
	float i;
	
	
	public GLRenderer(Context c)
	{
		mContext = c;
		mLastTime = System.currentTimeMillis() + 100;
		
	}
	
	public void onPause()
	{
		
	}
	
	public void onResume()
	{
		
		
	}
	
	@Override
	public void onDrawFrame(GL10 unused) {
		
		// zu zeichnende Punkte aktualisieren
    	SetupPath();
		// Render our example
		Render(mtrxProjectionAndView);
				
		
	}
	
	private void Render(float[] m) {
		
		// Buffer clearen 
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        
        // get handle to vertex shader's vPosition member
	    int mPositionHandle = GLES20.glGetAttribLocation(GLShader.sp_SolidColor, "vPosition");
	    
	    // Enable generic vertex attribute array
	    GLES20.glEnableVertexAttribArray(mPositionHandle);

	    // Prepare the triangle coordinate data
	    GLES20.glVertexAttribPointer(mPositionHandle, 3,GLES20.GL_FLOAT, false,0, vertexBuffer);
	    
	    // Get handle to shape's transformation matrix
        int mtrxhandle = GLES20.glGetUniformLocation(GLShader.sp_SolidColor, "uMVPMatrix");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mtrxhandle, 1, false, m, 0);
        GLES20.glLineWidth(5.0f);
       
        // Den Pfad zeichnen
        GLES20.glDrawElements(GLES20.GL_LINE_STRIP, indices.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
        
        
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        	
	}
	

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		
		// We need to know the current width and height.
		mScreenWidth = width;
		mScreenHeight = height;
		
		// Redo the Viewport, making it fullscreen.
		GLES20.glViewport(0, 0, (int)mScreenWidth, (int)mScreenHeight);
		
		// Clear our matrices
	    for(int i=0;i<16;i++)
	    {
	    	mtrxProjection[i] = 0.0f;
	    	mtrxView[i] = 0.0f;
	    	mtrxProjectionAndView[i] = 0.0f;
	    }
	    
	    // Setup our screen width and height for normal sprite translation.
	    Matrix.orthoM(mtrxProjection, 0, 0f, mScreenWidth, 0.0f, mScreenHeight, 0, 50);
	    
	    // Set the camera position (View matrix)
        Matrix.setLookAtM(mtrxView, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0.5f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mtrxProjectionAndView, 0, mtrxProjection, 0, mtrxView, 0);

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
		// Pfad erstellen
		SetupPath();
		
		// Set the clear color to black
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1);	

	    // Create the shaders
	    int vertexShader = GLShader.loadShader(GLES20.GL_VERTEX_SHADER, GLShader.vs_SolidColor);
	    int fragmentShader = GLShader.loadShader(GLES20.GL_FRAGMENT_SHADER, GLShader.fs_SolidColor);

	    GLShader.sp_SolidColor = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	    GLES20.glAttachShader(GLShader.sp_SolidColor, vertexShader);   // add the vertex shader to program
	    GLES20.glAttachShader(GLShader.sp_SolidColor, fragmentShader); // add the fragment shader to program
	    GLES20.glLinkProgram(GLShader.sp_SolidColor);                  // creates OpenGL ES program executables
	    
	    // Set our shader programm
		GLES20.glUseProgram(GLShader.sp_SolidColor);
	}
	
	
	
	public void SetupPath()
	{
		
		
		pathArray = LocationGPS.getVertices();
		indices = LocationGPS.getIndices();
		
		
		// The vertex buffer.
		ByteBuffer bb = ByteBuffer.allocateDirect(pathArray.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(pathArray);
		vertexBuffer.position(0);
		
		// initialize byte buffer for the draw list
		ByteBuffer dlb = ByteBuffer.allocateDirect(indices.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(indices);
		drawListBuffer.position(0);
		
		
	}
}
