package io.nicco.memo.memo;

import android.Manifest;
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
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Arrays;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class frag_photo extends Fragment {

    /* VARS */
    Context c;
    NotePhoto np;
    View shutter;
    ImageView btn_change;
    TextureView tv;
    boolean takingPic = false;

    /* CAMERA VARS */
    Surface surfacePreview;
    Surface surfaceJPEG;
    CameraDevice cam;
    CameraCaptureSession camSession;
    CameraCharacteristics cc;
    int curCam = 0;
    int maxCam = 1;

    Handler handler;
    HandlerThread handlerThread;

    final TextureView.SurfaceTextureListener surfacePreviewListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            cameraIni();
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
    };

    void createPreview() {
        try {
            if (camSession == null)
                return;
            CaptureRequest.Builder request = cam.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            request.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            request.addTarget(surfacePreview);
            camSession.setRepeatingRequest(request.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                }
            }, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    void shutter() {
        try {
            if (camSession == null || takingPic)
                return;
            takingPic = true;
            CaptureRequest.Builder request = cam.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            request.addTarget(surfaceJPEG);
            camSession.stopRepeating();
            camSession.capture(request.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                    takingPic = false;
                }
            }, handler);
        } catch (CameraAccessException e) {
            takingPic = false;
            createPreview();
            e.printStackTrace();
        }
    }

    void cameraOpen() {
        try {
            if (cam == null)
                return;

            StreamConfigurationMap streamConfigs = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert streamConfigs != null;
            Size[] jpegSizes = streamConfigs.getOutputSizes(ImageFormat.JPEG);

            final ImageReader jpegImageReader = ImageReader.newInstance(jpegSizes[0].getWidth(), jpegSizes[0].getHeight(), ImageFormat.JPEG, 1);
            jpegImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {

                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    np.img = imageReader.acquireNextImage();
                    np.save();
                    new Utils().toast(getContext(), "Image Saved");
                    imageReader.close();
                    createPreview();
                }
            }, handler);
            surfaceJPEG = jpegImageReader.getSurface();

            surfacePreview = new Surface(tv.getSurfaceTexture());

            cam.createCaptureSession(Arrays.asList(surfacePreview, surfaceJPEG), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    if (cam == null)
                        return;
                    camSession = session;
                    createPreview();
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                }
            }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    void cycleCam() {
        curCam++;
        if (curCam >= maxCam)
            curCam = 0;
        closeCamera();
        cameraIni();
    }

    void cameraIni() {
        try {
            CameraManager cm = (CameraManager) c.getSystemService(Context.CAMERA_SERVICE);
            maxCam = cm.getCameraIdList().length;
            String camId = cm.getCameraIdList()[curCam];
            cc = cm.getCameraCharacteristics(camId);

            if (ActivityCompat.checkSelfPermission(c, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cm.openCamera(camId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cam = camera;
                    cameraOpen();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                    cam.close();
                    cam = null;
                }

                @Override
                public void onError(@NonNull CameraDevice cameraDevice, int i) {
                    cam.close();
                    cam = null;
                }
            }, handler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    void closeCamera() {
        if (null != camSession) {
            camSession.close();
            camSession = null;
        }
        if (null != cam) {
            cam.close();
            cam = null;
        }
    }

    private void startBackgroundThread() {
        handlerThread = new HandlerThread("CameraBackground");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    private void stopBackgroundThread() {
        handlerThread.quitSafely();
        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_photo, container, false);

        c = getContext();
        np = new NotePhoto(c);

        tv = (TextureView) v.findViewById(R.id.frag_photo_preview_field);
        shutter = v.findViewById(R.id.frag_photo_shutter_btn);
        shutter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shutter();
            }
        });
        btn_change = (ImageView) v.findViewById(R.id.frag_photo_change_btn);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cycleCam();
            }
        });

        if (new Utils().getPref(getContext(), Main.PREF_CAM_DEF).equals("true"))
            curCam = 1;

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();
        if (tv.isAvailable())
            cameraIni();
        else
            tv.setSurfaceTextureListener(surfacePreviewListener);
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }
}
