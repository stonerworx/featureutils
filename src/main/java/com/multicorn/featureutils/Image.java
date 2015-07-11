package com.multicorn.featureutils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.Features2d;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;

import boofcv.core.image.ConvertBufferedImage;
import boofcv.struct.image.ImageFloat32;



/**
 * Class Image, created by David on 02.06.15.
 *
 * @author David Steiner <david@stonerworx.com>
 */
public class Image {

  protected int height;
  protected int width;

  protected Mat imageMat;
  protected ImageFloat32 imageFloat32;

  public Image() {}

  public Image(Mat image) {
    Size size = image.size();
    this.height = (int) size.height;
    this.width = (int) size.width;
    imageMat = image;

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
  }

  public Image(BufferedImage image) {
    height = image.getHeight();
    width = image.getWidth();

    imageMat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
    byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    imageMat.put(0, 0, pixels);

    imageFloat32 = new ImageFloat32(image.getWidth(), image.getHeight());
    ConvertBufferedImage.convertFrom(image, imageFloat32);

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

  public ImageFloat32 getImageFloat32() {
    return imageFloat32;
  }

  public BufferedImage drawKeypoints(List<Keypoint> keypoints) {
    Mat out = new Mat();
    Mat imageMat = getImageMat();
    MatOfKeyPoint keyPoints = FeatureUtils.keypointListToMatOfKeyPoint(keypoints);
    Features2d.drawKeypoints(imageMat, keyPoints, out);

    BufferedImage outImage;
    byte[] byteArray = new byte[(int) (out.total() * out.channels())];
    out.get(0, 0, byteArray);

    int type;

    if(out.channels() == 1) {
      type = BufferedImage.TYPE_BYTE_GRAY;
    } else {
      type = BufferedImage.TYPE_3BYTE_BGR;
    }

    outImage = new BufferedImage(getWidth(), getHeight(), type);

    outImage.getRaster().setDataElements(0, 0, getWidth(), getHeight(), byteArray);

    return outImage;
  }

  public void release() {
    imageMat.release();
    imageFloat32 = null;
    height = 0;
    width = 0;
  }
}
