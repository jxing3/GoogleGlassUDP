package com.example.jessexing.solesoundglassudp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by jessexing on 6/21/16.
 */
public class BalanceGraph2 {

    private Paint paintGraph;
    private Paint paintLine;

    private int centerX;
    private int centerY;

    private int width;
    private int height;


    public BalanceGraph2(int x, int y, int w, int h) {
        paintGraph = new Paint();
        paintGraph = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintGraph.setAntiAlias(true);
        paintGraph.setStrokeWidth(5);

        this.centerX = x;
        this.centerY = y;

        this.width = w;
        this.height = h;

        paintLine = new Paint();
        paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLine.setAntiAlias(true);
        paintLine.setStrokeWidth(10);
        paintLine.setColor(Color.WHITE);

        // this.values = new float[numBars];
    }

    /** Takes array of values between 0 and 1 corresponding to bar length
     *
     * @param value array of values (each for a different bar)
     */

   /* public void setValues(float[] values) {
        this.values = values;
    } */

    public void draw(Canvas c, float value) {

        float[] rect = new float[4];
        float[] rect2 = new float[4];
        float[] line = new float[4];

        rect[0] = this.centerX-this.width/2;
        rect[1] = this.centerY-this.height;
        rect[2] = this.centerX;
        rect[3] = this.centerY;

        rect2[0] = this.centerX;
        rect2[1] = this.centerY-this.height;
        rect2[2] = this.centerX+this.width/2;
        rect2[3] = this.centerY;

        line[0] = this.centerX+value*this.width/2;
        line[1] = this.centerY;
        line[2] = this.centerX+value*this.width/2;
        line[3] = this.centerY-this.height;

        paintGraph.setColor(Color.BLUE);

        c.drawRect(rect[0], rect[1], rect[2], rect[3], paintGraph);

        paintGraph.setColor(Color.RED);

        c.drawRect(rect2[0], rect2[1], rect2[2], rect2[3], paintGraph);

        c.drawLine(line[0],line[1],line[2],line[3],paintLine);

    }
}
