package com.example.jessexing.solesoundglassudp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class DrawView extends View {
	private static final String TAG = "DrawView";

	List<Point> points = new ArrayList<Point>();
	List<Paint> paints = new ArrayList<Paint>();

	private String time;
//	private String cadence;
//	private String symmetry;

	private Double cadence;
	private Double symmetry;

	private float pitch;
	private float roll;

	private float[] linAcc;

	private int toggle;

	private Paint paint;
	private Paint paint1;

	private CenterGraph graph;
	private BarGraph barGraph;
	Integer count;


	private gaitMetrics gMetrics;

	private long pass_time;

	private float offset;
	private float range;

	private BalanceGraph balanceGraph;
	private BalanceGraph2 balanceGraph2;
	private BalanceGraph balanceGraph3;
	//Paint paint = new Paint();

	public DrawView(Context context) {
		super(context);
		setFocusable(true);
		setFocusableInTouchMode(true);
		this.time = "0";
		this.cadence = new Double(0);
		this.symmetry = new Double(0);

		this.offset = 0;
		this.range = 0.3f;

		this.count = 50;

		graph = new CenterGraph(300,140,300,200,10,10);
		barGraph = new BarGraph(50,345,200,180,3);

		balanceGraph= new BalanceGraph(300,300,400,150);

		//balanceGraph2 = new BalanceGraph2(300,300,400,150);

		balanceGraph2 = new BalanceGraph2(400,300,400,150);

		balanceGraph3 = new BalanceGraph(300,280,400,180);

		pass_time = System.currentTimeMillis();

		gMetrics = new gaitMetrics();

		paint = new Paint();
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
		paint.setTextSize(75);

		paint1 = new Paint();
		paint1.setARGB(255, (int )(Math.random() * 255), (int )(Math.random() * 255), (int )(Math.random() * 255));

		toggle = 1;
		///this.setOnTouchListener(this);
		//       for (int i=1; i< 64; i++) {
		//		  Point point = new Point();
		//		  point.x = 10*i;
		//		  point.y = 5*i;
		//		  points.add(point);
		//       }      

		//        paint.setColor(Color.RED);
		//        paint.setAntiAlias(true);

		invalidate();
	}

	public void setAngle(float roll, float pitch) {
		this.roll = roll;
		this.pitch = pitch;
	}



	public void setGaitEvents(long l_hs_prev, long l_to, long l_hs_curr, long r_hs_prev, long r_to, long r_hs_curr) {
		gMetrics.setParameters(l_hs_prev,l_to,l_hs_curr,r_hs_prev,r_to,r_hs_curr);

	}

	public void setUpdate(Long ti,Integer ca,Integer sym,double offset,double range, boolean display) {

		this.time = ti.toString();
		this.cadence = ca/10000.0;
		this.symmetry = sym/10000.0;

		if(!display) {
			this.cadence = 0.0;
			this.symmetry = offset;
		}

		paint1.setARGB(255, (int )(Math.random() * 255), (int )(Math.random() * 255), (int )(Math.random() * 255));
		paint1.setAntiAlias(true);

		this.range = (float) range;
		this.offset = (float) offset;
	}

	public void setLinAcc(float[] acc) {
		float[] temp = new float[3];
		//acc[0] = 1;
		for(int i=0;i<3;i++) {
			temp[i]=Math.abs(acc[i]);
			if(temp[i]>1) {
				temp[i] = 1;
			}
		}
		this.linAcc = temp;
	}

	public void setToggle(int num) {
		toggle= num;
	}

	@Override
	public void onDraw(Canvas canvas) {

		long time = System.currentTimeMillis();
		/*
		if(time - pass_time>5000) {
			setToggle();
			pass_time = System.currentTimeMillis();
		}*/

		if(time-pass_time>50) {
			count++;
			pass_time = System.currentTimeMillis();
		}

		if(count>120) {
			count = 50;
		}
/*
		if(count<70) {
			paint.setColor(Color.BLUE);
		} else if(count>=70&&count<90) {
			paint.setColor(Color.WHITE);
		} else {
			paint.setColor(Color.RED);
		}
*/
		switch (toggle) {
			case 1:
				balanceGraph3.draw(canvas,this.symmetry.floatValue(),this.offset,this.range);
				break;
			case 2:
				canvas.drawText(this.time, 100, 100, paint);

				Point pt = new Point();

				pt.x = 120;
				pt.y = 280;
				//canvas.drawCircle(pt.x, pt.y, 25, paint1);

				float xper = this.roll;
				//int yper = 0;
				float yper = this.pitch;
				//	Float p = this.pitch;
				graph.draw(xper, yper, canvas);
				//barGraph.draw(canvas,new float[]{0.5f,1f,0.7f});
				barGraph.draw(canvas, this.linAcc);
				break;

			case 3:

			//	Double cadence = gMetrics.getCadence();
				//Log.i("Symmetry", "SymmetryRatio " + symRatio+"cadence "+cadence);
				//log.i("")
				//	canvas.drawText(update, 100, 100, paint);
				canvas.drawText(this.time, 100, 100, paint);
				canvas.drawText(this.cadence.toString(), 100, 200, paint);
				canvas.drawText(this.symmetry.toString(), 100, 300, paint);
				//balanceGraph.draw(canvas,symRatio,0,1);
				//balanceGraph.draw(canvas,this.roll,0,80);
				break;
				//
			case 4:
				//Log.i("Symmetry", "SymmetryRatio " + symRatio+"cadence "+cadence);
				//log.i("")
				//	canvas.drawText(update, 100, 100, paint);

				canvas.drawText(count.toString(), 100, 100, paint);
				balanceGraph.draw(canvas,this.roll,0,80);
				break;
			case 5:
				//float symRatio = (float) gMetrics.getSymmetry();
				// Double cadence = gMetrics.getCadence();
				//Log.i("Symmetry", "SymmetryRatio " + symRatio+"cadence "+cadence);
				//log.i("")
			//	canvas.drawText(update, 100, 100, paint);

				canvas.drawText(this.cadence.toString(), 100, 100, paint);
				balanceGraph.draw(canvas,this.symmetry.floatValue(),this.offset,this.range);
				break;
			default:
				break;
		}

	//	Log.i("acceleration: ", ""+linAcc[0] +" "+linAcc[1]+" "+linAcc[2]);
	//	canvas.drawText(p.toString(),100,250, paint);

		//paint.setARGB(255, (int )(Math.random() * 255), (int )(Math.random() * 255), (int )(Math.random() * 255));

		// tested code for adding custom views to cardscrollview. Works!
		/*for (int j=0; j<5; j++) {
			Point pt = new Point();
			pt.x = (int )(Math.random() * 640 + 1);
			pt.y = (int )(Math.random() * 360 + 1); 
			paint.setARGB(255, (int )(Math.random() * 255), (int )(Math.random() * 255), (int )(Math.random() * 255));
			paint.setAntiAlias(true);

			canvas.drawCircle(pt.x, pt.y, 25, paint);
		}
		
		int i=0;
		for (Point point : points) {
			//canvas.drawARGB(255, (int )(Math.random() * 255), (int )(Math.random() * 255), (int )(Math.random() * 255));
			canvas.drawCircle(point.x, point.y, 5, paints.get(i++));
			//Log.e(TAG, "Painting: "+point);
		}*/
		// Delay
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) { }

		invalidate();
	}

	//    public boolean onTouch(View view, MotionEvent event) {
	//        // if(event.getAction() != MotionEvent.ACTION_DOWN)
	//        // return super.onTouchEvent(event);
	//        Point point = new Point();
	//        point.x = event.getX();
	//        point.y = event.getY();
	//        points.add(point);
	//
	//        Log.d(TAG, "point: " + point);
	//        return true;
	//    }
}

class Point {
	float x, y;

	@Override
	public String toString() {
		return x + ", " + y;
	}
}

class CenterGraph {

	private Paint paintGraph;
	private Paint paintDot;
	private Paint paintSlim;

	private int minx;
	private int miny;

	private int width;
	private int height;

	private int linesx;
	private int linesy;

	private float[] perimeter;

	public CenterGraph(int x, int y, int w, int h, int lx, int ly) {
		paintGraph = new Paint();
		paintGraph = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintGraph.setColor(Color.WHITE);
		paintGraph.setAntiAlias(true);
		paintGraph.setStrokeWidth(5);

		paintSlim = new Paint();
		paintSlim = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintSlim.setColor(Color.WHITE);
		paintSlim.setAntiAlias(true);

		paintDot = new Paint();
		paintDot = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintDot.setColor(Color.RED);
		paintDot.setAntiAlias(true);

		minx = x;
		miny = y;

		width = w;
		height = h;

		linesx=lx;
		linesy=ly;

		perimeter = createPeriPoints();

	}


	public float[] createPeriPoints() {
		float[] points = new float[24];

		points[0] = this.minx;
		points[1] = this.miny;
		points[2] = this.minx;
		points[3] = this.miny+height;
		points[4] = this.minx+width;
		points[5] = this.miny+height;
		points[6] = this.minx+width;
		points[7] = this.miny;


		points[8] = this.minx;
		points[9] = this.miny+height;
		points[10] = this.minx+width;
		points[11] = this.miny+height;
		points[12] = this.minx+width;
		points[13] = this.miny;
		points[14] = this.minx;
		points[15] = this.miny;

		points[16] = (this.minx+width/2);
		points[17] = (this.miny+height);
		points[18] = (this.minx+width/2);
		points[19] = this.miny;

		points[20] = this.minx;
		points[21] =(this.miny+height/2);
		points[22] = this.minx+width;
		points[23] = (this.miny+height/2);

		return points;

	}


	public void draw(float x, float y, Canvas c) {

		float centerx =  (this.minx+width/2) + (x/80)*width/2;
		float centery = (this.miny+height/2) + (y/80)*height/2;

		c.drawLines(perimeter,paintGraph);
		c.drawCircle(centerx, centery, 25, paintDot);

	}
}