package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.DetectedObjectEvent;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private int currentTime = 0;
    private int id;
    private int frequency;
    private STATUS status;
    private LiDarDataBase liDarDataBase;
    private List<TrackedObject> lastTrackedObjects;
    private StatisticalFolder statisticalFolder;

    public LiDarWorkerTracker(int id, int frequency, STATUS status) {
        this.lastTrackedObjects = new LinkedList<TrackedObject>();
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.liDarDataBase = LiDarDataBase.getInstance();
        this.statisticalFolder = StatisticalFolder.getInstance();
    }

    public int getId() {
        return this.id;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public STATUS getStatus() {
        return this.status;
    }

    public int getCurrentTime() {
        return this.currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public List<TrackedObject> getLastTrackedObjects() {
        return this.lastTrackedObjects;
    }

    public List<TrackedObject> prepareData(int currentTime, StampedDetectedObjects stampedDetectedObjects) {
            lastTrackedObjects.clear();
                    for (DetectedObject D : stampedDetectedObjects.getDetectedobject()) {
                        List<StampedCloudPoints> stampedCloudPoints = liDarDataBase.getStampedCloudPointsAtTime(currentTime);
                         for (StampedCloudPoints sp : stampedCloudPoints) {
                             if (sp.getID().equals(D.getID())){
                                 List<CloudPoint> cp = sp.toCloudPoints();
                                 //setLastCloudPoints(cp);
                                 //setLastCloudPointsTime(sp.getTime());
                                 TrackedObject T = new TrackedObject(sp.getTime() ,D,cp);
                                 statisticalFolder.addTrackedObject();
                                 lastTrackedObjects.add(T);
                             }
                         }

            }
        return getLastTrackedObjects();
    }

    //public LiDarCloudPoints getLastFrame(){
        //return new LiDarCloudPoints("LiDar" + getId(), getLastCloudPoints());
    //}


public void checkIfError(int currentTime) {
    if (liDarDataBase.checkIfError(currentTime)) {
        setStatus(STATUS.ERROR);
    }
}
public List<TrackedObject> readyToSend(int time, List<TrackedObject> trackedObjects) {
    List<TrackedObject> toSend = new ArrayList<>();
        for (TrackedObject T : trackedObjects) {
            if(T.getTime() + getFrequency() <= time) {
                toSend.add(T);
            }
        }
        return toSend;
}
}
