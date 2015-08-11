package com.multicorn.featureutils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

//import boofcv.core.image.ConvertBufferedImage;
//import boofcv.struct.image.ImageFloat32;



/**
 * Class Image, created by David on 02.06.15.
 *
 * @author David Steiner <david@stonerworx.com>
 */
public class Image {

  protected int height;
  protected int width;

  protected Mat imageMat;
  //protected ImageFloat32 imageFloat32;

  public Image() {}

  public Image(Mat image) {
    Size size = image.size();
    this.height = (int) size.height;
    this.width = (int) size.width;
    imageMat = image;

    /*
    //convert Mat to BufferedImage
    BufferedImage bufferedImage;
    byte[] byteArray = new byte[(int) (imageMat.total() * imageMat.channels())];
    imageMat.get(0, 0, byteArray);
    int type;
    if(imageMat.channels() == 1) {
      type = BufferedImage.TYPE_BYTE_GRAY;
    } else {
      type = BufferedImage.TYPE_3BYTE_BGR;
    }
    bufferedImage = new BufferedImage(getWidth(), getHeight(), type);
    bufferedImage.getRaster().setDataElements(0, 0, getWidth(), getHeight(), byteArray);

    imageFloat32 = new ImageFloat32(bufferedImage.getWidth(), bufferedImage.getHeight());
    ConvertBufferedImage.convertFrom(bufferedImage, imageFloat32);

    bufferedImage.flush();
    */
  }

  public Image(BufferedImage image) {
    height = image.getHeight();
    width = image.getWidth();

    int type = CvType.CV_8UC1;
    if (image.getType() == BufferedImage.TYPE_3BYTE_BGR) {
      type = CvType.CV_8UC3;
    }

    imageMat = new Mat(image.getHeight(), image.getWidth(), type);
    byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    imageMat.put(0, 0, pixels);

    /*
    imageFloat32 = new ImageFloat32(image.getWidth(), image.getHeight());
    ConvertBufferedImage.convertFrom(image, imageFloat32);
    */

    image.flush();
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public Mat getImageMat() {
    return imageMat;
  }

  /*
  public ImageFloat32 getImageFloat32() {
    return imageFloat32;
  }
  */

  public BufferedImage drawKeypoints(List<Keypoint> keypoints) {
    Mat out = new Mat();
    Mat imageMat = getImageMat();
    MatOfKeyPoint keyPoints = FeatureUtils.keypointListToMatOfKeyPoint(keypoints);
    Features2d.drawKeypoints(imageMat, keyPoints, out);

    return matToBufferedImage(out);
  }

  public BufferedImage drawMatches(Image otherImage, List<Keypoint> keypoints1,
                                   List<Keypoint> keypoints2, List<Match> matches) {
    Mat out = new Mat();
    Mat imageMat = getImageMat();
    Mat otherImageMat = otherImage.getImageMat();

    MatOfKeyPoint keyPoints1 = FeatureUtils.keypointListToMatOfKeyPoint(keypoints1);
    MatOfKeyPoint keyPoints2 = FeatureUtils.keypointListToMatOfKeyPoint(keypoints2);

    MatOfDMatch dmatches = FeatureUtils.matchesListToMatOfDMatch(keypoints1, keypoints2, matches);

    Features2d.drawMatches(imageMat, keyPoints1, otherImageMat, keyPoints2, dmatches, out);

    return matToBufferedImage(out);
  }

  private BufferedImage matToBufferedImage(Mat image) {
    BufferedImage outImage;

    Mat swappedImage = swapChannels(image);

    byte[] byteArray = new byte[(int) (swappedImage.total() * swappedImage.channels())];
    swappedImage.get(0, 0, byteArray);

    int type;

    if(swappedImage.channels() == 1) {
      type = BufferedImage.TYPE_BYTE_GRAY;
    } else {
      type = BufferedImage.TYPE_3BYTE_BGR;
    }

    Size size = swappedImage.size();

    outImage = new BufferedImage((int) size.width, (int) size.height, type);

    outImage.getRaster().setDataElements(0, 0, (int) size.width, (int) size.height, byteArray);

    return outImage;
  }

  private Mat swapChannels(Mat image) {
    List<Mat> channels = new ArrayList<Mat>();
    Core.split(image, channels);

    List<Mat> swapped = new ArrayList<Mat>();
    swapped.add(0, channels.get(2));
    swapped.add(1, channels.get(1));
    swapped.add(2, channels.get(0));

    Mat swappedMat = new Mat();
    Core.merge(swapped, swappedMat);

    return swappedMat;
  }

  public void release() {
    imageMat.release();
    //imageFloat32 = null;
    height = 0;
    width = 0;
  }
}
