package com.multicorn.featureutils;

import org.junit.Test;
import org.opencv.highgui.Highgui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Class FeatureUtilsTest, created by David on 02.06.15.
 *
 * @author David Steiner <david@stonerworx.com>
 */
public class FeatureUtilsTest {

  static {
    System.loadLibrary("opencv_java2411");
    System.loadLibrary("opencv_nonfree");
    System.loadLibrary("opensurf_java");
  }

  @Test
  public void testOpensurf() {
    Image img = getTestImage1();

    List<Keypoint> keypoints = FeatureUtils.detectDescribe(img, FeatureUtils.TYPE_OPENSURF, 5,
                                                           4, 2, 0.0004f);

    assertTrue("keypoints shouldn't be null", keypoints != null);
    assertTrue("it should detect keypoints", keypoints.size() > 0);
    for (Keypoint kp : keypoints) {
      assertEquals("it should compute a descriptor with 64 values", 64, kp.getDescriptor().getSize());
    }

    img.release();
  }

  @Test
  public void testOpenCvsurf() {
    Image img = getTestImage1();

    List<Keypoint> keypoints = FeatureUtils.detectDescribe(img, FeatureUtils.TYPE_OPENCV_SURF, 5,
                                                           4, 2, 0.0004f);

    assertTrue("keypoints shouldn't be null", keypoints != null);
    assertTrue("it should detect keypoints", keypoints.size() > 0);
    for (Keypoint kp : keypoints) {
      assertEquals("it should compute a descriptor with 64 values", 64, kp.getDescriptor().getSize());
    }

    img.release();
  }

  /*
  @Test
  public void testBoofCvsurf() {
    Image img = getTestImage();

    List<Keypoint> keypoints = FeatureUtils.detectDescribe(img, FeatureUtils.TYPE_BOOFCV_SURF, 5,
                                                           4, 2, 0.0004f);

    assertTrue("keypoints shouldn't be null", keypoints != null);
    assertTrue("it should detect keypoints", keypoints.size() > 0);
    for (Keypoint kp : keypoints) {
      assertEquals("it should compute a descriptor with 64 values", 64, kp.getDescriptor().getSize());
    }

    img.release();
  }
  */

  @Test
  public void testBruteForceMatcher() {
    Image img = getTestImage1();

    List<Keypoint> keypoints1 = FeatureUtils.detectDescribe(img, FeatureUtils.TYPE_OPENSURF, 5,
                                                            4, 2, 0.0004f);

    try {
      List<Match> matches = FeatureUtils.getMatches(keypoints1, keypoints1,
                                                    FeatureUtils.MATCH_BRUTE_FORCE,
                                                    FeatureUtils.DISTANCE_EUCLIDEAN);

      assertEquals("all keypoints should match.", keypoints1.size(), matches.size());
    } catch (Exception e) {
      e.printStackTrace();
    }

    img.release();
  }

  @Test
  public void testFlannMatcher() {
    Image img = getTestImage1();

    List<Keypoint> keypoints1 = FeatureUtils.detectDescribe(img, FeatureUtils.TYPE_OPENSURF, 5,
                                                            4, 2, 0.0004f);


    try {
      List<Match> matches = FeatureUtils.getMatches(keypoints1, keypoints1,
                                                    FeatureUtils.MATCH_OPENCV_FLANN,
                                                    FeatureUtils.DISTANCE_EUCLIDEAN);

      assertEquals("all keypoints should match.", keypoints1.size(), matches.size());
    } catch (Exception e) {
      e.printStackTrace();
    }

    img.release();
  }

  private Image getTestImage1() {
    try {
      BufferedImage img = ImageIO.read(getClass().getResource("/test1.jpg"));
      return new Image(img);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  private Image getTestImage2() {
    try {
      BufferedImage img = ImageIO.read(getClass().getResource("/test2.jpg"));
      return new Image(img);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  private Image getTestImage3() {
    try {
      BufferedImage img = ImageIO.read(getClass().getResource("/test3.jpg"));
      return new Image(img);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
