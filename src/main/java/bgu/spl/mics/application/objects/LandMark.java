package bgu.spl.mics.application.objects;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private String id;
    private String description;
    private List<CloudPoint> coordinates;

    public LandMark(String Id, String description, List<CloudPoint> coordinates) {
        this.id = Id;
        this.description = description;
        this.coordinates = coordinates;
    }

    public String getID() {
        return id;
    }

    public List<CloudPoint> getCoordinates() {
        return coordinates;
    }

    public synchronized void setCoordinates(List<CloudPoint> newCoordinates) {
        for (int i = 0; i < newCoordinates.size(); i++) {
            if (i < coordinates.size()) {
                double avgX = (coordinates.get(i).getX() + newCoordinates.get(i).getX()) / 2;
                double avgY = (coordinates.get(i).getY() + newCoordinates.get(i).getY()) / 2;
                coordinates.set(i, new CloudPoint(avgX, avgY));
            } else {
                coordinates.add(newCoordinates.get(i));
            }
        }

    }
}
