package com.multicorn.featureutils;

import com.multicorn.opensurf.Opensurf;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;

import java.util.ArrayList;
import java.util.List;

//import boofcv.abst.feature.detdesc.DetectDescribePoint;
//import boofcv.abst.feature.detect.interest.ConfigFastHessian;
//import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
//import boofcv.struct.feature.SurfFeature;
//import boofcv.struct.image.ImageFloat32;

/**
 * Class FeatureUtils, created by David on 31.05.15.
 *
 * @author David Steiner <david@stonerworx.com>
 */
public class FeatureUtils {

  public static final int TYPE_OPENCV_SURF = 0;
  public static final int TYPE_OPENSURF = 1;
  //public static final int TYPE_BOOFCV_SURF = 2;

  public static final int MATCH_BRUTE_FORCE = 0;

  public static final int DISTANCE_EUCLIDEAN = 0;

  public static List<Keypoint> detect(Image image, int type, int octaves, int intervals,
                                      int initSample, float threshold) {
    switch (type) {
      case TYPE_OPENCV_SURF:
        return detectOpencvSurf(image, octaves, intervals, initSample, threshold);
      case TYPE_OPENSURF:
        return detectOpensurf(image, octaves, intervals, initSample, threshold);
      default:
        return null;
    }
  }

  public static void describe(Image image, List<Keypoint> keypoints, int type) {
    switch (type) {
      case TYPE_OPENCV_SURF:
        describeOpencvSurf(image, keypoints);
      case TYPE_OPENSURF:
        describeOpensurf(image, keypoints);
    }
  }

  public static List<Keypoint> detectDescribe(Image image, int type, int octaves, int intervals,
                                              int initSample, float threshold) {
    switch (type) {
      case TYPE_OPENCV_SURF:
        return detectDescribeOpencvSurf(image, octaves, intervals, initSample, threshold);
      case TYPE_OPENSURF:
        return detectDescribeOpensurf(image, octaves, intervals, initSample, threshold);
      //case TYPE_BOOFCV_SURF:
      //  return detectDescribeBoofcvSurf(image, octaves, intervals, initSample, threshold);
      default:
        return null;
    }
  }

  public static List<Match> getMatches(List<Keypoint> keypoints1, List<Keypoint> keypoints2,
                                       int type, int distanceFunction) throws Exception{
    switch (type) {
      case MATCH_BRUTE_FORCE:
        return getMatchesBruteForce(keypoints1, keypoints2, distanceFunction);
      default:
        return null;
    }
  }

  public static float distance(Keypoint keypoint1, Keypoint keypoint2,
                               int distanceFunction) throws Exception{
    List<Double> values1 = keypoint1.getDescriptor().getValues();
    List<Double> values2 = keypoint2.getDescriptor().getValues();
    if (values1.size() != values2.size()) {
      throw new Exception("Desriptor size doesn't match.");
    }
    switch (distanceFunction) {
      case DISTANCE_EUCLIDEAN:
        return distanceEuclidean(values1, values2);
      default:
        return Float.MAX_VALUE;
    }
  }

  /*
   * -----------------------------------------------------------------------------------------------
   * different detect implementations
   * -----------------------------------------------------------------------------------------------
   */

  private static List<Keypoint> detectOpencvSurf(Image image, int octaves, int intervals,
                                                 int initSample, float threshold) {
    MatOfKeyPoint keyPoints = new MatOfKeyPoint();
    FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SURF);
    featureDetector.detect(image.getImageMat(), keyPoints);

    return matOfKeyPointToKeypointList(keyPoints);
  }

  private static List<Keypoint> detectOpensurf(Image image, int octaves, int intervals,
                                               int initSample, float threshold) {
    MatOfKeyPoint keyPoints = Opensurf.detect(image.getImageMat(), octaves, intervals, initSample,
                                              threshold);

    return matOfKeyPointToKeypointList(keyPoints);
  }

  /*
   * -----------------------------------------------------------------------------------------------
   * different describe implementations
   * -----------------------------------------------------------------------------------------------
   */

  private static void describeOpencvSurf(Image image, List<Keypoint> keypoints) {
    Mat descriptors = new Mat();

    DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
    descriptorExtractor.compute(image.getImageMat(), keypointListToMatOfKeyPoint(keypoints),
                                descriptors);

    for (int i = 0; i < keypoints.size(); i++) {
      Keypoint kp = keypoints.get(i);
      Descriptor descriptor = new Descriptor();
      for (int j = 0; j < descriptors.row(i).cols(); j++) {
        descriptor.addValue(descriptors.get(i, j)[0]);
      }
      kp.setDescriptor(descriptor);
    }
  }

  private static void describeOpensurf(Image image, List<Keypoint> keypoints) {
    Mat descriptors = Opensurf.describe(image.getImageMat(), keypointListToMatOfKeyPoint(keypoints),
                                       true);

    for (int i = 0; i < keypoints.size(); i++) {
      Keypoint kp = keypoints.get(i);
      Descriptor descriptor = new Descriptor();
      for (int j = 0; j < descriptors.row(i).cols(); j++) {
        descriptor.addValue(descriptors.get(i, j)[0]);
      }
      kp.setDescriptor(descriptor);
    }
  }

  /*
   * -----------------------------------------------------------------------------------------------
   * different detect + describe implementations
   * -----------------------------------------------------------------------------------------------
   */

  private static List<Keypoint> detectDescribeOpencvSurf(Image image, int octaves, int intervals,
                                                         int initSample, float threshold) {
    List<Keypoint> keypoints = detectOpencvSurf(image, octaves, intervals, initSample, threshold);
    describeOpencvSurf(image, keypoints);

    return keypoints;
  }

  private static List<Keypoint> detectDescribeOpensurf(Image image, int octaves, int intervals,
                                                       int initSample, float threshold) {
    MatOfKeyPoint keyPoints = new MatOfKeyPoint();
    Mat descriptors = new Mat();

    Opensurf.detectDescribe(image.getImageMat(), keyPoints, descriptors, octaves, intervals,
                            initSample, threshold, true);

    List<Keypoint> keypoints = matOfKeyPointToKeypointList(keyPoints);
    for (int i = 0; i < keypoints.size(); i++) {
      Keypoint kp = keypoints.get(i);
      Descriptor descriptor = new Descriptor();
      for (int j = 0; j < descriptors.row(i).cols(); j++) {
        descriptor.addValue(descriptors.get(i, j)[0]);
      }
      kp.setDescriptor(descriptor);
    }

    return keypoints;
  }

  /*
  private static List<Keypoint> detectDescribeBoofcvSurf(Image image, int octaves, int intervals,
                                                         int initSample, float threshold) {
    List<Keypoint> keypoints = new ArrayList<Keypoint>();

    DetectDescribePoint<ImageFloat32,SurfFeature> surf = FactoryDetectDescribe.
        surfStable(new ConfigFastHessian(threshold, 2, 0, initSample, 9, intervals, octaves),
                   null, null, ImageFloat32.class);

    surf.detect(image.getImageFloat32());

    for (int i = 0; i < surf.getNumberOfFeatures(); i++) {
      Keypoint kp = new Keypoint(surf.getLocation(i).getX(), surf.getLocation(i).getY(),
                                 surf.getOrientation(i), surf.getScale(i));
      Descriptor descriptor = new Descriptor();
      for (int j = 0; j < surf.getDescription(i).size(); j++) {
        descriptor.addValue(surf.getDescription(i).getDouble(j));
      }
      kp.setDescriptor(descriptor);

      keypoints.add(kp);
    }

    return keypoints;
  }
  */

  /*
   * -----------------------------------------------------------------------------------------------
   * different matching implementations
   * -----------------------------------------------------------------------------------------------
   */

  //OpenSURF matching function
  private static List<Match> getMatchesBruteForce(List<Keypoint> keypoints1,
                                                  List<Keypoint> keypoints2,
                                                  int distanceFunction) throws Exception{
    float dist, d1, d2;
    Keypoint match;
    List<Match> matches = new ArrayList<Match>();

    for (Keypoint keypoint1 : keypoints1) {

      d1 = d2 = Float.MAX_VALUE;
      match = null;

      for (Keypoint keypoint2 : keypoints2) {
        dist = distance(keypoint1, keypoint2, distanceFunction);

        if(dist < d1) // if this feature matches better than current best
        {
          d2 = d1;
          d1 = dist;
          match = keypoint2;
        }
        else if(dist < d2) // this feature matches better than second best
        {
          d2 = dist;
        }
      }

      // If match has a d1:d2 ratio < 0.65 keypoints are a match
      if(d1 / d2 < 0.65 && match != null)
      {
        matches.add(new Match(keypoint1, match, d1));
      }
    }

    return matches;
  }

  /*
   * -----------------------------------------------------------------------------------------------
   * different distance functions
   * -----------------------------------------------------------------------------------------------
   */

  private static float distanceEuclidean(List<Double> values1, List<Double> values2) {
    float sum = 0.f;
    for(int i = 0; i < values1.size(); ++i) {
      sum += (values1.get(i) -
              values2.get(i)) *
             (values1.get(i) -
              values2.get(i));
    }
    return (float) Math.sqrt(sum);
  }

  /*
   * -----------------------------------------------------------------------------------------------
   * helper / conversion functions
   * -----------------------------------------------------------------------------------------------
   */

  public static List<Keypoint> matOfKeyPointToKeypointList(MatOfKeyPoint matOfKeyPoint) {
    List<Keypoint> keypoints = new ArrayList<Keypoint>();
    for(KeyPoint kp : matOfKeyPoint.toArray()) {
      keypoints.add(new Keypoint(kp.pt.x, kp.pt.y, kp.angle, kp.size));
    }

    return keypoints;
  }

  public static MatOfKeyPoint keypointListToMatOfKeyPoint(List<Keypoint> keypoints) {
    KeyPoint[] keyPointArray = new KeyPoint[keypoints.size()];
    for (int i = 0; i < keypoints.size(); i++) {
      Keypoint kp = keypoints.get(i);
      keyPointArray[i] = new KeyPoint((float) kp.getX(), (float) kp.getY(),
                                      (float) kp.getSize(), (float) kp.getAngle());
    }
    MatOfKeyPoint matOfKeyPoint = new MatOfKeyPoint();
    matOfKeyPoint.fromArray(keyPointArray);

    return matOfKeyPoint;
  }
}
