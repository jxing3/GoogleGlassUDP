package com.example.jessexing.solesoundglassudp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by jessexing on 6/18/16.
 */
public class BarGraph {

    public static final int sep = 15;

    private Paint paintGraph;

    private int minx;
    private int miny;

    private int width;
    private int height;

    private int numBars;

   // private float[] values;

    public BarGraph(int x, int y, int w, int h, int numBars) {
        paintGraph = new Paint();
        paintGraph = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintGraph.setColor(Color.GREEN);
        paintGraph.setAntiAlias(true);
        paintGraph.setStrokeWidth(5);

        this.minx = x;
        this.miny = y;

        this.width = (w-numBars*sep)/numBars;
        this.height = h;

        this.numBars = numBars;

       // this.values = new float[numBars];
    }

    /** Takes array of values between 0 and 1 corresponding to bar length
     *
     * @param values array of values (each for a different bar)
     */

   /* public void setValues(float[] values) {
        this.values = values;
    } */

    public void draw(Canvas c, float[] values) {

        int startX = minx;
        int startY = miny;

        for (int i=0;i<numBars;i++) {
            float h = values[i]*this.height;
            float[] rect = new float[4];

            rect[0]=startX;
            rect[1]=startY-h;
            rect[2]=this.width+startX;
            rect[3]=startY;

            startX += this.width+sep;
            c.drawRect(rect[0], rect[1], rect[2], rect[3], paintGraph);

        }

    }
}
