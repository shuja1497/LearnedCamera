package com.shuja1497.learnedcamera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.provider.Settings.System.DATE_FORMAT;

public class MainActivity extends Activity implements View.OnClickListener , TextToSpeech.OnInitListener {
    private Button upload,choose;
    private EditText name;
    private ImageView imgView;
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap;

    //    private String Uploadurl = "http://caff2a32.ngrok.io/vihaan/image_save.php" ;
//    private String Uploadurl = "http://192.168.43.18/image_save.php" ;
    private String Uploadurl = "https://8930065f.ngrok.io/" ;

    private static final String TAG = "MainActivity";
    private TextToSpeech tts ;

    private static final int PICK_CAMERA_IMAGE = 2;
    private static final int PICK_GALLERY_IMAGE = 1;

    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String IMAGE_DIRECTORY = "ImageScalling";

    MediaPlayer mp;

    private Uri imageCaptureUri;

    private File file;
    private File sourceFile;
    private File destFile;

    private SimpleDateFormat dateFormatter;




    private TextView textViewResult;
    private Button btnDetectObject, btnToggleCamera;
    private ImageView imageViewResult;
    private CameraView cameraView;

    private static final int INPUT_SIZE = 224;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = (CameraView) findViewById(R.id.cameraView);

        imageViewResult = (ImageView) findViewById(R.id.imageViewResult);
        textViewResult = (TextView) findViewById(R.id.textViewResult);
        textViewResult.setMovementMethod(new ScrollingMovementMethod());

        btnToggleCamera = (Button) findViewById(R.id.btnToggleCamera);
        btnDetectObject = (Button) findViewById(R.id.btnDetectObject);

        btnDetectObject.setOnClickListener(this);
        btnToggleCamera.setOnClickListener(this);


        cameraView.setFocus(CameraKit.Constants.FOCUS_CONTINUOUS);
        cameraView.setZoom(CameraKit.Constants.ZOOM_PINCH);
        cameraView.setCropOutput(true);
        cameraView.setJpegQuality(100);


        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);

                Log.d(TAG, "onPictureTaken: "+ picture);

                bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);

                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                //Log.d(TAG, "onPictureTaken: size ::::::"+picture.length);

                imageViewResult.setImageBitmap(bitmap);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

                byte[] x = byteArrayOutputStream.toByteArray();

                Log.d(TAG, "onPictureTaken: new length 2  ::::::::::"+x.length);



                //String s  = new String (picture,'UTF-8');

                //sourceFile = new File(getPathFromGooglePhotosUri(uriPhoto));

//                destFile = new File(file, "img_"
//                        + dateFormatter.format(new Date()).toString() + ".png");
//
//                Log.d(TAG, "Source File Path :" + sourceFile);
//
//                try {
//                    copyFile(sourceFile, destFile);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

//                bitmap = decodeFile(destFile);
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                byte[] imageInByte = stream.toByteArray();
//                long lengthbmp = imageInByte.length;
//                Log.d(TAG, "onClick: ********"+lengthbmp);
//                imageViewResult.setImageBitmap(bitmap);

                UploadImage upld = new UploadImage();
                upld.execute();

            }
        });

        tts = new TextToSpeech(this , this);
        file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }

        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);


//        name.setText("Welcome");

//        upload.setOnClickListener(this);
//        choose.setOnClickListener(this);
    }



    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
//            case R.id.upload:
//                //uploadImage();
//                bitmap = decodeFile(destFile);
////                Bitmap bitmap = bitmapOrg;
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//                byte[] imageInByte = stream.toByteArray();
//                long lengthbmp = imageInByte.length;
//                Log.d(TAG, "onClick: ********"+lengthbmp);
//                imgView.setImageBitmap(bitmap);
//                Log.d(TAG, "onClick: *******done");
//                UploadImage upld = new UploadImage();
//                upld.execute();
//                break;
//            case R.id.choose:
//                selectImage_gallery();
////                selectImage_camera();
//                break;
            case R.id.btnDetectObject:
                //mp = MediaPlayer.create(this, R.raw.soho);
//                mp.start();
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(500);
                cameraView.captureImage();
                break;


            case R.id.btnToggleCamera:
                cameraView.toggleFacing();
                break;

        }
    }
    //    private void selectImage_gallery(){
//        Intent intentGalley = new Intent(Intent.ACTION_PICK);
//        intentGalley.setType("image/*");
//        startActivityForResult(intentGalley, PICK_GALLERY_IMAGE);
//    }
//    private void selectImage_camera(){
//        destFile = new File(file, "img_"
//                + dateFormatter.format(new Date()).toString() + ".png");
//        imageCaptureUri = Uri.fromFile(destFile);
//
//        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
//        startActivityForResult(intentCamera, PICK_CAMERA_IMAGE);
//    }
/*
    private void uploadImage()
    {
        String.format(Uploadurl,ImagetoString(bitmap));
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Uploadurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "*******************onResponse: "+response);
                        name.setText(response.toString());
                        speakOut();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+ error);

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params =new HashMap<>();
                params.put("name",name.getText().toString().trim());
                params.put("image",ImagetoString(bitmap));

                return params;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        MySingelton.getInstance(MainActivity.this).addToRequestQue(stringRequest);
    }
    */
    private String ImagetoString(Bitmap bitmap)
    {
        Log.d(TAG, "ImagetoString: in");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        Log.d(TAG, "ImagetoString: out*******"+imgBytes.length);
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: in");
//
//        if (resultCode == Activity.RESULT_OK) {
//            switch (requestCode) {
//                case PICK_GALLERY_IMAGE:
//                    Uri uriPhoto = data.getData();
//                    Log.d(TAG + ".PICK_GALLERY_IMAGE", "Selected image uri path :" + uriPhoto.toString());
//
//                    imgView.setImageURI(uriPhoto);
//                    imgView.setVisibility(View.VISIBLE);
//                    name.setVisibility(View.VISIBLE);
//
//                    sourceFile = new File(getPathFromGooglePhotosUri(uriPhoto));
//
//                    destFile = new File(file, "img_"
//                            + dateFormatter.format(new Date()).toString() + ".png");
//
//                    Log.d(TAG, "Source File Path :" + sourceFile);
//
//                    try {
//                        copyFile(sourceFile, destFile);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                case PICK_CAMERA_IMAGE:
//                    Log.d(TAG + ".PICK_CAMERA_IMAGE", "Selected image uri path :" + imageCaptureUri);
//                    imgView.setImageURI(imageCaptureUri);
//                    break;
//            }
//
//        }
//        Log.d(TAG, "onActivityResult: out");
//    }
//

//    public String getPathFromGooglePhotosUri(Uri uriPhoto) {
//        if (uriPhoto == null)
//            return null;
//
//        FileInputStream input = null;
//        FileOutputStream output = null;
//        try {
//            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uriPhoto, "r");
//            FileDescriptor fd = pfd.getFileDescriptor();
//            input = new FileInputStream(fd);
//
//            String tempFilename = getTempFilename(this);
//            output = new FileOutputStream(tempFilename);
//
//            int read;
//            byte[] bytes = new byte[4096];
//            while ((read = input.read(bytes)) != -1) {
//                output.write(bytes, 0, read);
//            }
//            return tempFilename;
//        } catch (IOException ignored) {
//            // Nothing we can do
//        } finally {
//            closeSilently(input);
//            closeSilently(output);
//        }
//        return null;
//    }

//    public static void closeSilently(Closeable c) {
//        if (c == null)
//            return;
//        try {
//            c.close();
//        } catch (Throwable t) {
//            // Do nothing
//        }
//    }
//
//    private static String getTempFilename(Context context) throws IOException {
//        File outputDir = context.getCacheDir();
//        File outputFile = File.createTempFile("image", "tmp", outputDir);
//        return outputFile.getAbsolutePath();
//    }
//
//    private void copyFile(File sourceFile, File destFile) throws IOException {
//        if (!sourceFile.exists()) {
//            return;
//        }
//
//        FileChannel source = null;
//        FileChannel destination = null;
//        source = new FileInputStream(sourceFile).getChannel();
//        destination = new FileOutputStream(destFile).getChannel();
//        if (destination != null && source != null) {
//            destination.transferFrom(source, 0, source.size());
//        }
//        if (source != null) {
//            source.close();
//        }
//        if (destination != null) {
//            destination.close();
//        }
//    }


//    private Bitmap decodeFile(File f) {
//        Bitmap b = null;
//
//        //Decode image size
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inJustDecodeBounds = true;
//
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(f);
//            BitmapFactory.decodeStream(fis, null, o);
//            fis.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        int IMAGE_MAX_SIZE = 1024;
//        int scale = 1;
//        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
//            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
//                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
//        }
//
//        //Decode with inSampleSize
//        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inSampleSize = scale;
//        try {
//            fis = new FileInputStream(f);
//            b = BitmapFactory.decodeStream(fis, null, o2);
//            fis.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Log.d(TAG, "decodeFile: **********"+b);
//        //Log.d(TAG, "Width :" + b.getWidth() + " Height :" + b.getHeight());
//
//        destFile = new File(file, "img_"
//                + dateFormatter.format(new Date()).toString() + ".png");
//        try {
//            FileOutputStream out = new FileOutputStream(destFile);
//            b.compress(Bitmap.CompressFormat.PNG, 100, out);
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return b;
//    }



    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result =tts.setLanguage(Locale.US);
            tts.setSpeechRate((float) 0.8);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                //btnSpeak.setEnabled(true);
                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOut() {

        String text = textViewResult.getText().toString();

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

//    private void setPic(String mCurrentPhotoPath) {
//        // Get the dimensions of the View
//        int targetW = imgView.getWidth();
//        int targetH = imgView.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        imgView.setImageBitmap(bitmap);
//    }

    private class UploadImage extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
//            String.format(Uploadurl,ImagetoString(bitmap));
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Uploadurl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "*******************onResponse: "+response);

                            textViewResult.setHighlightColor(5);
                            textViewResult.setText(response.toString());
                            speakOut();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "onErrorResponse: "+ error);

                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Log.d(TAG, "getParams: herererrerererre");
                    Map<String,String> params =new HashMap<>();
                    params.put("name",textViewResult.getText().toString().trim());
                    params.put("image",ImagetoString(bitmap));

                    return params;
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);

            MySingelton.getInstance(MainActivity.this).addToRequestQue(stringRequest);


            return null;
        }
    }


}
