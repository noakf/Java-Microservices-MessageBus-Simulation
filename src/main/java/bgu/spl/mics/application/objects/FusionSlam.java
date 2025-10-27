package bgu.spl.mics.application.objects;

import bgu.spl.mics.MessageBusImpl;
import java.io.FileWriter;
import java.io.File;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    private static class SingaltonFusionSlam {
        private static FusionSlam instance = new FusionSlam();
    }

    ;
    private List<LandMark> landMarks;
    private List<Pose> Poses;
    private List<TrackedObject> pending;
    private int time;
    private StatisticalFolder statisticalFolderInstance;
    private boolean crashReason;
    private String Error;
    private String faultySensor;
    private AtomicInteger numOfServices;
    private boolean wasGenerated;
    private LastFrames lastFramesInstance;
    private String inputFile;

    public FusionSlam() {
        landMarks = new ArrayList<>();
        Poses = new ArrayList<>();
        pending = new ArrayList<>();
        statisticalFolderInstance = StatisticalFolder.getInstance();
        crashReason = false;
        Error = "";
        faultySensor = "";
        this.numOfServices = new AtomicInteger(0);
        this.wasGenerated = false;
        this.lastFramesInstance = LastFrames.getInstance();
        this.inputFile = null;
    }

    public static FusionSlam getInstance() {
        return FusionSlam.SingaltonFusionSlam.instance;
    }

    public List<LandMark> getLandMarks() {
        return landMarks;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public List<Pose> getPoses() {
        return Poses;
    }

    public boolean getCrashReason() {
        return crashReason;
    }

    public String getError() {
        return Error;
    }

    public String getFaultySensor() {
        return faultySensor;
    }

    public boolean getWasGenerated() {
        return wasGenerated;
    }

    public List<LandMark>getLandMark() {
        return landMarks;
    }


    public void setWasGenerated(boolean wasGenerated) {
        this.wasGenerated = wasGenerated;
    }


    public boolean isNumOfServicesZero() {
        decrease();
        if(numOfServices.get() == 0){
            System.out.println("Number of services is zero");
            return true;
        }
        return false;
    }

    public void setInputFile (String inputFile){
        this.inputFile = inputFile;
    }

    public String getInputFile(){
        return inputFile;
    }

    public void setNumOfServices(int num) {
        this.numOfServices.addAndGet(num);
    }

    public void setError(String Error) {
        this.Error = Error;
    }

    public void setFaultySensor(String faultySensor) {
        this.faultySensor = faultySensor;
    }

    public void setPoses(List<Pose> poses) {
        Poses = poses;
    }

    public void setCrashReason(boolean isReason) {
        crashReason = isReason;
    }

    public void addLandMark(LandMark landMark) {
        landMarks.add(landMark);
    }


    public Pose getPostAtTime(int time) {
        for (Pose pose : Poses) {
            if (pose.getTime() == time) {
                return pose;
            }
        }
        return null;
    }

    public void addPose(Pose pose) {
        Poses.add(pose);
    }

public void decrease() {
    numOfServices.decrementAndGet();
    System.out.println("FusionSlam: Active microservices remaining: " + numOfServices.get());
}

    public List<CloudPoint> transformCoordinates(List<CloudPoint> coordinates, int time) {
        List<CloudPoint> transformedCoordinates = new ArrayList<>();
        Pose pose = getPostAtTime(time);
        if (pose == null) {
            return null;
        } else {
            for (CloudPoint p : coordinates) {
                double radYaw = Math.toRadians(pose.getYaw());
                double newX = Math.cos(radYaw) * p.getX() - Math.sin(radYaw) * p.getY() + pose.getX();
                double newY = Math.sin(radYaw) * p.getX() + Math.cos(radYaw) * p.getY() + pose.getY();
                transformedCoordinates.add(new CloudPoint(newX, newY));
            }
            return transformedCoordinates;
        }
    }


    public void trackedObjectsToLandMarks(List<TrackedObject> trackedObjects) {
        List<TrackedObject> test = new ArrayList<>(trackedObjects);
        test.addAll(pending);
        pending.clear();
        for (TrackedObject TO : test) {
            List<CloudPoint> coordinates = this.transformCoordinates(TO.getCoordinates(), TO.getTime());
            if (coordinates == null) {
                pending.add(TO);
                continue;
            }
            LandMark landMark = new LandMark(TO.getID(), TO.getDescription(), coordinates);
            LandMark oldLandMark = exist(landMark.getID());
            if (oldLandMark != null) {
                oldLandMark.setCoordinates(coordinates);

            }
            else {
                this.addLandMark(landMark);
                statisticalFolderInstance.addLandmark();
            }
        }
    }

    public LandMark exist(String id) {
        for (LandMark landmark : landMarks) {
            if (landmark.getID().equals(id)) {
                return landmark;
            }
        }
        return null;
    }

    public void generateErrorOutputFile(){
        File inputFile = new File(getInputFile());
        System.out.println(inputFile.getParent());
        String outputFile = inputFile.getParent() + File.separator + "error_output_file.json";
        ErrorOutputData errorOutputData = new ErrorOutputData(
                this.getError(),
                this.getFaultySensor(),
                lastFramesInstance.getMapCameraFrames(),
                lastFramesInstance.getMapLiDarFrames(),
                (ArrayList<Pose>) this.getPoses(),
                statisticalFolderInstance.getSystemRuntime(),
                statisticalFolderInstance.getNumDetectedObjects(),
                statisticalFolderInstance.getNumTrackedObjects(),
                statisticalFolderInstance.getNumLandmarks(),
                this.getLandMarks()
        );


        try (FileWriter writer = new FileWriter(outputFile)) {
            Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
            gsonPretty.toJson(errorOutputData, writer);
            System.out.println("Output written to " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to write the output file.");
        }
    }

    public void generateOutputFile() {
        File inputFile = new File(getInputFile());
        System.out.println(inputFile.getParent());
        String outputFile = inputFile.getParent() + File.separator + "output_file.json";
        OutputData outputData = new OutputData(
                statisticalFolderInstance.getSystemRuntime(),
                statisticalFolderInstance.getNumDetectedObjects(),
                statisticalFolderInstance.getNumTrackedObjects(),
                statisticalFolderInstance.getNumLandmarks(),
                this.getLandMarks()

        );

        try (FileWriter writer = new FileWriter(outputFile)) {
            Gson gsonPretty = new GsonBuilder().setPrettyPrinting().create();
            gsonPretty.toJson(outputData, writer);
            System.out.println("Output written to " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to write the output file.");
            setWasGenerated(true);
        }
    }


}
