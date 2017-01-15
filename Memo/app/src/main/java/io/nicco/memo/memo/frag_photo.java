package io.nicco.memo.memo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class frag_photo extends Fragment {

    /* VARS */
    Context c;
    NotePhoto np;
    View shutter;
    TextureView tv;


    /* CAMERA VARS */
    Surface surfacePreview;
    Surface surfaceJPEG;
    CameraDevice cam;
    CameraCaptureSession camSession;
    TotalCaptureResult res;
    Handler handler;
    HandlerThread handlerThread;

    /* PERMISSION */
    boolean granted = false;

    private void cameraOpen() {
        try {
            List<Surface> surfaces = Arrays.asList(surfacePreview, surfaceJPEG);
            cam.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    camSession = session;
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                }
            }, handler);

            CaptureRequest.Builder request = cam.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            request.addTarget(surfacePreview);

            camSession.setRepeatingRequest(request.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    res = result;
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void cameraIni() {
        try {
            CameraManager cm = (CameraManager) c.getSystemService(Context.CAMERA_SERVICE);
            for (String tmp : cm.getCameraIdList()) {
                Log.i("Cams", tmp);
            }
            String cameraId = cm.getCameraIdList()[0];
            CameraCharacteristics cc = cm.getCameraCharacteristics(cameraId);
            StreamConfigurationMap streamConfigs = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert streamConfigs != null;
            Size[] jpegSizes = streamConfigs.getOutputSizes(ImageFormat.JPEG);

            ImageReader jpegImageReader = ImageReader.newInstance(jpegSizes[0].getWidth(), jpegSizes[0].getHeight(), ImageFormat.JPEG, 1);
            jpegImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {

                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Log.i(TAG, "Image taken");
                    //np.img = imageReader.acquireLatestImage();
                }
            }, handler);

            surfaceJPEG = jpegImageReader.getSurface();

            if (ActivityCompat.checkSelfPermission(c, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cm.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cam = camera;
                    cameraOpen();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice cameraDevice) {

                }

                @Override
                public void onError(@NonNull CameraDevice cameraDevice, int i) {

                }
            }, handler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handlerThread = new HandlerThread("Camera");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        cameraIni();
    }

    @Override
    public void onPause() {
        super.onPause();
        handlerThread.quitSafely();
        handlerThread = null;
        handler = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_photo, container, false);

        c = getContext();

        np = new NotePhoto(c);

        ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.CAMERA}, Main.PERMISSION_REQUEST);

        tv = (TextureView) v.findViewById(R.id.frag_photo_preview_field);
        tv.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                surfacePreview = new Surface(surfaceTexture);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });

        shutter = v.findViewById(R.id.frag_photo_shutter_btn);
        shutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Main.PERMISSION_REQUEST:
                granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.i("PERMISSION", String.valueOf(granted));
                break;
        }
    }
}
