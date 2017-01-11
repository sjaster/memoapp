package io.nicco.memo.memo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class frag_photo extends Fragment {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Integer cam;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_photo, container, false);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(getContext(), mCamera);
        FrameLayout preview = (FrameLayout) v.findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    return v;
    }
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){

            return true;
        } else {
            return false;
        }
    }
    public static Camera getCameraInstance(){
        Camera c = null;
        int cam = Camera.getNumberOfCameras();
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
}
