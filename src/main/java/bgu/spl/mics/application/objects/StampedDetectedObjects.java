package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private int time;
    private List <DetectedObject> detectedObjects;

public StampedDetectedObjects(int time, List <DetectedObject> detectedObjects) {
	this.time=time;
    this.detectedObjects = detectedObjects;
}
public int getTime() {
    return time;
}
public List <DetectedObject> getDetectedobject() {
    return detectedObjects;
}
    public String toString() {
     return "StampedDetectedObjects: " + time + " " + detectedObjects ;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StampedDetectedObjects that = (StampedDetectedObjects) o;

        return time == that.time &&
                (detectedObjects != null ? detectedObjects.equals(that.detectedObjects) : that.detectedObjects == null);
    }

    @Override
    public int hashCode() {
        int result = time;
        result = 31 * result + (detectedObjects != null ? detectedObjects.hashCode() : 0);
        return result;
    }



}


