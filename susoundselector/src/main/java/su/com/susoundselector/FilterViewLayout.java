package su.com.susoundselector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import java.util.List;

public class FilterViewLayout extends LinearLayout {

    float x,y;
    boolean contain=false;
    boolean selected=false;
    Bitmap bitmap;
    Paint paint,paint2;
    List<SoundData> group;
    int maxSize;

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setGroup(List<SoundData> group) {
        this.group = group;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public FilterViewLayout(Context context) {
        this(context,null);
    }

    public FilterViewLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FilterViewLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed,l,t,r,b);
    }

    void init(){
        bitmap= BitmapFactory.decodeResource(getResources(),R.mipmap.check);
        paint=new Paint();
        paint2=new Paint();
        paint.setAntiAlias(true);
        paint2.setAntiAlias(true);
        paint.setColor(Color.parseColor("#99000000"));
        paint2.setColor(Color.WHITE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getX()+getLeft();
                    y = event.getY()+getTop();
                    if (getLeft() < x && x < getRight() && getTop() < y && y < getBottom()) {
                        contain = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    x = event.getX()+getLeft();
                    y = event.getY()+getTop();
                    if (contain && getLeft() < x && x < getRight() && getTop() < y && y < getBottom()) {
                        if(group.size()<maxSize) {
                            selected=!selected;
                        }else{
                            if(selected)
                                selected=false;
                        }
                        invalidate();
                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return super.onTouchEvent(event);
    }

    @SuppressLint("NewApi")
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        try {
            if (selected) {
                canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
                canvas.drawBitmap(bitmap, getHeight()/2-bitmap.getHeight()/2, getHeight()/2-bitmap.getHeight()/2, paint2);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
