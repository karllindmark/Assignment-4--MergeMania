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

public class LineData {

    // Constants
    public static final double DEFAULT_WIDTH = 5;

    // Attributes
    private double width, height;
    
    // Positions
    private double startX, startY, endX, endY;

    // Constructs
    public LineData(double x1, double y1, double x2, double y2) {

        //Set the positons
        startX = x1;
        startY = y1;
        endX = x2;
        endY = y2;
    }

    // Getters
    /*
     * Method to return the startX position for the line
     * @returns double startX position for the line
     */
    public double getStartX() {

        return startX;

    }

    /*
     * Method to return the startY position for the line
     * @returns double startY position for the line
     */

    public double getStartY() {

        return startY;

    }

    /*
     * Method to return the endX position for the line
     * @returns double endX position for the line
     */

    public double getEndX() {

        return endX;

    }

    /*
     * Method to return the endY position for the line
     * @returns double endY position for the line
     */

    public double getEndY() {

        return endY;

    }

    /*
     * Method to return the width of the line
     * @returns double The width of the line
     */

    public double getWidth() {

        return endX - startX;
    }

    /*
     * Method to return the height of the line
     * @returns double The height of the line
     */

    public double getHeight() {

        return endY - startY;
    }

    // Setters
    /*
     * Method to set the startX position of the ball
     * @param double Position to be set
     */

    public void setStartX(double l) {

        startX = l;
        endX = l + width;

    }

    /*
     * Method to set the startY position of the ball
     * @param double Position to be set
     */

    public void setStartY(double t) {

        startY = t;
        endY = t + height;

    }

    /*
     * Method to set the endX position of the ball
     * @param double Position to be set
     */

    public void setEndX(double r) {

        startX = r - width;
        endX = r;

    }

    /*
     * Method to set the endY position of the ball
     * @param double Position to be set
     */

    public void setEndY(double b) {

        startY = b - height;
        endY = b;

    }
    
    /*
     * Method to set the dimensions of the line
     * @param double The width
     * @param double The height
     */

    public void setDimensions(double w, double h) {

        width = w;
        height = h;

    }
    
    /* 
     * Overridden toString()-method
     * @return String The string
     * 
     */

    @Override
    public String toString() {
        
        return startX + "," + startY + ";" + endX + "," + endY;
        
    }
}
