package com.example.streck_o_graph;





import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GLSurfaceV extends GLSurfaceView {



private final GLRenderer mRenderer;
	
	public GLSurfaceV(Context context) {
        super(context);

        //OpenGl 2.0 Context erstellen
        setEGLContextClientVersion(2);
       
        // Renderer zum zeichnen erstellen
        mRenderer = new GLRenderer(context);
        setRenderer(mRenderer);

        // Nur zeichnen wenn sich die Daten geändert haben
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


	// Touch Event erstellen
	public boolean onTouchEvent(MotionEvent event) {
    	return mRenderer.onTouchEvent(event);
    }
	

}
