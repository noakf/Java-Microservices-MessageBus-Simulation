package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class OutputData {
    private int systemRuntime;
    private int numDetectedObjects;
    private int numTrackedObjects;
    private int numLandmarks;
    private List<LandMark> landMarks;

    public OutputData(int systemRuntime, int numDetectedObjects, int numTrackedObjects,
                      int numLandmarks, List<LandMark> landMarks) {

        this.systemRuntime = systemRuntime;
        this.numDetectedObjects = numDetectedObjects;
        this.numTrackedObjects = numTrackedObjects;
        this.numLandmarks = numLandmarks;
        this.landMarks = landMarks;
    }
}

