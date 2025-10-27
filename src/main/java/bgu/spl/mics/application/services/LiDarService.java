package bgu.spl.mics.application.services;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectedObjectEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private final LiDarWorkerTracker liDarWorkerTracker;
    private final List<TrackedObject> pending;
    private final LiDarDataBase liDarDataBase;
    private LastFrames lastFramesInstance;

    /**
     * Constructor for LiDarService.
     *
     * @param liDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker liDarWorkerTracker) {
        super("Lidar worker tracker"+liDarWorkerTracker.getId());
        this.liDarWorkerTracker=liDarWorkerTracker;
        this. pending = new ArrayList<>();
        this.liDarDataBase = LiDarDataBase.getInstance();
        this.lastFramesInstance = LastFrames.getInstance();
    }


    public void setStampedDetectedObjects(TrackedObject trackedObjects){
        this.pending.add(trackedObjects);
    }

    public List<TrackedObject> getPending() {
        return pending;
    }


    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
      subscribeBroadcast(TickBroadcast.class, tb ->{
          liDarWorkerTracker.setCurrentTime(tb.getTick());
          liDarWorkerTracker.checkIfError(liDarWorkerTracker.getCurrentTime());
              if (liDarWorkerTracker.getStatus() == STATUS.ERROR) {
                  Broadcast b = new CrashedBroadcast("Sensor liDarWorkerTracker " +liDarWorkerTracker.getId() + "disconnected", "LiDarWorkerTracker " + liDarWorkerTracker.getId());
                  sendBroadcast(b);
                  this.terminate();
                  return;
              }
              if (!pending.isEmpty()) {
                  List<TrackedObject> toSend = liDarWorkerTracker.readyToSend(liDarWorkerTracker.getCurrentTime(),getPending());
                  if (!toSend.isEmpty()) {
                      pending.removeAll(toSend);
                      TrackedObjectsEvent e = new TrackedObjectsEvent(toSend, tb.getTick());
                      sendEvent(e);
                      lastFramesInstance.setLiDarFrame(liDarWorkerTracker.getId(), liDarWorkerTracker.getLastTrackedObjects());
                  }

              }
      });
      subscribeEvent(DetectedObjectEvent.class,cl ->{
          System.out.println("got a detected object");
          if(liDarWorkerTracker.getStatus() == STATUS.UP) {
              int detectionTime = cl.getStampedDetectedObjects().getTime();
              List<TrackedObject> trackedObjects = liDarWorkerTracker.prepareData(detectionTime, cl.getStampedDetectedObjects());
              if(!trackedObjects.isEmpty()) {
                  if (detectionTime + liDarWorkerTracker.getFrequency() <= liDarWorkerTracker.getCurrentTime()) {
                      TrackedObjectsEvent e = new TrackedObjectsEvent(trackedObjects, liDarWorkerTracker.getCurrentTime());
                      sendEvent(e);
                      lastFramesInstance.setLiDarFrame(liDarWorkerTracker.getId(), liDarWorkerTracker.getLastTrackedObjects());
                  } else {
                      pending.addAll(trackedObjects);
                  }
                  }
              if (liDarDataBase.isThisTheLast(liDarWorkerTracker.getCurrentTime(), liDarWorkerTracker.getFrequency())) {
                  liDarWorkerTracker.setStatus(STATUS.DOWN);
                  System.out.println("LiDar worker tracker down");
                  sendBroadcast(new TerminatedBroadcast("LiDarService " + liDarWorkerTracker.getId() + " Terminated"));
                  terminate();
              }
          }
      });

        subscribeBroadcast(TerminatedBroadcast.class, tb -> {
            if(tb.getMessage().equals("TimeService")) {
                liDarWorkerTracker.setStatus(STATUS.DOWN);
                this.terminate();
            }
        });
        subscribeBroadcast(CrashedBroadcast.class, tb -> {
                if(liDarWorkerTracker.getStatus() != STATUS.ERROR) {
                    liDarWorkerTracker.setStatus(STATUS.ERROR);
                    Broadcast b = new CrashedBroadcast("Sensor liDarWorkerTracker " + liDarWorkerTracker.getId() + "disconnected", "LiDarWorkerTracker " +liDarWorkerTracker.getId());
                    sendBroadcast(b);
                    this.terminate();

                }
        });
    }
}
