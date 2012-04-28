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

import java.io.Serializable;
import java.util.List;

import android.graphics.Paint;
import android.util.Log;

import com.ninetwozero.assignment4.misc.Constants;

public class Ball implements Serializable {

    // Constants
    public static final int DEFAULT_RADIUS = 10;
    
    public static final double DEFAULT_SPEED_X = 3.0;
    public static final double DEFAULT_SPEED_Y = 3.0;
    
    public static final int DIRECTION_START = -1;
    public static final int DIRECTION_UP = 0;
    public static final int DIRECTION_RIGHT = 1;
    public static final int DIRECTION_DOWN = 2;
    public static final int DIRECTION_LEFT = 3;
    public static final int DIRECTION_NONE = 4;
    
    public static final int TYPE_BLACK = 0;
    public static final int TYPE_GREEN = 1;
    public static final int TYPE_RED = 2;
    public static final int TYPE_BLUE = 3;
    public static final int TYPE_YELLOW = 4;

    public static final Paint PAINT_BLACK = new Paint();
    public static final Paint PAINT_GREEN = new Paint();
    public static final Paint PAINT_RED = new Paint();
    public static final Paint PAINT_BLUE = new Paint();
    public static final Paint PAINT_YELLOW = new Paint();
    
    private static final long serialVersionUID = 4943234950875259202L;
    
    // Attributes
    private int radius, type, positionX, positionY;
    private int directionX, directionY;
    private double speedX, speedY;
    private boolean enabled;

    /*
     * Constructor for the class |Ball|
     * @param int Width for the ball
     * @param int Height for the ball
     */
    public Ball(int x, int y, int t) {

        // The direction
        directionX = (Math.random() < 0.5) ? Ball.DIRECTION_LEFT : Ball.DIRECTION_RIGHT;
        directionY = (Math.random() < 0.5) ? Ball.DIRECTION_UP : Ball.DIRECTION_DOWN;
        
        // Position
        positionX = x;
        positionY = y;
        
        // Type
        type = t;
        
        // Radius
        radius = DEFAULT_RADIUS;
        
        // The speed
        speedX = DEFAULT_SPEED_X;
        speedY = DEFAULT_SPEED_Y;

    }

    // Getters
    /*
     * Method to return the x position for the ball
     * @returns int  x position for the ball
     */
    
    public int getPositionX() {
        
        return positionX;
    }
    /*
     * Method to return the y position for the ball
     * @returns int y position for the ball
     */
    
    public int getPositionY() {
        
        return positionY;
    }

    /*
     * Method to return the radius
     * @returns int radius
     */

    public int getRadius() {
        
        return radius;
    }

    /*
     * Method to return the type of the ball
     * @returns int Type corresponding to TYPE_*
     */
    
    public int getType() {
        
        return type;
    }

    /*
     * Method to return the direction on the X-axis
     * @returns int Direction corresponding to DIRECTION_*
     */

    public int getDirectionX() {

        return directionX;

    }

    /*
     * Method to return the direction on the Y-axis
     * @returns int Direction corresponding to DIRECTION_*
     */

    public int getDirectionY() {

        return directionY;

    }

    /*
     * Method to return the speed on the X-axis
     * @returns double Speed on the x-axis
     */

    public double getSpeedX() {

        return speedX;
    }

    
    /*
     * Method to return the balls x
     * @returns int x of the ball
     */
    
    public void setPositionX(int x) {
        
        positionX = x;
        
    }
    
    /*
     * Method to return the balls x
     * @returns int x of the ball
     */
    
    public void setPositionY(int y) {
        
        positionY = y;
        
    }
   
    /*
     * Method to set the radius
     * @returns int radius the ball
     */
    
    public void setRadius(int r) {
        
        radius = r;
        
    }
   

    /*
     * Method to set the direction on the X-axis
     * @param int Direction to be set
     */

    public void setDirectionX(int d) {

        directionX = d;

    }

    /*
     * Method to set the direction on the Y-axis
     * @param int Direction to be set
     */

    public void setDirectionY(int d) {

        directionY = d;

    }

    /*
     * Method to set the speed on the X-axis
     * @param double Speed to be set
     */

    public void setSpeedX(double s) {

        speedX = s;

    }

    /*
     * Method to set the speed on the Y-axis
     * @param double Speed to be set
     */

    public void setSpeedY(double s) {

        speedY = s;

    }

    // Misc methods
    /*
     * Method to toggle the direction on the X-axis
     */

    public void toggleDirectionX() {

        directionX = (directionX == DIRECTION_LEFT) ? DIRECTION_RIGHT : DIRECTION_LEFT;

    }

    /*
     * Method to toggle the direction on the Y-axis
     */
    public void toggleDirectionY() {

        directionY = (directionY == DIRECTION_UP) ? DIRECTION_DOWN : DIRECTION_UP;

    }
    
    /* 
     * Method to get the paint 
     */
    public Paint getPaint() {
        
        switch( type ) {

            case TYPE_BLACK:
                return PAINT_BLACK;
                
            case TYPE_BLUE:
                return PAINT_BLUE;
            
            case TYPE_RED:
                return PAINT_RED;
            
            case TYPE_GREEN:
                return PAINT_GREEN;
            
            case TYPE_YELLOW:
                return PAINT_YELLOW;
            
            default:
                return PAINT_BLACK;
            
        }
        
    }
    /*
     * Method to actually move the ball
     */
    public void move(int xMax, int yMax) {
        
        // We have a slight chance of changing direction
        // Let's see what's up on the vertical axel
        if (directionX == DIRECTION_LEFT) {

            if( ( (positionX-radius) - speedX ) < 0 ) {
                
                positionX = (int) -((positionX-radius)- speedX );
                directionX = DIRECTION_RIGHT;
                
            } else {
                
                positionX -= speedX;

            }
            
        } else {
 
            if( ((positionX+radius) + speedX ) > xMax ) {
                
                positionX = (int) ((xMax * 2) - (positionX+radius + speedX ));
                directionX = DIRECTION_LEFT;
           
            } else {
                
                positionX += speedX;

            }
        }

        // Let's see what's up on the horizontal axel
        if (directionY == DIRECTION_UP) {

            if( ( (positionY-radius) - speedY ) < 0 ) {
                
                positionY = (int) -((positionY-radius)- speedY );
                directionY = DIRECTION_DOWN;
                
            } else {
                
                positionY -= speedY;
           
            }

        } else {

            if( ((positionY+radius) + speedY ) > yMax ) {
                
                positionY = (int) ((yMax * 2) - (positionY+radius + speedY ));
                directionY = DIRECTION_UP;
            
            } else {

                positionY += speedY;

            }
        }
        
    }
    
    /* 
     * Method to check for intersections versus a List of LineData
     * @param List<LineData> List of Lines to compare to
     * @return int Number of intersections
     */
    
    public int isIntersecting(List<LineData> lines) {
        
        // Init
        int counter = 0;
        
        //Iterate over the lines
        for( LineData l : lines ) {

            if( l.getStartX() > l.getEndX() ) {
            
                if( positionX <= l.getEndX() && positionX >= l.getStartX() ) {
                    
                    counter++;
                    
                }
                
            } else {
                
                if( positionX <= l.getStartX() && positionX >= l.getEndX() ) {
                    
                    counter++;
                    
                }
                
            }
            
        }
        
        // Return the counter
        return counter;
        
    }

    /*
     * Overridden toString() method for easier output
     */
    @Override
    public String toString() {

        return positionX + "," + positionY;
    }
    
    static {

        PAINT_BLACK.setARGB(255, 0, 0, 0);
        PAINT_BLUE.setARGB(255, 0, 255, 255);
        PAINT_RED.setARGB(255, 255, 0, 255);
        PAINT_YELLOW.setARGB(255, 255, 255, 0);
        PAINT_GREEN.setARGB(255, 0, 255, 0);
        
    }
}
