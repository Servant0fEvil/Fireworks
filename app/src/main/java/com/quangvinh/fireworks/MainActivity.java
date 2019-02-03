package com.quangvinh.fireworks;

import android.content.Context;
import android.content.res.Resources;
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
import android.widget.TextView;

import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

/**
 * @author ServantOfEvil
 */

public class MainActivity extends AppCompatActivity {

    public static final int width = Resources.getSystem().getDisplayMetrics().widthPixels;//lấy chiều rộng màn hình
    public static final int height = Resources.getSystem().getDisplayMetrics().heightPixels;//lấy chiều cao màn hình
    public LinearLayout linearLayout;//màn hình Canvas
    public SeekBar speed;//SeekBar chỉnh tốc độ
    public TextView sp;//label của SeekBar
    public static LinearLayout.LayoutParams lay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//loại bỏ title
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//set full screen mode
        setContentView(R.layout.activity_main);

        linearLayout = findViewById(R.id.RenderedScreen);
        speed = findViewById(R.id.seekBarSP);
        sp = findViewById(R.id.label);

//set kích thước màn hình Canvas
        lay = new LinearLayout.LayoutParams(width, (int) (height - Utils.DptoPx(45, this) - Utils.SptoPx(10, this)));
        lay.gravity = Gravity.CENTER;

        final subScreen subScr = new subScreen(this);//màn hình Canvas
        subScr.setLayoutParams(lay);

        linearLayout.removeAllViews();
        linearLayout.addView(subScr);
        linearLayout.addView(sp);
        linearLayout.addView(speed);

        speed.setProgress(0);
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

    }

}

class subScreen extends View {

    public static int QUANTITY = 7, SPEED = 0;
    public static Context context;
    private static int[] color = {0xff95f436, 0xffff5500, 0xffffff00, 0xff0022ff, 0xff663366};
    public static final Vector vector = new Vector();
    private Paint paint = new Paint();
    public static int width = 500, height = 500;

    public subScreen(Context context) {
        super(context);
        subScreen.context = context;
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
        int x = 10, y = 10;

        while (vector.size() < QUANTITY)
            vector.addElement(new FireWork(color[Utils.nextInt(0, color.length)]));

        canvas.drawColor(Color.BLACK);

        paint.setColor(Color.RED);
        paint.setTextSize(18);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText("Tăng tốc thêm: " + SPEED, x, y += paint.getTextSize(), paint);
        canvas.drawText(Utils.Today(), x, y += paint.getTextSize() + 10, paint);
        canvas.drawText(Utils.soNgay(), x, y += paint.getTextSize() + 10, paint);

        for (int i = 0; i < vector.size(); i++)
            ((FireWork) vector.elementAt(i)).Animate(canvas, paint);

        try {
            Thread.sleep(15 - SPEED);
        } catch (Exception e) {
        }

        invalidate();
    }

}

class FireWork {

    private int[] num_of_lights = {24, 36, 72, 48, 60};
    private static final float gravity = 0.987f;
    private float x, y, velocity, angle;
    private Light Lights[] = new Light[num_of_lights[Utils.nextInt(0, num_of_lights.length)]];
    private int life;
    private int lifeCheck = 0;
    private int color;


    public FireWork(int color) {
        x = subScreen.width / 2 + Utils.nextInt(-subScreen.width / 4, subScreen.width / 4);
        y = subScreen.height + Utils.nextInt(-20, 0);
        velocity = Utils.nextInt(10, 15);
        life = Utils.nextInt(Math.round(subScreen.height / 2 / velocity), Math.round(subScreen.height * 8 / 10 / velocity));
        angle = Utils.nextInt(80, 110);
        this.color = color;

        for (int i = 0; i < Lights.length; i++) Lights[i] = new Light(color);
    }

    public void Animate(Canvas canvas, Paint paint) {
        int j = 2, k = 0;

        lifeCheck++;

        if (lifeCheck < life) {
            paint.setColor(color);

            if (lifeCheck % 2 == 0) canvas.drawArc(x, y, x + 4, y + 4, 0, 360, true, paint);

            x += velocity * Math.cos(Math.toRadians(angle));
            y -= velocity * Math.sin(Math.toRadians(angle));
            velocity *= gravity;

            if (lifeCheck == life - 1) for (int i = 0; i < Lights.length; i++)
                Lights[i].creatPosition(x, y);

        } else if (lifeCheck < life + 15) {

            for (int i = 0; i < Lights.length; i++) {

                if (i > 0 && i * 30 % 360 == 0) j++;
                if (i > 0 && k * i * 30 % 360 == 0) k += 15;

                Light light;
                (light = Lights[i]).setPosition(j * Math.cos(Math.toRadians(i * 30 + k)), j * Math.sin(Math.toRadians(i * 30 + k) + 0.2f));
                light.render(canvas, paint);

            }
        } else {
            subScreen.vector.removeElement(this);
        }
    }

}

class Light {

    private float x, y;
    private int color;

    public Light(int color) {
        this.color = color;
    }


    public void render(Canvas canvas, Paint paint) {
        paint.setColor(color);
        canvas.drawRect(x, y, x + 2, y + 2, paint);
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

    private static Calendar GiaoThua = Calendar.getInstance();
    private static long GT;

    static {
        GiaoThua.set(2019, 1, 4, 24, 0, 0);
        GT = GiaoThua.getTimeInMillis();
    }

    public static long Now() {
        return System.currentTimeMillis();
    }

    public static final Random random = new Random();

    public static final int nextInt(int from, int to) {
        return Math.abs(random.nextInt()) % (to - from) + from;
    }

    public static String soNgay() {
        long time = (GT - Now()) / 1000;

        if (time <= 0) {
            if (time >= -518400) {
                subScreen.QUANTITY = 12;
                return "Chúc Mừng Năm Mới, Xuân Kỷ Hợi 2019!!!";
            } else return "";
        }

        String result;
        int songay = 0, sogio = 0, sophut = 0, sogiay = 0;

        if (time > 86400) {
            songay = (int) time / 86400;
            time -= songay * 86400;
        }
        if (time > 3600) {
            sogio = (int) time / 3600;
            time -= sogio * 3600;
        }
        if (time > 60) {
            sophut = (int) time / 60;
            time -= sophut * 60;
        }
        sogiay = (int) time;
        result = "Còn ";
        if (songay > 0) result += songay + " ngày ";
        if (sogio > 0) result += sogio + " giờ ";
        if (sophut > 0) result += sophut + " phút ";
        if (sogiay > 0) result += sogiay + " giây ";
        result += "nữa là đến giao thừa.";
        return result;
    }

    public static float DptoPx(int Dp, Context context) {
        return Dp * context.getResources().getDisplayMetrics().density;
    }

    public static String Today() {
        Calendar calendar = Calendar.getInstance();
        int tmp;
        StringBuffer result = new StringBuffer();

        tmp = calendar.get(Calendar.DAY_OF_MONTH);
        if (tmp <= 9) result.append("0");
        result.append(tmp + "/");
        tmp = calendar.get(Calendar.MONTH) + 1;
        if (tmp <= 9) result.append("0");
        result.append(tmp + "/");
        tmp = calendar.get(Calendar.YEAR);
        result.append(tmp + " ");
        tmp = calendar.get(Calendar.HOUR_OF_DAY);
        if (tmp <= 9) result.append("0");
        result.append(tmp + ":");
        tmp = calendar.get(Calendar.MINUTE);
        if (tmp <= 9) result.append("0");
        result.append(tmp + ":");
        tmp = calendar.get(Calendar.SECOND);
        if (tmp <= 9) result.append("0");
        result.append(tmp);

        return result.toString();
    }

    public static float SptoPx(int Sp, Context context) {
        return Sp * context.getResources().getDisplayMetrics().scaledDensity;
    }

}