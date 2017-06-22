package com.example.jessexing.solesoundglassudp;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.MenuUtils;
import com.google.android.glass.view.WindowUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Immersion2DActivity extends Activity {
	private static final String TAG = "Immersion2DActivity";

	TextView mTextView;
	DrawView mDrawView; // 2D
	private GestureDetector mGestureDetector;
	OrientationManager orientationManager;

	DatagramSocket clientSocket;

	private int currStateL;
	private int currStateR;

	private int prevState;

	private long l_hs_prev;
	private long l_to;
	private long l_hs_curr;
	private long r_hs_prev;
	private long r_to;
	private long r_hs_curr;

	private ByteBuffer ti;
	private ByteBuffer ca;
	private ByteBuffer sym;

	private ByteBuffer temp2;

	private boolean runonce;

	private double offset1;
	private double range1;

	private boolean disp;

	private int count;
	private int prev_symmetry;

	private boolean lap_detect;

	private File file;
	private BufferedWriter os;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.currStateL=1;
		this.currStateR=1;

		this.prevState=1;
		l_hs_prev = 0;
		l_to = 0;
		l_hs_curr = 0;
		r_hs_prev = 0;
		r_to = 0;
		r_hs_curr = 0;

		this.prev_symmetry=0;

		this.disp = true;
		this.range1 = 1;
		this.offset1 = 0;

		this.count = 0;

		Date dNow = new Date( );
		SimpleDateFormat ft =
				new SimpleDateFormat ("MMdd-HHmmss");

		this.runonce=true;


		try {
			File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

			Log.i("file path", "result"+path.exists());
			Log.i("file path", "result"+path.toString());
			this.file = new File(path, "glassSave-"+ft.format(dNow)+".txt");

			if(!this.file.exists()){
				this.file.createNewFile();
			}
			Log.i("file path", "result"+file.exists());
			Log.i("file path", "result"+file.toString());

			FileWriter fw = new FileWriter(file);

			os = new BufferedWriter(fw);
			//file = new File(path,"file_new.txt");
			//this.file.createNewFile();


			//os = new FileOutputStream(file);


		} catch(Exception e) {
			Log.i("File",e.toString());
		}


		// 2D Canvas-based drawing
		mDrawView = new DrawView(this);
		setContentView(mDrawView);
		mDrawView.requestFocus();

		mGestureDetector = new GestureDetector(this);

		// Called when the following gestures happen: TAP, LONG_PRESS SWIPE_UP,
		// SWIPE_LEFT, SWIPE_RIGHT, SWIPE_DOWN
		mGestureDetector.setBaseListener(new GestureDetector.BaseListener() {
			@Override
			public boolean onGesture(Gesture gesture) {
				if (gesture == Gesture.SWIPE_DOWN) {
					//finishAffinity();
					return false;
				} else if (gesture == Gesture.LONG_PRESS) {
					Log.v(TAG, "LONG_PRESS");
					openOptionsMenu();
					return true;
				}
				return false;
			}});

		final SensorManager sensorManager =
				(SensorManager) getSystemService(Context.SENSOR_SERVICE);
		orientationManager = new OrientationManager(sensorManager);
		orientationManager.start();
		orientationManager.addOnChangedListener(new OrientationManager.OnChangedListener() {
			@Override
			public void onOrientationChanged(OrientationManager orientationManager) {
				float pitch= orientationManager.getPitch();
				float yaw = orientationManager.getRoll();

				float[] linAcc = orientationManager.getmLinearAcceleration();
				if(linAcc!=null) {
					mDrawView.setLinAcc(linAcc);
				}

				mDrawView.setAngle(yaw,pitch);
			}

			@Override
			public void onLocationChanged(OrientationManager orientationManager) {

			}

			@Override
			public void onAccuracyChanged(OrientationManager orientationManager) {

			}
		});


		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		startListenForUDPBroadcast();
	}

	// Send generic motion events to the gesture detector
	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (mGestureDetector != null) {
			return mGestureDetector.onMotionEvent(event);
		}
		return false;
	}

	/*public void setView(byte[] packet) {
		mDrawView.setUpdate(packet);
		mDrawView.invalidate();

	}*/

	/**
	 * Debounces against state transitions
	 * @param state current state changes
	 * @param time current time
     * @return true if valid state transition, false otherwise
     */

	private boolean bounceStateChange(int state,long time) {
		switch(state) {
			case 1:
				if((time - this.l_hs_curr)>64) {
					return true;
				}
				return false;
			case 2:
				if((time - this.l_to)>64) {
					return true;
				}
				return false;
			case 3:
				if((time- this.r_hs_curr)>64) {
					return true;
				}
				return false;
			case 4:
				if((time - this.r_to)>64) {
					return true;
				}
				return false;
			default:
				return false;
		}
	}

	/**
	 * Debounces against same event changes
	 * @param state current state
	 * @param time current time
     * @return true if valid event change, false otherwise
     */

	private boolean bounceEventChange(int state, long time) {
		switch(state) {
			case 1:
				if((time - this.l_hs_curr)>600) {
					return true;
				}
				return false;
			case 2:
				if((time - this.l_to)>600) {
					return true;
				}
				return false;
			case 3:
				if((time- this.r_hs_curr)>600) {
					return true;
				}
				return false;
			case 4:
				if((time - this.r_to)>600) {
					return true;
				}
				return false;
			default:
				return false;
		}
	}

	private void process(Byte temp) {

		int state = temp.intValue();
		int left = state/10;
		int right = state%10;

		long time = System.currentTimeMillis();

		//left hs
		if(left!=currStateL&&currStateL==3) {
			//if(bounceEventChange(1,time)){//&&bounceStateChange(prevState,time)) {
				prevState = 1;
				l_hs_prev = l_hs_curr;
				l_hs_curr = time;
			//}
		//left to
		} else if (left!=currStateL&&left==3) {
			//if(bounceEventChange(2,time)){//&&bounceStateChange(prevState,time)) {
				prevState = 2;
				l_to = time;
			//}
		}

		//right hs
		if(right!=currStateR&&currStateR==3) {
			//if(bounceEventChange(3,time)){//&&bounceStateChange(prevState,time)) {
				prevState = 3;
				r_hs_prev = r_hs_curr;
				r_hs_curr = time;
			//}
		// right to
		} else if (right!=currStateR&&right==3) {
			//if(bounceEventChange(4,time)){//&&bounceStateChange(prevState,time)) {
				prevState = 4;
				r_to = time;
			//}
		}
		currStateL = left;
		currStateR = right;
		//Log.i("Process right", r_hs_prev+ " "+r_hs_curr+ " "+r_to);
		//Log.i("Process left", l_hs_prev+ " "+l_hs_curr+ " "+l_to);
	}

	private void update() {

		mDrawView.setGaitEvents(l_hs_prev, l_to, l_hs_curr, r_hs_prev, r_to, r_hs_curr);
	}

	// Define the Handler that receives messages from the thread and update the progress
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			Log.i("handler", ""+ msg.obj.toString());

			ByteBuffer b = (ByteBuffer) msg.obj;

			byte[] buffer = b.array();

			byte[] timestamp = new byte[8];

			byte[] temp = new byte[8];

			timestamp[0] = b.get(10);
			timestamp[1] = b.get(9);
			timestamp[2] = b.get(8);
			timestamp[3] = b.get(7);
			timestamp[4] = b.get(6);
			timestamp[5] = b.get(5);
			timestamp[6] = b.get(4);
			timestamp[7] = b.get(3);

			byte[] cadence = new byte[4];

			byte[] symmetry = new byte[4];

			cadence[0] = b.get(14);
			cadence[1] = b.get(13);
			cadence[2] = b.get(12);
			cadence[3] = b.get(11);

			symmetry[0] = b.get(18);
			symmetry[1] = b.get(17);
			symmetry[2] = b.get(16);
			symmetry[3] = b.get(15);

			ti = ByteBuffer.wrap(timestamp);

			ti.order(ByteOrder.LITTLE_ENDIAN);

			ca = ByteBuffer.wrap(cadence);

			ca.order(ByteOrder.LITTLE_ENDIAN);

			sym = ByteBuffer.wrap(symmetry);

			sym.order(ByteOrder.LITTLE_ENDIAN);

			String save = "";
			for(int i=1;i<=6;i++) {
				temp[0] = b.get(i*4+18);
				temp[1] = b.get(i*4+17);
				temp[2] = b.get(i*4+16);
				temp[3] = b.get(i*4+15);
				temp[4] = 0;
				temp[5] = 0;
				temp[6] = 0;
				temp[7] = 0;

				temp2 = ByteBuffer.wrap(temp);

				temp2.order(ByteOrder.LITTLE_ENDIAN);
				save+=temp2.getLong()+ " ";
			}

			save+="\n";
			try {
				os.write(save);

			} catch(Exception e) {
				Log.i("File","File not found exception");
			}

			int symmetry1 = sym.getInt();
			if(prev_symmetry!= symmetry1) {
				count++;
				prev_symmetry = symmetry1;

			}


			if(runonce) {
				temp[0] = b.get(46);
				temp[1] = b.get(45);
				temp[2] = b.get(44);
				temp[3] = b.get(43);
				temp[4] = 0;
				temp[5] = 0;
				temp[6] = 0;
				temp[7] = 0;

				temp2 = ByteBuffer.wrap(temp);

				temp2.order(ByteOrder.LITTLE_ENDIAN);

				long offset= temp2.getLong();

				offset1 = offset/1000.0;

				temp[0] = b.get(50);
				temp[1] = b.get(49);
				temp[2] = b.get(48);
				temp[3] = b.get(47);
				temp[4] = 0;
				temp[5] = 0;
				temp[6] = 0;
				temp[7] = 0;

				temp2 = ByteBuffer.wrap(temp);

				temp2.order(ByteOrder.LITTLE_ENDIAN);

				long range = temp2.getLong();
				Log.i("range: ",""+ range);

				range1 = range/1000.0;

				runonce = false;
			}
			Log.i("offset range ", ""+offset1+" "+range1);

			temp[0] = b.get(54);
			temp[1] = b.get(53);
			temp[2] = b.get(52);
			temp[3] = b.get(51);
			temp[4] = 0;
			temp[5] = 0;
			temp[6] = 0;
			temp[7] = 0;

			temp2 = ByteBuffer.wrap(temp);

			temp2.order(ByteOrder.LITTLE_ENDIAN);

			long lap = temp2.getLong();

			if(lap==1) {
				disp = false;
				count = 0;
			}

			if(count>1) {
				disp=true;
			}

			mDrawView.setUpdate(ti.getLong(),ca.getInt(),symmetry1,offset1,range1,disp);

		}
	};


	Thread UDPBroadcastThread;

	void startListenForUDPBroadcast() {
		UDPBroadcastThread = new Thread(new Runnable() {
			DatagramPacket receivePacket;
			public void run() {
				try {
					Integer port = 3456;
					byte[] receiveData = new byte[59];
					clientSocket = new DatagramSocket(port);
					receivePacket = new DatagramPacket(receiveData, receiveData.length);

					//ByteBuffer bb = ByteBuffer.wrap(receiveData);
					//BigInteger unsigned = new BigInteger(1,receiveData);
					//int modifiedSentence = new Integer(receivePacket.getData());
					while(true) {
						clientSocket.receive(receivePacket);

						byte[] packet = receivePacket.getData();

						Message msg = handler.obtainMessage();

						ByteBuffer wrapped = ByteBuffer.wrap(packet); // big-endian by default
						// num = wrapped; // 1
						msg.obj = wrapped;
						handler.sendMessage(msg);
						//Thread.sleep(500);
						//Log.i("UDP", receivePacket.toString()+"size: " +packet.length);
					}

				} catch (Exception e) {
					Log.i("UDP", "no longer listening for UDP broadcasts cause of error " + e.getMessage());
				}
			}
		});
		UDPBroadcastThread.start();
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.i("Stop","I am here");
		clientSocket.close();
		orientationManager.stop();

		try {
			os.close();
			Log.i("File",""+this.file.exists());
		} catch(IOException e) {
			Log.i("IO exception","Cannot close output stream");
		}
		//UDPBroadcastThread.stop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.juststop, menu);

		MenuItem item2 = menu.add(0, R.id.stop+1, 0, "Graph1");
		MenuItem item3 = menu.add(0, R.id.stop+2, 0, "Graph2");
		MenuItem item4 = menu.add(0, R.id.stop+3, 0, "Graph3");
		MenuItem item5 = menu.add(0, R.id.stop+4, 0, "Graph4");


		MenuUtils.setDescription(item2, R.string.graph1);
		MenuUtils.setDescription(item3, R.string.graph2);
		MenuUtils.setDescription(item4, R.string.graph3);

		MenuUtils.setDescription(item5, R.string.graph4);


		MenuUtils.setInitialMenuItem(menu, item2);

		getWindow().addFlags(WindowUtils.FLAG_DISABLE_HEAD_GESTURES);
		return true;
	} 

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.stop:
			finish(); 
			return true;

		case R.id.stop+1:
			mDrawView.setToggle(1);
			return true;

		case R.id.stop+2:
			mDrawView.setToggle(2);
			return true;

		case R.id.stop+3:
			mDrawView.setToggle(3);
			return true;

		case R.id.stop+4:
			mDrawView.setToggle(4);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}    	
}

