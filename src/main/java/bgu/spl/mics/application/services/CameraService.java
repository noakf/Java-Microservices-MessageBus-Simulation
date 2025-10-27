    package bgu.spl.mics.application.services;
    import bgu.spl.mics.Broadcast;
    import bgu.spl.mics.Event;
    import bgu.spl.mics.application.messages.CrashedBroadcast;
    import bgu.spl.mics.application.messages.TerminatedBroadcast;
    import bgu.spl.mics.application.objects.*;
    import bgu.spl.mics.application.messages.TickBroadcast;
    import bgu.spl.mics.MicroService;
    import bgu.spl.mics.application.messages.DetectedObjectEvent;
    /**
     * CameraService is responsible for processing data from the camera and
     * sending DetectObjectsEvents to LiDAR workers.
     *
     * This service interacts with the Camera object to detect objects and updates
     * the system's StatisticalFolder upon sending its observations.
     */
    public class CameraService extends MicroService {
        private final Camera camera;
        private LastFrames lastFramesInstance;

        /**
         * Constructor for CameraService.
         *
         * @param camera The Camera object that this service will use to detect objects.
         */
        public CameraService(Camera camera) {
            super("camera " + camera.getID());
            this.camera = camera;
            this.lastFramesInstance = LastFrames.getInstance();

        }

        /**
         * Initializes the CameraService.
         * Registers the service to handle TickBroadcasts and sets up callbacks for sending
         * DetectObjectsEvents.
         */
        @Override
        protected void initialize() {
            subscribeBroadcast(TickBroadcast.class, tb -> {
                if (camera.getStatus() == STATUS.UP) {
                    if (camera.getErrorDescription() != null && camera.getErrorTime() == tb.getTick()) {
                        camera.setStatus(STATUS.ERROR);
                        Broadcast b = new CrashedBroadcast(camera.getErrorDescription(), "Camera " + camera.getID());
                        sendBroadcast(b);
                        this.terminate();

                    } else {
                        camera.setCurrentTime(tb.getTick());
                        StampedDetectedObjects stampedDetectedObjects = camera.prepareData(tb.getTick());
                        if (stampedDetectedObjects != null) {
                            DetectedObjectEvent e = new DetectedObjectEvent(stampedDetectedObjects, stampedDetectedObjects.getTime());
                            camera.addEvent(e, tb.getTick() + camera.getFrequency());
                            DetectedObjectEvent eventToSend = camera.getEvent(tb.getTick());
                            if (eventToSend != null) {
                                sendEvent(eventToSend);
                                System.out.println(getName() + ": Sent DetectObjectsEvent for time " + tb.getTick() + ", detection time: " + stampedDetectedObjects.getTime());
                                lastFramesInstance.setCameraFrame(camera.getID(), camera.getLastStampedDetectedObjectList());
                            }
                        }
                        if (camera.isThisTheLast(tb.getTick())) {
                            System.out.println("camera terminated");
                            sendBroadcast(new TerminatedBroadcast("Camera terminated"));
                            terminate();
                        }
                    }
                }
            });
            subscribeBroadcast(TerminatedBroadcast.class, tb -> {
                if(tb.getMessage().equals("TimeService")||tb.getMessage().equals("FusionSlam")) {
                    camera.setStatus(STATUS.DOWN);
                    this.terminate();
                }
        });
            subscribeBroadcast(CrashedBroadcast.class, tb -> {
                if(camera.getStatus() != STATUS.ERROR) {
                    camera.setStatus(STATUS.ERROR);
                Broadcast b = new CrashedBroadcast(camera.getErrorDescription(), "Camera " + camera.getID());
                    sendBroadcast(b);
                    this.terminate();
                }
            });
        }
    }

