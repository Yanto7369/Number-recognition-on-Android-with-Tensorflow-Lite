package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

//"<内部共有ストレージ>/deeplab/"に以下の2ファイルを置いて実行する
//  ・"frozen_inference_graph.pb" :モデルファイル
//  ・"image.jpg" :車が写った画像
public class MainActivity extends AppCompatActivity {



    //以下雑務
    //=========================================================================
    private static final int MAX_SRCWIDTH = 1920;
    private static final int MAX_SRCHEIGHT = 1920;
    private int mImageIndex=-1;
    private ClassificationClient client;
    private static final String TAG = "ClassificationDemo";
    private Handler handler;
    private TextView resultTextView;
   // private ScrollView scrollView;
    private  Bitmap bmap;
    private penView penview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        client=new ClassificationClient(getApplicationContext());
        handler = new Handler();
        Button classifyButton = findViewById(R.id.classify_button);
        classifyButton.setOnClickListener(
                (View v) -> {
                    if(bmap!=null) {
                        ImageView image = findViewById(R.id.imageView);
                        image.setImageBitmap(bmap);
                        classify(bmap);
                    }
                    else
                        Log.d(TAG,"null!");
                });

        penview=findViewById(R.id.pen_view);
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(
                (View v) -> {
                    bmap=penview.getBmp();
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart");
        handler.post(
                () -> {
                    client.load();
                });
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        handler.post(
                () -> {
                    client.unload();
                });
    }
    private void classify(Bitmap bmp) {
        handler.post(
                () -> {
                    // Run text classification with TF Lite.
                    float[][] results = client.classify(bmp);
                    ImageView image = findViewById(R.id.imageView);
                    image.setImageBitmap(client.test);

                    // Show classification result on screen
                    showResult(results);
                });
    }
    private void showResult( final float[][] results) {
        // Run on UI thread as we'll updating our app UI
        runOnUiThread(
                () -> {
                    String textToShow ="";
                    for (int i = 0; i < results[0].length; i++) {
                        //Result result = results.get(i);
                        textToShow +=
                                String.format("    %s: %s\n",i,results[0][i]);
                    }
                    textToShow+=String.format("この文字は%sです",maxindex(results[0]));
                    textToShow += "---------\n";
                    Context context = getApplicationContext();
                    Toast.makeText(context , textToShow, Toast.LENGTH_LONG).show();

                });
    }
    private int maxindex(float[] array){
        float res=array[0];
        int number=0;
        for(int i=0;i<array.length;i++){
            if(res<array[i]){
                res=array[i];
                number=i;
            }
        }
        return  number;
    }
    //表示に適したサイズに縮小したBitmapを生成する
   /* private Bitmap createShrinedkBitmap(Bitmap src) {
        int wOrg=src.getWidth();
        int hOrg=src.getHeight();
        int w=wOrg,h=hOrg;
        if(w>MAX_SRCWIDTH) {h=(int) (((float)MAX_SRCWIDTH/w)*h);w=MAX_SRCWIDTH;}
        if(h>MAX_SRCHEIGHT) {w=(int) (((float)MAX_SRCHEIGHT/h)*w);h=MAX_SRCHEIGHT;}
        if(w==wOrg && h==hOrg) return null;

        return Bitmap.createScaledBitmap(src,w,h,true);
    }*/

}

