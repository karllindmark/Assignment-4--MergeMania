/*
    This file is part of Assignment 4: Spheres

    BF3 Battlelog is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    BF3 Battlelog is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

 */

package com.ninetwozero.assignment4.datatypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.ninetwozero.assignment4.GameOverActivity;
import com.ninetwozero.assignment4.R;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    // Context & elements
    private Context context;
    private Handler handler;
    private TextView textMessage;
    private View viewOverlay;

    // Event-related
    private final int TOUCHPOINT_X = 0;
    private final int TOUCHPOINT_Y = 1;

    // Defaults
    private final int DEFAULT_WIDTH = 540;
    private final int DEFAULT_HEIGHT = 960;

    // Touch related
    private double[] touchPoints;
    private double[] touchPointsPrevious;
    private List<Point> points;
    private Path path;
    private int stateTouch;

    // Sphere-related
    private int numSpheres;
    private List<Ball> balls;

    // Scaling related
    private double scaleModifierX;
    private double scaleModifierY;
    private int scaledSpacing;
    private int scaledRadiusBall;

    // Scoring related
    private long timeStart;
    private int viewMaxWidth;
    private int viewMaxHeight;

    // Sound
    private MediaPlayer soundLine;
    private MediaPlayer soundMerge;

    private Map<Integer, Integer> ballMap;

    /** The thread that actually draws the animation */
    private GameThread thread;

    /*
     * Constructor for the GameSurfaceView
     * @param Context The context that it's been created in
     * @param AttributeSet The attributes
     */

    public GameSurfaceView(Context c, AttributeSet attrs) {
        super(c, attrs);

        // register our interest in hearing about changes to our surface
        context = c;
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        // Let's get the touch points
        touchPoints = new double[2];
        points = new CopyOnWriteArrayList<Point>();
        balls = new ArrayList<Ball>();

        // create thread only; it's started in surfaceCreated()
        thread = new GameThread(holder, c, handler = new Handler() {
            @Override
            public void handleMessage(Message m) {

                textMessage.setVisibility(m.getData().getInt("viz"));
                textMessage.setText(m.getData().getString("text"));
                viewOverlay.setVisibility(m.getData().getInt("overlay", View.INVISIBLE));

            }
        });

        setFocusable(true); // make sure we get key events

        // Init the paddle
        touchPoints = new double[2];
        touchPointsPrevious = new double[2];

        // Setup the width/height
        viewMaxWidth = 0;
        viewMaxHeight = 0;

        // Set the MediaPlayer
        soundLine = MediaPlayer.create(c, R.raw.line);
        soundMerge = MediaPlayer.create(c, R.raw.merge);

        // Start them off to remove the lag
        soundLine.start();
        soundMerge.start();

    }

    /*
     * Method to init the values
     */
    public void initValues() {

        // Init the scaled values
        scaleModifierX = ((double) viewMaxWidth) / DEFAULT_WIDTH;
        scaleModifierY = ((double) viewMaxHeight) / DEFAULT_HEIGHT;
        scaledRadiusBall = (int) (((Ball.DEFAULT_RADIUS * scaleModifierX) + (Ball.DEFAULT_RADIUS * scaleModifierY)) / 2);

        // Determine if screen is large enough for two players
        numSpheres = 20;

        // Data
        ballMap = new HashMap<Integer, Integer>();

        // Init the path
        path = new Path();

    }

    /*
     * Method to init the Spheres
     */
    public void initSpheres(boolean restart) {

        // Are there any blocks?
        if (!restart) {
            return;
        }

        // We need a random generator
        Random generator = new Random();

        // No room for balls
        balls.clear();

        // Iterate and create!
        for (int count = 0; count < numSpheres; count++) {

            // Randomize an id
            int typeId = generator.nextInt(5);

            // Use it on the ball
            balls.add(new Ball((int) (viewMaxWidth * Math.random()), (int) (viewMaxHeight * Math
                    .random()), typeId));

            // Also, put it in the ballMap to keep track
            if (ballMap.containsKey(typeId)) {

                ballMap.put(typeId, ballMap.get(typeId) + 1);

            } else {

                ballMap.put(typeId, 1);

            }

        }

    }

    /*
     * Method to return the GameThread
     * @return GameThread The GameThread
     */
    public GameThread getThread() {
        return thread;
    }

    /*
     * Overridden method to pause the thread upon losing focus
     * @param boolean Whether or not the window has focus
     */
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus) {
            thread.pause();
        }
    }

    /*
     * Method to set the TextView for the message
     * @param TextView The referenced TextView
     */
    public void setTextMessage(TextView tv) {
        textMessage = tv;
    }

    /*
     * Method to set the View for the overlay
     * @param TextView The referenced View
     */
    public void setViewOverlay(View v) {

        viewOverlay = v;

    }

    /*
     * Callback for when the surface (Canvas) is changed
     * @param SurfaceHolder The surface holder
     * @param int The format
     * @param int The new width
     * @param int The new height
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        thread.setViewSize(width, height);
    }

    /*
     * Callback for when the surface has been created
     * @param SurfaceHolder The surface holder
     */
    public void surfaceCreated(SurfaceHolder holder) {

        // Let's calculate the dimensions
        Rect surfaceFrame = holder.getSurfaceFrame();
        viewMaxWidth = surfaceFrame.right - surfaceFrame.left;
        viewMaxHeight = surfaceFrame.bottom - surfaceFrame.top;

        // Let's generate teh scaled values
        initValues();

        // Generate the blocks
        initSpheres(true);

        // Start the thread
        if (thread.getState() == Thread.State.TERMINATED) {

            thread = new GameThread(holder, context, handler);
            thread.setRunning(true);
            thread.start();
            thread.doStart();

        } else {

            if (!thread.isRunning()) {

                thread.setRunning(true);
                thread.start();

            }
        }

    }

    /*
     * Callback for when the surface (Canvas) is destroyed
     * @param SurfaceHolder The surface holder
     */
    public void surfaceDestroyed(SurfaceHolder holder) {

        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    /*
     * Callback for when an TouchEvent occurs
     * @param MotionEvent The touch event
     * @return boolean A flag
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // Get the action
        int action = event.getAction();
        stateTouch = action;

        // Save the "point of impact"
        touchPointsPrevious = touchPoints.clone();
        touchPoints[TOUCHPOINT_X] = event.getX();
        touchPoints[TOUCHPOINT_Y] = event.getY();

        // Act upon the action
        if (action == MotionEvent.ACTION_DOWN) {

            // Init a line
            points.add(new Point((int) touchPoints[TOUCHPOINT_X], (int) touchPoints[TOUCHPOINT_Y]));
            path.moveTo((int) touchPoints[TOUCHPOINT_X], (int) touchPoints[TOUCHPOINT_Y]);

        } else if (action == MotionEvent.ACTION_MOVE) {

            // We need to validate that they don't cross over
            Point last = new Point((int) touchPointsPrevious[TOUCHPOINT_X],
                    (int) touchPointsPrevious[TOUCHPOINT_Y]);

            // Add the points (index = 1)
            points.add(last);
            path.lineTo(last.x, last.y);

        } else if (action == MotionEvent.ACTION_UP) {

            // Let's make that sound ring!
            soundLine.start();

            // We need to close off the path
            if (points.size() > 0) {

                Point point = points.get(0);
                points.add(new Point(point.x, point.y));
                path.close();

            }

        }

        return true;
    }

    public class GameThread extends Thread {

        // Game states
        public static final int STATE_READY = 0;
        public static final int STATE_RUNNING = 1;
        public static final int STATE_PAUSE = 2;
        public static final int STATE_LOSE = 3;
        public static final int STATE_WIN = 4;

        private Handler mHandler;
        private int mMode;
        private boolean running;

        /** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder;

        // Just a lil' something
        private Rect rectClear;

        // Paint
        private Paint paintReset;
        private Paint paintBackground;
        private Paint paintLine;

        /*
         * Construct for the GameThread
         * @param SurfaceHolder The surface holder
         * @param Context The context from which it has been called
         * @param Handler The handler to be used
         */
        public GameThread(SurfaceHolder surfaceHolder, Context c,
                Handler handler) {

            // First of all - let's grab the important stuff
            mSurfaceHolder = surfaceHolder;
            mHandler = handler;
            context = c;

            // Init the paint
            paintReset = new Paint();
            paintBackground = new Paint();
            paintLine = new Paint();

            // Set the background color
            paintReset.setARGB(255, 255, 255, 255);
            paintBackground.setARGB(16, 255, 255, 255);
            paintLine.setARGB(255, 0, 0, 0);

            // Set it as stroke
            paintLine.setStyle(Paint.Style.STROKE);

            // Misc
            rectClear = new Rect();

        }

        /**
         * Method to start the game and init the ball
         */
        public void doStart() {
            synchronized (mSurfaceHolder) {

                // Set the direction
                if (mMode != STATE_PAUSE) {

                    // Was the game paused?

                }

                // Let's run!
                setState(STATE_RUNNING);
            }
        }

        /**
         * Method to pause the "game physics"
         */
        public void pause() {
            synchronized (mSurfaceHolder) {
                if (mMode == STATE_RUNNING)
                    setState(STATE_PAUSE);
            }
        }

        /*
         * Callback for when state is to be restored
         * @param Bundle The savedInstanceState
         */
        public synchronized void restoreState(Bundle savedState) {
            synchronized (mSurfaceHolder) {
                setState(STATE_PAUSE);

            }
        }

        /*
         * Callback for when the thread starts running
         */
        @Override
        public void run() {
            while (running) {
                Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (mMode == STATE_RUNNING) {
                            calculateGameplay();
                        }
                        doDraw(c);
                    }
                } finally {

                    // Make sure we don't leave it locked
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

        /*
         * Callback for when the state is to be saved
         * @param Bundle The bundle to save everything in
         * @return Bundle The bundle that everything is stored in
         */
        public Bundle saveState(Bundle map) {

            return map;
        }

        /*
         * Method to set the running flag
         * @param boolean Whether or not it's running
         */
        public void setRunning(boolean b) {
            running = b;
        }

        /*
         * Method to set the state flag
         * @param int The new state
         */

        public void setState(int mode) {

            synchronized (mSurfaceHolder) {

                // Init
                mMode = mode;
                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                String str = "";
                int visibilityOverlay = View.GONE;

                if (mMode == STATE_RUNNING) {

                    timeStart = System.currentTimeMillis();
                    visibilityOverlay = View.GONE;

                } else if (mMode == STATE_WIN) {

                    // Calculate how long time it took
                    double timeElapsed = (System.currentTimeMillis() - timeStart) / 1000.0;

                    // Go the the end
                    context.startActivity(new Intent(context, GameOverActivity.class).putExtra(
                            "time", timeElapsed));
                    ((Activity) context).finish();

                } else if (mMode == STATE_PAUSE) {

                    str = "PAUSED";
                    running = false;

                } else {

                    str = "LET'S BOUNCE";
                    visibilityOverlay = View.VISIBLE;

                }
                b.putString("text", str);
                b.putInt("viz", !str.equals("") ? View.VISIBLE : View.GONE);
                b.putInt("overlay", visibilityOverlay);
                msg.setData(b);
                mHandler.sendMessage(msg);

            }

        }

        /*
         * Method to set size of the view in terms of attributes
         * @param int The width
         * @param int The height
         */
        public void setViewSize(int width, int height) {

            synchronized (mSurfaceHolder) {

                // Set the sizes
                viewMaxWidth = width;
                viewMaxHeight = height;

                // Init the rect
                rectClear.set(0, 0, viewMaxWidth, viewMaxHeight);

            }

        }

        /*
         * Method to set the state as STATE_RUNNING after being STATE_PAUSE
         */
        public void unpause() {
            setState(STATE_RUNNING);
        }

        /*
         * Method that handles the actual "drawing"
         * @param Canvas The canvas to draw on
         */
        private void doDraw(Canvas canvas) {

            // If we ain't got nothing to draw on, we just exit
            if (canvas == null) {
                return;
            }

            /* BG */
            canvas.drawRect(rectClear, paintReset);
            canvas.drawRect(rectClear, paintBackground);

            /* Draw lines. */
            canvas.drawPath(path, paintLine);

            /* Draw balls */
            for (Ball ball : balls) {

                if (ball.isEnabled()) {

                    canvas.drawCircle(ball.getPositionX(), ball.getPositionY(), ball.getRadius(),
                            ball.getPaint());

                }

            }

        }

        /*
         * Method that does the hard work, ie calculating what to do
         */
        private void calculateGameplay() {

            // Iterate over the balls
            for (Ball ball : balls) {

                // Should we turn around X-wise?
                if (Math.random() > 0.99) {

                    ball.toggleDirectionX();

                }

                // Should we turn around Y-wise?
                if (Math.random() > 0.99) {

                    ball.toggleDirectionY();

                }

                // Do the actual move
                ball.move(0, 0, viewMaxWidth, viewMaxHeight);

            }

            // Let's handle the balls
            if (stateTouch == MotionEvent.ACTION_UP) {

                if (balls.size() > 0) {

                    // Create the Polygon & init an array
                    Polygon polygon = new Polygon(points);
                    List<Ball> selectedBalls = new ArrayList<Ball>();

                    // Init the vital variables
                    int foundType = -1;
                    int counterSame = 0;

                    // First thing's first - we need to check if we have any
                    // intersections
                    for (Ball ball : balls) {

                        if (polygon.contains(new Point(ball.getPositionX(), ball.getPositionY()))) {

                            if (foundType == -1) {

                                foundType = ball.getType();
                                counterSame = 1;
                                selectedBalls.add(ball);

                            } else if (foundType == ball.getType()) {

                                counterSame++;
                                selectedBalls.add(ball);

                            } else if (foundType != ball.getType()) {

                                counterSame = 0;
                                break;

                            }

                        }

                    }

                    // Let's validate the results
                    if (counterSame > 1) {

                        // Init
                        int size = 0;
                        int xTot = 0, yTot = 0;
                        int numSelected = selectedBalls.size();

                        // Merge sound!
                        soundMerge.start();

                        // Iterate over the balls, removing them as needed
                        for (int i = 0; i < numSelected; i++) {

                            // Get the ball
                            Ball b = selectedBalls.get(i);

                            // Get the data from it
                            size += b.getSize();
                            xTot += b.getPositionX();
                            yTot += b.getPositionY();

                            // Remove the ball
                            balls.remove(b);

                        }

                        // We need to add a bigger ball if there's >0 left
                        int numLeft = ballMap.get(foundType);

                        // Kids, don't try this at home.
                        if ((numLeft - (counterSame - 1)) == 1) {

                            setState(STATE_WIN);

                        } else {

                            // Deincrement the counter
                            ballMap.put(foundType, numLeft - (counterSame - 1));

                            // Create a new ball
                            Ball b = new Ball(xTot / numSelected, yTot / numSelected, foundType);
                            b.setSize(size);
                            b.setRadius(Ball.DEFAULT_RADIUS * size);
                            balls.add(b);

                        }

                    }

                    points.clear();
                    path.reset();
                    stateTouch = MotionEvent.ACTION_CANCEL;

                }

            }

        }

        /*
         * Method to get the running flag
         * @return boolean Whether or not it's running
         */
        public boolean isRunning() {

            return running;

        }
    }
}
