package bgu.spl.mics.application.objects;


import bgu.spl.mics.Event;
import bgu.spl.mics.application.messages.DetectedObjectEvent;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private int currentTime;
    private int id;
    private int frequency;
    private STATUS status;
    private List<StampedDetectedObjects> detectedObjectList;
    private StatisticalFolder statisticalFolderInstance;
    private String errorDescription;
    private int errorTime = 0;
    private StampedDetectedObjects lastStampedDetectedObjects;
    private final Map<Integer, DetectedObjectEvent> eventPerTime = new ConcurrentHashMap<>();

    public Camera(int id, int frequency, STATUS status, List<StampedDetectedObjects> detectedObjectList) {
        this.currentTime = 0;
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.detectedObjectList = detectedObjectList;
        this.errorDescription = null;
        this.statisticalFolderInstance = StatisticalFolder.getInstance();
        this.lastStampedDetectedObjects = null;

    }
    public int getID() {
        return id;
    }
    public int getFrequency() {
        return frequency;
    }

    public int getErrorTime(){
        return errorTime;
    }

    public STATUS getStatus() {
        return status;
    }

    public List<StampedDetectedObjects> getDetectedObjectList() {
        return detectedObjectList;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public StampedDetectedObjects getLastStampedDetectedObjectList() {
        return lastStampedDetectedObjects;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }
    public void setErrorTime(int errorTime) {
        this.errorTime = errorTime;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public void setLastStampedDetectedObjectList(StampedDetectedObjects lastDetectedObjectList) {
        this.lastStampedDetectedObjects = lastDetectedObjectList;
    }

    public void setDetectedObjectList(List<StampedDetectedObjects> detectedObjectList) {
        this.detectedObjectList = detectedObjectList;
    }

    public String toString(){
        return "Camera ID: "+id+" Frequency: "+frequency+" Status: "+status + detectedObjectList ;

    }


    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public boolean isThisTheLast (int time){
        if(detectedObjectList.get(detectedObjectList.size()-1).getTime()+ frequency <= time){
            return true;
        }
        return false;
    }

    public void crashIfError(List<DetectedObject> detectedObjectList, int time){
        for (DetectedObject detectedObject : detectedObjectList) {
            if (detectedObject.getID().equals("ERROR") ) {
                System.out.println("crash "+detectedObject.getID());
                setErrorDescription(detectedObject.getDescription());
                setErrorTime(time);
            }
        }

    }
    public void addEvent(DetectedObjectEvent e, int freqTime){
        eventPerTime.put(freqTime, e);
    }

    public DetectedObjectEvent getEvent(int time){
        return eventPerTime.remove(time);
    }

    public StampedDetectedObjects prepareData(int currentTime) {
        Iterator<StampedDetectedObjects> iterator = this.getDetectedObjectList().iterator();
        while (iterator.hasNext()) {
        StampedDetectedObjects stampedDetectedObjects = iterator.next();
            if (stampedDetectedObjects.getTime() == currentTime && stampedDetectedObjects.getDetectedobject() != null && !stampedDetectedObjects.getDetectedobject().isEmpty()) {
                statisticalFolderInstance.addDetectedObject(stampedDetectedObjects.getDetectedobject().size());
                setLastStampedDetectedObjectList(stampedDetectedObjects);
                if(iterator.hasNext()) {
                    StampedDetectedObjects stampedDetectedObjectsNext = iterator.next();
                    crashIfError(stampedDetectedObjectsNext.getDetectedobject(), stampedDetectedObjectsNext.getTime());
                }
                return stampedDetectedObjects;
            }
    }
        return null;
    }

    public void addStampedDetectedObject (StampedDetectedObjects stampedDetectedObjects){
        detectedObjectList.add(stampedDetectedObjects);

    }

}
