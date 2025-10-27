package bgu.spl.mics.application.services;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.STATUS;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private final GPSIMU gpsimu;
    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("pose service");
        this.gpsimu=gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        
        subscribeBroadcast(CrashedBroadcast.class, tb ->{
            sendBroadcast(new CrashedBroadcast("Sensor GPSIMU disconnected", null));
            this.terminate();

        });

        subscribeBroadcast(TerminatedBroadcast.class, tb ->{
            if(tb.getMessage().equals("TimeService")||tb.getMessage().equals("FusionSlam")) {
                sendBroadcast(new TerminatedBroadcast("Pose terminated"));
                terminate();
            }
        });
        System.out.println("im here");
        subscribeBroadcast(TickBroadcast.class,tick -> {
            System.out.println("im here2");
            int currentTime=tick.getTick();
            if (gpsimu.getStatus()==STATUS.UP && gpsimu.getSize()-1>=currentTime) {
                Pose currentPose = (Pose) gpsimu.getPoseFromList(currentTime);
                sendEvent(new PoseEvent(currentPose) );
                System.out.println("i send posed");

            }
            else {
                sendBroadcast(new TerminatedBroadcast("Pose terminated"));
                terminate();

            }
        });


    }
}
