package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.StampedDetectedObjects;


public class DetectedObjectEvent implements Event {
    private StampedDetectedObjects stampedDetectedObjects;
    private int detctionTime;

    public DetectedObjectEvent(StampedDetectedObjects stampedDetectedObjects, int detctionTime) {
        this.stampedDetectedObjects = stampedDetectedObjects;
        this.detctionTime = detctionTime;
    }
    public StampedDetectedObjects getStampedDetectedObjects() {
        return stampedDetectedObjects;
    }
    public int detctionTime() {
        return detctionTime;
    }

}
