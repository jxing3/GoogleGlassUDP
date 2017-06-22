package com.example.jessexing.solesoundglassudp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by jessexing on 6/21/16.
 */
public class BalanceGraph {

    private Paint paintGraph;

    private int centerX;
    private int centerY;

    private int width;
    private int height;

    private Paint paintLine;


    public BalanceGraph(int x, int y, int w, int h) {
        paintGraph = new Paint();
        paintGraph = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintGraph.setAntiAlias(true);
        paintGraph.setStrokeWidth(5);

        paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintLine.setStrokeWidth(20);
        paintLine.setColor(Color.GREEN);

        this.centerX = x;
        this.centerY = y;

        this.width = w;
        this.height = h;
        // this.values = new float[numBars];
    }

    /** Takes array of values between 0 and 1 corresponding to bar length
     *
     * @param value array of values (each for a different bar)
     */

   /* public void setValues(float[] values) {
        this.values = values;
    } */

    public void draw(Canvas c, float value, float thr, float div) {

        int startX = centerX;
        int startY = centerY;

        float h = this.height;
        float[] rect = new float[4];

        float[] line = new float[2];

        c.drawLine(this.centerX,this.centerY+10,this.centerX,this.centerY-this.height-10,paintLine);

        if(value<thr) {
            rect[0] = (this.centerX-10)+(value-thr)/div*this.width/2;
            rect[1] = this.centerY-this.height;
            rect[2] = (this.centerX-10);
            rect[3] = this.centerY;
            paintGraph.setColor(Color.BLUE);

            // c.drawRect(left top right bottom);
            c.drawRect(rect[0], rect[1], rect[2], rect[3], paintGraph);

        } else {
            rect[0] = this.centerX+10;
            rect[1] = this.centerY-this.height;
            rect[2] = (this.centerX+10)+(value-thr)/div*this.width/2;
            rect[3] = this.centerY;
            paintGraph.setColor(Color.RED);

            c.drawRect(rect[0], rect[1], rect[2], rect[3], paintGraph);
        }
            //c.drawRect(rect[0], rect[1], rect[2], rect[3], paintGraph);
            //c.drawRect(rect[0], rect[1], rect[2], rect[3], paintGraph);
    }
}
