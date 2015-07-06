package com.multicorn.featureutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Class StopWatch, created by David on 01.06.15.
 *
 * @author David Steiner <david@stonerworx.com>
 */
public class StopWatch {

  private static StopWatch instance;
  private static final Object syncObject = new Object();
  private HashMap<UUID, Timediff> timediffs = new HashMap<UUID, Timediff>();
  private HashMap<String, ArrayList<UUID>> uuids = new HashMap<String, ArrayList<UUID>>();

  private StopWatch() {}

  public static StopWatch getInstance() {
    synchronized (syncObject) {
      if (instance == null) {
        instance = new StopWatch();
      }
      syncObject.notifyAll();
      return instance;
    }
  }

  public UUID start(String category) {
    synchronized (syncObject) {
      Timediff timediff = new Timediff(category);
      timediffs.put(timediff.getId(), timediff);
      if (!uuids.containsKey(category)) {
        uuids.put(category, new ArrayList<UUID>());
      }
      uuids.get(category).add(timediff.getId());
      UUID id = timediff.getId();
      syncObject.notifyAll();
      return id;
    }
  }

  public void stop(UUID id) {
    synchronized (syncObject) {
      timediffs.get(id).stop();
      syncObject.notifyAll();
    }
  }

  public void reset() {
    synchronized (syncObject) {
      timediffs = new HashMap<UUID, Timediff>();
      uuids = new HashMap<String, ArrayList<UUID>>();
    }
  }

  public long getDifference(UUID id) {
    synchronized (syncObject) {
      long difference = timediffs.get(id).getDifference();
      syncObject.notifyAll();
      return difference;
    }
  }

  public int getCountForCategory(String category) {
    synchronized (syncObject) {
      int count = 0;
      if (uuids.get(category) != null) {
        count = uuids.get(category).size();
      }
      syncObject.notifyAll();
      return count;
    }
  }

  public long getDifferenceForCategory(String category) {
    synchronized (syncObject) {
      int count = getCountForCategory(category);
      long difference = 0;
      if (count > 0) {
        long time = 0;

        if (uuids.get(category) != null) {
          for (UUID uuid : uuids.get(category)) {
            if (timediffs.get(uuid) != null && timediffs.get(uuid).getDifference() > 0) {
              time += timediffs.get(uuid).getDifference();
            }
          }
        }

        difference = time / count;
      }
      syncObject.notifyAll();
      return difference;
    }
  }
}

class Timediff {

  private UUID id = UUID.randomUUID();
  private String category;
  private long start = 0;
  private long end = 0;

  public Timediff(String category) {
    this.category = category;
    start = System.currentTimeMillis();
  }

  public UUID getId() {
    return id;
  }

  public void stop() {
    end = System.currentTimeMillis();
  }

  public long getDifference() {
    return end - start;
  }
}