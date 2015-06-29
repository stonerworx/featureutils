package com.multicorn.featureutils;

/**
 * Class Keypoint, created by David on 01.06.15.
 *
 * @author David Steiner <david@stonerworx.com>
 */
public class Keypoint {

  private double x;
  private double y;
  private double angle;
  private double size;
  private Descriptor descriptor;

  public Keypoint(double x, double y, double angle, double size) {
    this.x = x;
    this.y = y;
    this.angle = angle;
    this.size = size;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getAngle() {
    return angle;
  }

  public double getSize() {
    return size;
  }

  public Descriptor getDescriptor() {
    return descriptor;
  }

  public void setDescriptor(Descriptor descriptor) {
    this.descriptor = descriptor;
  }
}
