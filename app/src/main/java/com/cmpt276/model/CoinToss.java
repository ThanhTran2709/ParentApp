package com.cmpt276.model;
import android.view.animation.Animation;

import android.view.animation.Transformation;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

public class CoinToss extends Animation {
    private final float fromXDegrees;
    private final float toXDegrees;
    private final float fromYDegrees;
    private final float toYDegrees;
    private final float fromZDegrees;
    private final float toZDegrees;
    private Camera camera;
    private int width = 0;
    private int height = 0;
    private ImageView imageView;
    private int currentDrawable;
    private int nextDrawable;
    private int numOfRepetition = 0;
    ConstraintLayout.LayoutParams params;

    public CoinToss(ImageView imageView, int currentDrawable, int nextDrawable, float fromXDegrees, float toXDegrees, float fromYDegrees, float toYDegrees, float fromZDegrees, float toZDegrees) {
        this.fromXDegrees = fromXDegrees;
        this.toXDegrees = toXDegrees;
        this.fromYDegrees = fromYDegrees;
        this.toYDegrees = toYDegrees;
        this.fromZDegrees = fromZDegrees;
        this.toZDegrees = toZDegrees;
        this.imageView = imageView;
        this.currentDrawable = currentDrawable;
        this.nextDrawable = nextDrawable;
        params = (ConstraintLayout.LayoutParams) imageView.getLayoutParams();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        this.width = width / 2;
        this.height = height / 2;
        camera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float xDegrees = fromXDegrees + (toXDegrees  * interpolatedTime);
        float yDegrees = fromYDegrees + (toYDegrees  * interpolatedTime);
        float zDegrees = fromZDegrees + (toZDegrees  * interpolatedTime);

        Matrix matrix = t.getMatrix();

        applyZoom(interpolatedTime);

        // Apply Rotation
        if (interpolatedTime >= 0.5f) {
            if (interpolatedTime == 1f){
                int temp = currentDrawable;
                currentDrawable = nextDrawable;
                nextDrawable = temp;

                numOfRepetition++;

            } else {
                imageView.setImageResource(nextDrawable);
                imageView.setLayoutParams(params);
            }

            xDegrees -= 180f;

        } else if (interpolatedTime == 0)
            imageView.setImageResource(currentDrawable);



        camera.save();
        camera.rotateX(-xDegrees);
        camera.rotateY(yDegrees);
        camera.rotateZ(zDegrees);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-this.width, -this.height);
        matrix.postTranslate(this.width, this.height);
    }

    //TODO: fix scaling problem
    private void applyZoom(float interpolatedTime){
        if ((numOfRepetition + interpolatedTime) / ((super.getRepeatCount() + 1) / 2) <= 1){
            imageView.setScaleX((float)0.25 + (numOfRepetition + interpolatedTime) / ((super.getRepeatCount() + 1) / 2));
            imageView.setScaleY((float)0.25 + (numOfRepetition + interpolatedTime) / ((super.getRepeatCount() + 1) / 2));
        } else if (numOfRepetition < (super.getRepeatCount() + 1)){
            imageView.setScaleX(3 - (numOfRepetition + interpolatedTime) / ((super.getRepeatCount() + 1) / 2));
            imageView.setScaleY(3- (numOfRepetition + interpolatedTime) / ((super.getRepeatCount() + 1) / 2));
        }
    }
}