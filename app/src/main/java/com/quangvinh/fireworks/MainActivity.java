package com.quangvinh.fireworks;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.util.Random;
import java.util.Vector;

/**
 * @author ServantOfEvil
 */

public class MainActivity extends AppCompatActivity {

    public static final int width = Resources.getSystem().getDisplayMetrics().widthPixels;
    public static final int height = Resources.getSystem().getDisplayMetrics().heightPixels;
    public LinearLayout linearLayout;
    public SeekBar quantity, speed;
    public static LinearLayout.LayoutParams lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        linearLayout = findViewById(R.id.RenderedScreen);

        quantity = findViewById(R.id.seekBarQTT);
        speed = findViewById(R.id.seekBarSP);

        // final ImageView imageView = new ImageView(this);
        // imageView.setImageResource(R.drawable.icon);
        lay = new LinearLayout.LayoutParams(width, height - 300);
        lay.gravity = Gravity.CENTER;

        final subScreen subScr = new subScreen(this);
        subScr.setLayoutParams(lay);

        linearLayout.removeAllViews();
        linearLayout.addView(subScr);
        linearLayout.addView(speed);
        linearLayout.addView(quantity);

        quantity.setProgress(5);
        quantity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                subScreen.QUANTITY = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        speed.setProgress(50);
        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                subScreen.SPEED = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.red);
        //    final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        //final Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim_alpha);

        /*imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setAlpha(1f);
            }
        });*/
    }

}

class subScreen extends View {

    public static Context context;
    public static int QUANTITY = 5, SPEED = 50;
    public static final Vector vector = new Vector();
    private Paint paint = new Paint();
    public static int width = 500, height = 500;
    //   Bitmap bm;

    public subScreen(Context context) {
        super(context);
        subScreen.context = context;
        vector.addElement(new FireWork(context));
        //    bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.red);
        //((MainActivity)context).linearLayout.setLayoutParams(MainActivity.lay);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        width = params.width;
        height = params.height;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        while (vector.size() < QUANTITY) vector.addElement(new FireWork(context));
        canvas.drawColor(Color.BLACK);
        paint.setColor(Color.RED);
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Number of firework: " + QUANTITY, 0, paint.getTextSize(), paint);
        canvas.drawText("Speed: " + SPEED, 0, 2 * paint.getTextSize(), paint);
        for (int i = 0; i < vector.size(); i++)
            ((FireWork) vector.elementAt(i)).Animate(canvas, paint);
        try {
            Thread.sleep(100 - SPEED);
        } catch (Exception e) {
        }
        invalidate();
    }

}

class FireWork {

    int[] num_of_lights = {24, 36, 72, 48, 60};
    public static int[] img = {R.drawable.torquoise, R.drawable.blue, R.drawable.red, R.drawable.yellow};
    Bitmap bm;
    private static final float gravity = 0.987f;
    float x, y, velocity, angle;
    int size = num_of_lights[Utils.nextInt(0, num_of_lights.length)];
    Vector lights = new Vector();
    int life = Utils.nextInt(20, 80);
    int lifeCheck = 0;
    Bitmap[] bms = new Bitmap[4];


    public FireWork(Context context) {
        x = subScreen.width / 2 + Utils.nextInt(-subScreen.width / 4, subScreen.width / 4);
        y = subScreen.height + Utils.nextInt(-20, 0);
        velocity = Utils.nextInt(7, 9);
        angle = Utils.nextInt(60, 120);
        bm = BitmapFactory.decodeResource(context.getResources(), img[Utils.nextInt(0, 3)]);
        for (int i = 0; i < bms.length; i++) bms[i] = Bitmap.createBitmap(bm, 0, i * 7, 7, 7);
        for (int i = 0; i < size; i++) lights.addElement(new Light(bms[3]));
    }

    public void Animate(Canvas canvas, Paint paint) {
        int j = 2, k = 0;
        lifeCheck++;
        if (lifeCheck < life) {
            canvas.drawBitmap(bms[lifeCheck / 4 % 4], x, y, paint);
            x += velocity * Math.cos(Math.toRadians(angle));
            y -= velocity * Math.sin(Math.toRadians(angle));
            velocity *= gravity;
            //canvas.drawText("a", x, y, paint);
            if (lifeCheck == life - 1) for (int i = 0; i < lights.size(); i++)
                ((Light) lights.elementAt(i)).creatPosition(x, y);
        } else if (lifeCheck < life + 15) {
            for (int i = 0; i < lights.size(); i++) {
                if (i > 0 && i * 30 % 360 == 0) j++;
                if (i > 0 && k * i * 30 % 360 == 0) k += 15;

                Light light = null;
                (light = ((Light) lights.elementAt(i))).setPosition(j * Math.cos(Math.toRadians(i * 30 + k)), j * Math.sin(Math.toRadians(i * 30 + k) + 0.2f));
                light.render(canvas, paint);

            }
        } else {
            subScreen.vector.removeElement(this);
        }
    }

}

class Light {

    float x, y;
    Bitmap bit;

    public Light(Bitmap bm) {
        bit = bm;
    }


    public void render(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bit, x, y, paint);
    }

    public void setPosition(double x, double y) {
        this.x += (float) x;
        this.y += (float) y;
    }

    public void creatPosition(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }

}

class Utils {
    public static final Random random = new Random();

    public static final int nextInt(int from, int to) {
        return Math.abs(random.nextInt()) % (to - from) + from;
    }

}

class Font {

}