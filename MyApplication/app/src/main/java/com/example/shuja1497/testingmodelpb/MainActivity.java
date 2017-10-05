package com.example.shuja1497.testingmodelpb;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.shuja1497.testingmodelpb.models.Classification;
import com.example.shuja1497.testingmodelpb.models.Classifier;
import com.example.shuja1497.testingmodelpb.models.TensorFlowClassifier;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.bitmap;
import static android.content.ContentValues.TAG;


public class MainActivity extends Activity implements View.OnClickListener {

    private static final int PIXEL_WIDTH = 32;

    // ui elements
    private Button btn;
    private TextView resText;
    private List<Classifier> mClassifiers = new ArrayList<>();

    // views
    private PointF mTmpPiont = new PointF();

    private float mLastX;
    private float mLastY;

    @Override
    // In the onCreate() method, you perform basic application startup logic that should happen
    //only once for the entire life of the activity.
    protected void onCreate(Bundle savedInstanceState) {
        //initialization
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resText = (TextView) findViewById(R.id.tv);
        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(this);

        // tensorflow
        //load up our saved model to perform inference from local storage
        loadModel();
    }

    //the activity lifecycle

    @Override
    //OnResume() is called when the user resumes his Activity which he left a while ago,
    // //say he presses home button and then comes back to app, onResume() is called.
    protected void onResume() {
        super.onResume();
    }

    @Override
    //OnPause() is called when the user receives an event like a call or a text message,
    // //when onPause() is called the Activity may be partially or completely hidden.
    protected void onPause() {
        super.onPause();
    }
    //creates a model object in memory using the saved tensorflow protobuf model file
    //which contains all the learned weights
    private void loadModel() {
        //The Runnable interface is another way in which you can implement multi-threading other than extending the
        // //Thread class due to the fact that Java allows you to extend only one class. Runnable is just an interface,
        // //which provides the method run.
        // //Threads are implementations and use Runnable to call the method run().
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //add 2 classifiers to our classifier arraylist
                    //the tensorflow classifier and the keras classifier
//                    mClassifiers.add(
//                            TensorFlowClassifier.create(getAssets(), "TensorFlow",
//                                    "opt_mnist_convnet-tf.pb", "labels.txt", PIXEL_WIDTH,
//                                    "input", "output", true));
                    mClassifiers.add(
                            TensorFlowClassifier.create(getAssets(), "Keras",
                                    "frozen_mnist_convnet.pb", "labels.txt", PIXEL_WIDTH,
                                    "conv2d_13_input",
                                    "dense_8/Softmax", false));
                } catch (final Exception e) {
                    //if they aren't found, throw an error!
                    throw new RuntimeException("Error initializing classifiers!", e);
                }
            }
        }).start();
    }


    @Override
    public void onClick(View view) {
        //when the user clicks something
         if (view.getId() == R.id.btn) {
            //if the user clicks the classify button
            //get the pixel data and store it in an array
            float pixels[] = getPixelData();

            //init an empty string to fill with the classification output
            String text = "";
            //for each classifier in our array
            for (Classifier classifier : mClassifiers) {
                //perform classification on the image
                final Classification res = classifier.recognize(pixels);
                //if it can't classify, output a question mark
                if (res.getLabel() == null) {
                    text += classifier.name() + ": ?\n";
                } else {
                    //else output its name
                    text += String.format("%s: %s, %f\n", classifier.name(), res.getLabel(),
                            res.getConf());
                }
            }
            resText.setText(text);
        }
    }

    private float[] getPixelData() {

        Bitmap bit = BitmapFactory.decodeResource(getResources(), R.drawable.horse);
        Bitmap bitmap = Bitmap.createScaledBitmap(bit,32,32,false);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Get 28x28 pixel data from bitmap
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        float[] retPixels = new float[pixels.length];
        for (int i = 0; i < pixels.length; ++i) {
            // Set 0 for white and 255 for black pixel
            int pix = pixels[i];
            int b = pix & 0xff;
            retPixels[i] = (float)((0xff - b)/255.0);
        }
        Log.d(TAG, "onCreate: "+retPixels);

        return retPixels;
    }


}