package com.example.myapplication;

/*
参考
TensorFlow Lite Examples text_classification
 */


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import androidx.annotation.WorkerThread;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ClassificationClient {
    private Interpreter tflite;
    private final Context context;
    private static final int Height=28;
    private static final int Width=28;
    private static final String TAG = "ClassificationDemo";
    private static final String MODEL_PATH = "converted_model.tflite";
   // private static final String DIC_PATH = "text_classification_vocab.txt";
    private static final String LABEL_PATH = "classification_label.txt";
    private final List<String> labels = new ArrayList<>();
    public ClassificationClient(Context context) {
        this.context = context;
    }

    @WorkerThread
    public synchronized float[][] classify(Bitmap bmp) {
        // Pre-prosessing.
        float[][][][] input=tokenizeInputImage(bmp);//1 28 28 1の形式にすること
        // Run inference.
        Log.v(TAG, "Classifying text with TF Lite...");
        float[][] output = new float[1][labels.size()];
        tflite.run(input, output);
        return output;
    }

        private float[][][][] tokenizeInputImage(Bitmap bmp) {
           // Bitmap bmp1 = BitmapFactory.decodeResource(getResources(), R.drawable.red);
          //  Bitmap bmp = ImageToBitmap(img);
       Bitmap resized=Bitmap.createScaledBitmap(bmp,Width,Height,true);//縮小ではfilterを無効にするとジャギーがはっきりと表れる。 識別精度が落ちる？

            return MapToArray(resized);

        }
    /*    private Bitmap ImageToBitmap(Image img){
            //Image image = reader.acquireLatestImage();
            ByteBuffer buffer = img.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.capacity()];
            buffer.get(bytes);
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
            return bitmapImage;
    }*/
    //greyscale
    public float pixelConverting(int pixel) {
       // int alpha=(pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel ) & 0xff;
       // Log.d(TAG,"out  "+pixel);
        //Log.d(TAG,"out  "+"alpha"+ alpha +" red "+red+" green " +green+" blue "+blue);
        return (red*0.3f+green+0.59f+blue*0.11f)/255; //グレースケール化して０〜１の範囲に正規化
    }
    public static Bitmap test;
        private float[][][][] MapToArray(Bitmap bmp){
        float[][][][] ans=new float[1][bmp.getWidth()][bmp.getHeight()][1];
        float mGray;
            test= Bitmap.createBitmap(28,28, Bitmap.Config.ARGB_8888);
        for(int i=0;i<bmp.getWidth();i++){
            for(int j=0;j<bmp.getHeight();j++){
                int pixel = bmp.getPixel(i, j);
                mGray= pixelConverting(pixel);
                ans[0][j][i][0]=mGray;//縦横の順番！
                test.setPixel(i,j,pixel);
            }
        }
        return ans;
        }


    @WorkerThread
    public void load(){
        loadModel();
        loadLabels();
    }

    private void loadLabels() {
        try {
            loadLabelFile(this.context.getAssets());
            Log.v(TAG, "Labels loaded.");
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    @WorkerThread
    public synchronized void unload() {
        tflite.close();
        labels.clear();
    }
    @WorkerThread
    private synchronized void loadModel() {
        try {
            ByteBuffer buffer = loadModelFile(this.context.getAssets());
            tflite = new Interpreter(buffer);
            Log.v(TAG, "TFLite model loaded.");
        } catch (IOException ex) {
            Log.e(TAG, ex.getMessage());
        }
    }
    private static MappedByteBuffer loadModelFile(AssetManager assetManager) throws IOException {
        try (AssetFileDescriptor fileDescriptor = assetManager.openFd(MODEL_PATH);
             FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor())) {
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }
    private void loadLabelFile(AssetManager assetManager) throws IOException {
        try (InputStream ins = assetManager.open(LABEL_PATH);
             BufferedReader reader = new BufferedReader(new InputStreamReader(ins))) {
            // Each line in the label file is a label.
            while (reader.ready()) {
                labels.add(reader.readLine());
            }
        }
    }

    List<String> getLabels() {
        return this.labels;
    }
    Interpreter getTflite() {
        return this.tflite;
    }
}
