package com.weisenberger.sascha.mccellid;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class CamHandler {
    TextureView view;
    AppCompatActivity activity;
    public CamHandler(TextureView view, AppCompatActivity activity)
    {
        this.view = view;
        this.activity = activity;
    }

    SurfaceTexture mSurfaceTexture;
    CameraDevice mCamera;
    Surface previewSurface;
    Surface jpegCaptureSurface;
    CameraCaptureSession mSession;

    public void Start()
    {
        view.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                mSurfaceTexture = surface;
                SurfaceAvailable();            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    private void SurfaceAvailable()
    {
        try {
            CameraManager cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            String cameraId = cameraManager.getCameraIdList()[0];
            CameraCharacteristics cc = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap streamConfigs = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] rawSizes = streamConfigs.getOutputSizes(ImageFormat.RAW_SENSOR);
            Size[] jpegSizes = streamConfigs.getOutputSizes(ImageFormat.JPEG);

            ImageReader jpegImageReader = ImageReader.newInstance(jpegSizes[0].getWidth(), jpegSizes[0].getHeight(), ImageFormat.JPEG, 1);
            jpegImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    // save jpeg
                    Log.d("Log","ImageAvailable");
                }
            }, null);

            previewSurface = new Surface(mSurfaceTexture);
            jpegCaptureSurface = jpegImageReader.getSurface();

            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.CAMERA}, 1);
            }

            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    mCamera = camera;
                    CameraAvailable();

                }

                @Override
                public void onDisconnected(CameraDevice camera) {

                }

                @Override
                public void onError(CameraDevice camera, int error) {

                }
            }, null);

        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void CameraAvailable()
    {
        try
        {
            List<Surface> surfaces = Arrays.asList(previewSurface, jpegCaptureSurface);
            mCamera.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    mSession = session;
                    SessionAvailable();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, null);


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void SessionAvailable()
    {
        try
        {
            CaptureRequest.Builder cRequest = mCamera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            cRequest.addTarget(previewSurface);
            // set capture options: fine-tune manual focus, white balance, etc.

            mSession.setRepeatingRequest(cRequest.build(), new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    Log.d("Log", "Captured");
                    try {
//                        if(null == originalBitmap)
//                            originalBitmap = view.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
//                        view.getBitmap(originalBitmap);
                        //CreateOverlay((15 << 16) | (163 << 8) | (15));
                        //if(CreateOverlay((208 << 16) | (0 << 8) | (0))) {
                        //    Canvas canvas = view.lockCanvas();
                        //    canvas.setBitmap(toShowBitmap);
                        //    view.unlockCanvasAndPost(canvas);
                        //}

                        CameraAvailable();
                    }catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }

                }
            }, null);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void ShowBitmap()
    {
        //ImageView mImage = (ImageView)findViewById(R.id.imageView);
        //mImage.setImageBitmap(toShowBitmap);
    }
}
