package com.example.streckograph;





import android.content.Context;
import android.opengl.GLSurfaceView;

public class GlSurfaceV extends GLSurfaceView {


float[] vertices;

private final GLRenderer mRenderer;
	
	public GlSurfaceV(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
       
        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new GLRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mRenderer.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mRenderer.onResume();
	}
	
	
	

}
