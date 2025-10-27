package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.objects.CloudPoint;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
   private String id;
   private int time;
   private String description;
   private List <CloudPoint>  coordinates;

   public TrackedObject( int time, DetectedObject detectedObject, List <CloudPoint> coordinates) {
       this.id = detectedObject.getID();
       this.time = time;
       this.description = detectedObject.getDescription();
       this.coordinates = coordinates;
   }

    public List<CloudPoint> getCoordinates() {
        return coordinates;
    }

    public String getDescription() {
        return description;
    }

    public String getID() {
       return id;
    }

    public int getTime() {
        return time;
    }
}
