package com.multicorn.featureutils;

/**
 * Class Match, created by David on 05.06.15.
 *
 * @author David Steiner <david@stonerworx.com>
 */
public class Match {

  private Keypoint keypoint1;
  private Keypoint keypoint2;
  private float distance;

  public Match(Keypoint keypoint1, Keypoint keypoint2, float distance) {
    this.keypoint1 = keypoint1;
    this.keypoint2 = keypoint2;
    this.distance = distance;
  }

  public Keypoint getKeypoint1() {
    return keypoint1;
  }

  public Keypoint getKeypoint2() {
    return keypoint2;
  }

  public float getDistance() {
    return distance;
  }
}
