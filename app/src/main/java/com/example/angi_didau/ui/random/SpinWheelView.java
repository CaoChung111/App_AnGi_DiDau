package com.example.angi_didau.ui.random;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpinWheelView extends View {

    private List<String> items = new ArrayList<>();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF rectF = new RectF();

    private int[] colors = {Color.parseColor("#FFF1E8"), Color.parseColor("#FFE0D3")};
    private int[] textColors = {Color.parseColor("#FF6B35"), Color.parseColor("#AB3500")};

    private float currentAngle = 0;
    private boolean isSpinning = false;

    private OnSpinListener listener;

    public interface OnSpinListener {
        void onSpinEnd(int index, String result);
    }

    public SpinWheelView(Context context) {
        super(context);
        init();
    }

    public SpinWheelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        textPaint.setTextSize(34f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
    }

    public void setItems(List<String> items) {
        this.items = items;
        if (items != null && items.size() > 0) {
            float size = Math.max(12f, 34f * (8f / items.size()));
            textPaint.setTextSize(size);
        }
        invalidate();
    }

    public void setOnSpinListener(OnSpinListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (items == null || items.isEmpty()) return;

        int width = getWidth();
        int height = getHeight();
        int minSize = Math.min(width, height);
        
        int padding = 20;
        int radius = minSize / 2 - padding;
        int centerX = width / 2;
        int centerY = height / 2;

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        float sweepAngle = 360f / items.size();

        canvas.save();
        canvas.rotate(currentAngle, centerX, centerY);

        for (int i = 0; i < items.size(); i++) {
            paint.setColor(colors[i % colors.length]);
            canvas.drawArc(rectF, i * sweepAngle - 90, sweepAngle, true, paint);

            // Draw Text
            canvas.save();
            float angle = i * sweepAngle - 90 + sweepAngle / 2;
            canvas.rotate(angle, centerX, centerY);

            textPaint.setColor(textColors[i % textColors.length]);
            // Draw text along the radius
            canvas.drawText(items.get(i), centerX + radius * 0.6f, centerY + 12, textPaint);

            canvas.restore();
        }

        canvas.restore();
        
        // Draw center circle (white background for the spin icon)
        paint.setColor(Color.WHITE);
        canvas.drawCircle(centerX, centerY, 70, paint);
    }

    public void spin() {
        if (isSpinning || items.isEmpty()) return;
        isSpinning = true;

        Random random = new Random();
        int targetIndex = random.nextInt(items.size());
        
        // Number of full rotations
        int rounds = 5 + random.nextInt(5);
        
        float sweepAngle = 360f / items.size();
        
        // We want the target index to be at the top (-90 degrees)
        float targetAngleOffset = 360f - (targetIndex * sweepAngle + sweepAngle / 2);
        
        // Add random offset within the slice
        float randomOffset = (random.nextFloat() - 0.5f) * sweepAngle * 0.8f;
        
        float endAngle = currentAngle + 360 * rounds + targetAngleOffset + randomOffset;

        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "rotationAngle", currentAngle, endAngle);
        animator.setDuration(3500 + random.nextInt(1000));
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                isSpinning = false;
                currentAngle = endAngle % 360;
                if (listener != null) {
                    listener.onSpinEnd(targetIndex, items.get(targetIndex));
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        animator.start();
    }

    // Used by ObjectAnimator
    public void setRotationAngle(float angle) {
        this.currentAngle = angle;
        invalidate();
    }
}
