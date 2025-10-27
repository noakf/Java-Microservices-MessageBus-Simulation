//package bgu.spl.mics.application.objects;
//package org.junit.jupiter.api
//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class FusionSlamTest {
//
//    @Test
//    void trackedObjectsToLandMarks() {
//    }
//}

package bgu.spl.mics.application.objects;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FusionSlamTest {
    private FusionSlam fusionSlam;

    @BeforeEach
    void setUp() {
        fusionSlam = FusionSlam.getInstance();
        fusionSlam.getLandMark().clear();

    }
    @Test
    void trackedObjectsToLandMarks() {
        List<CloudPoint> cloudPoints1 = new ArrayList<>();//wall_1-time2
        List<CloudPoint> cloudPoints2 = new ArrayList<>();//wall_1-time10
        List<CloudPoint> cloudPoints3 = new ArrayList<>();//Door-time7
        fusionSlam.addPose(new Pose( (float)-3.2076, (float)0.0755,(float)-87.48,2));
        fusionSlam.addPose(new Pose( (float)-2.366,  (float)0.9327,(float)-28.08,7));
        fusionSlam.addPose(new Pose( (float)0.0,  (float)3.6,(float)57.3,10));
        cloudPoints1.add(new CloudPoint(0.1176, 3.6969));
        cloudPoints1.add(new CloudPoint(0.11362,3.6039));
        cloudPoints2.add(new CloudPoint(0.5, 3.9));
        cloudPoints2.add(new CloudPoint(0.2, 3.7));
        cloudPoints3.add(new CloudPoint(0.5, -2.1));
        cloudPoints3.add(new CloudPoint(0.8, -2.3));
        TrackedObject trackedObject1 = new TrackedObject(2,new DetectedObject("Wall_1","Wall near to chair"), cloudPoints1 );
        TrackedObject trackedObject2 = new TrackedObject(10, new DetectedObject("Wall_1","Wall") , cloudPoints2);
        TrackedObject trackedObject3 = new TrackedObject(7, new DetectedObject("Door", "Door") ,cloudPoints3);
        List<TrackedObject> trackedObjects = Arrays.asList(trackedObject1,trackedObject2,trackedObject3);
        fusionSlam.trackedObjectsToLandMarks(trackedObjects);
       LandMark landmark2 = fusionSlam.getLandMark().get(0);
        assertEquals(-1.260438231426796, landmark2.getCoordinates().get(0).getX());
        assertEquals(3.124125914456589, landmark2.getCoordinates().get(0).getY());
        assertEquals(-1.3038657317253883, landmark2.getCoordinates().get(1).getX());
        assertEquals(2.9438188258406823, landmark2.getCoordinates().get(1).getY());
        LandMark landmark = fusionSlam.getLandMark().get(1);
        assertEquals(-2.913332578606659, landmark.getCoordinates().get(0).getX());
        assertEquals(-1.1554635639732926, landmark.getCoordinates().get(0).getY());
        assertEquals(-2.742785996686237, landmark.getCoordinates().get(1).getX());
        assertEquals(-1.4731329886827864, landmark.getCoordinates().get(1).getY());
    }
    @Test
    void trackedObjectsToLandMarksWithDifferentCloudPointsSize() {
        List<CloudPoint> cloudPoints1 = new ArrayList<>();//wall-time2
        List<CloudPoint> cloudPoints2 = new ArrayList<>();//wall-time10
        List<CloudPoint> cloudPoints3 = new ArrayList<>();//Door-time7
        cloudPoints1.add(new CloudPoint(0.1176, 3.6969));
        cloudPoints1.add(new CloudPoint(0.11362,3.6039));
        cloudPoints2.add(new CloudPoint(0.5, 3.9));
        cloudPoints2.add(new CloudPoint(0.2, 3.7));
        cloudPoints2.add(new CloudPoint(0.1, 3.6));
        cloudPoints2.add(new CloudPoint(7.0, 8.0));
        cloudPoints3.add(new CloudPoint(0.5, -2.1));
        cloudPoints3.add(new CloudPoint(0.8, -2.3));
        fusionSlam.addPose(new Pose( (float) -3.2076, (float) 0.0755,(float) -87.48,2));
        fusionSlam.addPose(new Pose( (float)-2.366,  (float)0.9327,(float)-28.08,7));
        fusionSlam.addPose(new Pose( (float)0.0, (float) 3.6,(float)57.3,10));
        TrackedObject trackedObject1 = new TrackedObject(2, new DetectedObject("Wall_1", "Wall near to chair"), cloudPoints1);
        TrackedObject trackedObject2 = new TrackedObject(10, new DetectedObject("Wall_1","Wall") , cloudPoints2);
        TrackedObject trackedObject3 = new TrackedObject(7, new DetectedObject("Door", "Door") ,cloudPoints3);
        List<TrackedObject> trackedObjects = Arrays.asList(trackedObject1,trackedObject2,trackedObject3);
        fusionSlam.trackedObjectsToLandMarks(trackedObjects);
        LandMark landmark2 = fusionSlam.getLandMark().get(0);
        assertEquals(-1.260438231426796, landmark2.getCoordinates().get(0).getX());
        assertEquals(3.124125914456589, landmark2.getCoordinates().get(0).getY());
        assertEquals(-1.3038657317253883, landmark2.getCoordinates().get(1).getX());
        assertEquals(2.9438188258406823, landmark2.getCoordinates().get(1).getY());
        LandMark landmark = fusionSlam.getLandMark().get(1);
        assertEquals(-2.913332578606659, landmark.getCoordinates().get(0).getX());
        assertEquals(-1.1554635639732926, landmark.getCoordinates().get(0).getY());
        assertEquals(-2.742785996686237, landmark.getCoordinates().get(1).getX());
        assertEquals(-1.4731329886827864, landmark.getCoordinates().get(1).getY());
        List<LandMark> landmarks = fusionSlam.getLandMark();
        assertEquals (2, landmarks.size(), "Landmarks list size should match the number of tracked objects.");
        assertEquals(4, landmarks.get(0).getCoordinates().size());
    }
}
