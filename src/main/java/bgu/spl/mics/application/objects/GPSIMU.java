package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import bgu.spl.mics.application.configs.Configuration;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private STATUS status;
    private List<Pose> poseList;
    private List<Pose> foundPoseList;
    public GPSIMU() {
        status=STATUS.UP;
        poseList = new ArrayList<>();
        foundPoseList = new ArrayList<>();
        loadPoseData();
    }

    public STATUS getStatus() {
        return status;
    }

    public List<Pose> getFoundPoseList() {
        return foundPoseList;
    }

    public Pose getPoseFromList(int tick) {
        Pose pose = poseList.get(tick-1);
        foundPoseList.add(pose);
        return pose;
    }
    public int getSize() {
        return poseList.size();
    }
    private void loadPoseData() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(Configuration.getInstance(null).getPoseJsonFile())) {
            // Define the type for the list of poses
            Type poseListType = new TypeToken<List<Pose>>() {}.getType();
            // Deserialize JSON into a list of Pose objects and assign to poseList
            this.poseList = gson.fromJson(reader, poseListType);
        } catch (IOException e) {
            // Handle file reading errors
            status = STATUS.ERROR;
            System.out.println("Failed to read pose data from file: " + e.getMessage());
            e.printStackTrace();
            this.poseList = null; // Set to null in case of error
        }

    }
}
