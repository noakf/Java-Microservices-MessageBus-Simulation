package bgu.spl.mics.application.objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private static class SingaltonStatisticalFolder {
        private static StatisticalFolder instance = new StatisticalFolder();
    }

    ;
    private  final AtomicInteger systemRuntime = new AtomicInteger(0);
    private  final AtomicInteger numDetectedObjects = new AtomicInteger(0);
    private  final AtomicInteger numTrackedObjects = new AtomicInteger(0);
    private  final AtomicInteger numLandmarks = new AtomicInteger(0);


    public static StatisticalFolder getInstance() {
        return StatisticalFolder.SingaltonStatisticalFolder.instance;
    }

    public int getSystemRuntime() {
        return systemRuntime.get();
    }

    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }

    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }

    public int getNumLandmarks() {
        return numLandmarks.get();
    }

    public void addDetectedObject(int detectedObjects) {
        this.numDetectedObjects.addAndGet(detectedObjects);
    }

    public void addTrackedObject() {
        this.numTrackedObjects.addAndGet(1);
    }

    public void addLandmark() {
        this.numLandmarks.addAndGet(1);
    }

    public void addSystemRuntime() {
        this.systemRuntime.addAndGet(1);
    }

}