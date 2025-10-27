//package bgu.spl.mics.application.objects;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class CameraTest {
//    Camera camera;
//    @BeforeEach
//    void setUp() {
//        List<StampedDetectedObjects> detectedObjectsList = new ArrayList<>();
//        detectedObjectsList.add(new StampedDetectedObjects(1, Arrays.asList(new DetectedObject("Cat","Blue cat"))));
//        camera = new Camera(1,4,)
//    }
//    @Test
//    void prepareData() {
//    }
//}

package bgu.spl.mics.application.objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CameraTest {
    Camera camera;
    @BeforeEach
    void setUp() {
        List<StampedDetectedObjects> detectedObjectsList = new ArrayList<>();
        detectedObjectsList.add(new StampedDetectedObjects(1, Arrays.asList(new DetectedObject("Door", "Brown Door"), new DetectedObject("Wall_1", "Wall"))));
        detectedObjectsList.add(new StampedDetectedObjects(2, Arrays.asList(new DetectedObject("Dog", "Dog"))));
        StampedDetectedObjects object3 =  new StampedDetectedObjects(3, Arrays.asList(new DetectedObject("Wall_2", "Wall"), new DetectedObject("Wall_4", "Wall"), new DetectedObject("Chair", "Chair near to table")));
        detectedObjectsList.add( new StampedDetectedObjects(3, Arrays.asList(new DetectedObject("Wall_2", "Wall"), new DetectedObject("Wall_4", "Wall"), new DetectedObject("Chair", "Chair near to table"))));
        detectedObjectsList.add(new StampedDetectedObjects(4, Arrays.asList(new DetectedObject("Wall_1", "Wall"))));
        camera = new Camera(1, 2, STATUS.UP, detectedObjectsList); // frequency set to 2
    }

    @Test
    void prepareData() {
        StampedDetectedObjects object3 = new StampedDetectedObjects(3, Arrays.asList(new DetectedObject("Wall_2", "Wall"), new DetectedObject("Wall_4", "Wall"), new DetectedObject("Chair", "Chair near to table")));
        StampedDetectedObjects prepareData = camera.prepareData(3);
       StampedDetectedObjects object4 = new StampedDetectedObjects(4, Arrays.asList(new DetectedObject("Wall_1", "Wall")));
        assertNotEquals(4, prepareData.getDetectedobject().size());
        assertEquals(object3, prepareData);
       prepareData = camera.prepareData(4);
       assertEquals(object4, prepareData);
       prepareData = camera.prepareData(1);
      assertEquals(camera.getDetectedObjectList().get(0), prepareData);
    }

    @Test
    void prepareDataWithError() {
        StampedDetectedObjects ErrorObj = new StampedDetectedObjects(6, Arrays.asList(new DetectedObject("Wall_2", "Wall"), new DetectedObject("ERROR", "ERROR"), new DetectedObject("Chair", "Chair near to table")));
        camera.addStampedDetectedObject(ErrorObj);
        StampedDetectedObjects prepareData = camera.prepareData(6);
        assertNotEquals(STATUS.ERROR, camera.getStatus());
    }


}