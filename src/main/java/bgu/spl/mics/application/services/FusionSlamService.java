package bgu.spl.mics.application.services;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;

import java.security.Provider;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 *
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private final FusionSlam fusionSlam;


    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlam");
        this.fusionSlam = FusionSlam.getInstance();
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tb-> {
                    fusionSlam.setTime(tb.getTick());
                    if(fusionSlam.getWasGenerated()){
                        sendBroadcast(new TerminatedBroadcast("FusionSlam"));
                        terminate();
                    }

                }
        );
        subscribeEvent(PoseEvent.class, ev ->{
            fusionSlam.addPose(ev.getPose());
            fusionSlam.trackedObjectsToLandMarks(new ArrayList<>());
                }
        );
        subscribeEvent(TrackedObjectsEvent.class, ev->{
            fusionSlam.trackedObjectsToLandMarks(ev.getTrackedObjectList());
        });

        subscribeBroadcast(TerminatedBroadcast.class, tb->{
            if(fusionSlam.isNumOfServicesZero() || tb.getMessage().equals("TimeService")){
                System.out.println("FusionSlam terminated");
                sendBroadcast(new TerminatedBroadcast("FusionSlam"));
                fusionSlam.generateOutputFile();
                terminate();
            }

        });
        subscribeBroadcast(CrashedBroadcast.class, tb->{
            //fusionSlam.decrease();
            if(!fusionSlam.getCrashReason()){
                fusionSlam.setCrashReason(true);
                fusionSlam.setError(tb.getError());
                fusionSlam.setFaultySensor(tb.getFaultySensor());
            }
            //fusionSlam.addFrame(tb.getLastFrame(), tb.getFaultySensor());
            if(fusionSlam.isNumOfServicesZero()) {
                sendBroadcast(new TerminatedBroadcast("FusionSlam"));
                fusionSlam.generateErrorOutputFile();
                terminate();
            }
        });

    }
}
