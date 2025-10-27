package bgu.spl.mics.application.objects;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import bgu.spl.mics.application.configs.Configuration;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {
    private static class SingaltonLiDarDataBase {
        private static LiDarDataBase instance = new LiDarDataBase();
    }

    ;
    private final Map<Integer, List<StampedCloudPoints>> data = new HashMap<>(); // time -> list of StampedCloudPoints
    private int lastTime = 0;

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */
    public LiDarDataBase() {
    }

    public static LiDarDataBase getInstance() {
        return SingaltonLiDarDataBase.instance;
    }

    public void loadData(String filePath) {
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            try (FileReader reader = new FileReader(filePath)) {
                List<Map<String, Object>> records = gson.fromJson(reader, listType);

                for (Map<String, Object> record : records) {
                    int time = ((Double) record.get("time")).intValue();
                    String id = (String) record.get("id");
                    List<List<Double>> cloudPoints = (List<List<Double>>) record.get("cloudPoints");
                    StampedCloudPoints stampedCloudPoint = new StampedCloudPoints(id, time, cloudPoints);
                    data.putIfAbsent(time, new ArrayList<>());
                    data.get(time).add(stampedCloudPoint);

                    if (time > lastTime) {
                        lastTime = time;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading LiDar data: " + e.getMessage());
        }
    }

    public List<StampedCloudPoints> getStampedCloudPointsAtTime(int time) {
        return data.getOrDefault(time, Collections.emptyList());
    }


    public boolean checkIfError(int time) {
        List<StampedCloudPoints> stampedCloudPoints = getStampedCloudPointsAtTime(time);
        for (StampedCloudPoints stampedCloudPoint : stampedCloudPoints) {
            if (stampedCloudPoint.getTime() == time && stampedCloudPoint.getID().equals("ERROR")) {
                return true;
            }
        }
        return false;
    }

    public boolean isThisTheLast(int time, int frequency) {
        if (lastTime + frequency <= time) {
            return true;
        }
        return false;
    }
}
