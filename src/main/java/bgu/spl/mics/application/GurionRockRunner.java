package bgu.spl.mics.application;
import bgu.spl.mics.application.configs.*;

import java.util.List;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;


/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        System.out.println("Starting simulation...");
        String configFilePath = args[0];

        try {
            // Initialize the configuration
            Configuration config = Configuration.getInstance(args[0]);
            config.initializeLiDarDataBase();
            // Initialize MessageBus (Singleton)
            MessageBusImpl messageBus = MessageBusImpl.getInstance();

            // Initialize Fusion-SLAM Singleton
            FusionSlam fusionSlam = FusionSlam.getInstance();
            fusionSlam.setInputFile(configFilePath);

            // Initialize PoseService
            GPSIMU gpsimu = new GPSIMU();
            PoseService poseService = new PoseService(gpsimu);
            Thread poseThread = new Thread(poseService);
            fusionSlam.setNumOfServices(1);
            poseThread.start();

            // Initialize Cameras and Camera Services
            List<Camera> cameras = CameraConfiguration.getCameras();
            for (Camera camera : cameras) {
                CameraService cameraService = new CameraService(camera);
                Thread cameraThread = new Thread(cameraService);
                fusionSlam.setNumOfServices(1);
                cameraThread.start();
            }

            // Initialize LiDAR Worker Services
            for (LidarConfig lidarConfig : config.getLidarWorkers().getLidarConfigurations()) {
                LiDarWorkerTracker lidarTracker = new LiDarWorkerTracker(lidarConfig.getId(), lidarConfig.getFrequency(), STATUS.UP);
                LiDarService lidarService = new LiDarService(lidarTracker);
                Thread lidarThread = new Thread(lidarService);
                fusionSlam.setNumOfServices(1);
                lidarThread.start();
            }

            // Initialize Fusion-SLAM Service
            FusionSlamService fusionSlamService = new FusionSlamService(fusionSlam);
            Thread fusionThread = new Thread(fusionSlamService);
            fusionThread.start();

            // Initialize TimeService
            TimeService timeService = new TimeService(config.getTickTime() * 1000, config.getDuration());
            Thread timeThread = new Thread(timeService);
            timeThread.start();
            // Wait for TimeService to finish
            timeThread.join();
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.out.println("Simulation interrupted.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
            System.out.println("Simulation interrupted by interruption.");
        }
    }
}
