package bgu.spl.mics.application.objects;

/**
 * DetectedObject represents an object detected by the camera.
 * It contains information such as the object's ID and description.
 */
public class DetectedObject {
    private String id;
    private String description;

    public DetectedObject(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getID() {
        return id;
    }

    public String getDescription() {
        return description;
    }
    public String toString(){
        return "ID: " + id + " Description: " + description;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DetectedObject that = (DetectedObject) o;

        if (!id.equals(that.id)) return false;
        return description.equals(that.description);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }



}
