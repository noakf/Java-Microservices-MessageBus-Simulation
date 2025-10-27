package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;

public class TrackedObjectsEvent implements Event {
    private List<TrackedObject> trackedObjectList;
    private int time;

   public TrackedObjectsEvent(List<TrackedObject> trackedObjectList, int time) {
       this.time = time;
       this.trackedObjectList = trackedObjectList;
}
   public List<TrackedObject> getTrackedObjectList() {
       return trackedObjectList;
}

    public int getTime() {
        return time;
    }
}
