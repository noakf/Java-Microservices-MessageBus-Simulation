package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ErrorOutputData {

        private String ERROR;
        private String faultySensor;
        private Map<String, StampedDetectedObjects> lastCameraFrames;
        private  Map<String, List<TrackedObject>> lastLidarFrames;
        private ArrayList<Pose> poses;
        private int systemRuntime;
        private int numDetectedObjects;
        private int numTrackedObjects;
        private int numLandmarks;
        private List<LandMark> landMarks;

        public ErrorOutputData(String ERROR, String faultySensor,Map<String, StampedDetectedObjects> lastCameraFrames,
                               Map<String, List<TrackedObject>> lastLidarFrames, ArrayList<Pose> poses,int systemRuntime, int numDetectedObjects, int numTrackedObjects,
                               int numLandmarks, List<LandMark> landMarks) {

            this.ERROR = ERROR;
            this.faultySensor = faultySensor;
            this.lastCameraFrames = lastCameraFrames;
            this.lastLidarFrames = lastLidarFrames;
            this.poses = poses;
            this.systemRuntime = systemRuntime;
            this.numDetectedObjects = numDetectedObjects;
            this.numTrackedObjects = numTrackedObjects;
            this.numLandmarks = numLandmarks;
            this.landMarks = landMarks;
        }
    }

