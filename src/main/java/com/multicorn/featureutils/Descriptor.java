package com.multicorn.featureutils;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Class Descriptor, created by David on 01.06.15.
 *
 * @author David Steiner <david@stonerworx.com>
 */
public class Descriptor {

  @Expose
  private ArrayList<Double> values = new ArrayList<Double>();

  public void addValue(double value) {
    values.add(value);
  }

  public ArrayList<Double> getValues() {
    return values;
  }

  public int getSize() {
    return values.size();
  }
}
