package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LastFrames {
    private static class LastFrameSingelton{
        private static final LastFrames instance = new LastFrames();

    }
    private final Map<String, StampedDetectedObjects> camerasLastFrames;
    private final Map<String, List<TrackedObject>> lidarsLastFrames;


    private LastFrames() {
        this.camerasLastFrames = new ConcurrentHashMap<>();
        this.lidarsLastFrames = new ConcurrentHashMap<>();
    }

    public static LastFrames getInstance() {
        return LastFrameSingelton.instance;
    }


    public void setCameraFrame(int ID, StampedDetectedObjects stampedDetectedObject) {
        camerasLastFrames.put("Camera" +ID, stampedDetectedObject);
    }


    public void setLiDarFrame(int ID, List<TrackedObject> trackedObjects) {
              synchronized (lidarsLastFrames) {
                  lidarsLastFrames.put("LiDarTrackerWorker"+ID, trackedObjects);
              }

    }
    public Map<String, List<TrackedObject>> getMapLiDarFrames() {
        synchronized (lidarsLastFrames) {

            return lidarsLastFrames;
        }
            }

    public Map<String, StampedDetectedObjects> getMapCameraFrames() {
        return camerasLastFrames;
    }





}
