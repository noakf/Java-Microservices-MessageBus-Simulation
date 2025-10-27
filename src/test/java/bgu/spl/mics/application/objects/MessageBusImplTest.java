package bgu.spl.mics.application.objects;
import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.messages.DetectedObjectEvent;
import bgu.spl.mics.application.services.CameraService;
import bgu.spl.mics.application.services.LiDarService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

class MessageBusImplTest {
    CameraService cameraService;
    MessageBusImpl messageBus;
    LiDarService liDarService;
    @BeforeEach
    void setUp() {
        messageBus = messageBus.getInstance();
        cameraService = new CameraService(new Camera(1,1,STATUS.UP, Arrays.asList(new StampedDetectedObjects(1,Arrays.asList(new DetectedObject("Wall_1","Wall"),new DetectedObject("Cat","red Cat"))))));
        liDarService = new LiDarService(new LiDarWorkerTracker(1,2,STATUS.UP));
    }
   
   @Test
        public void testInvalidAwaitMessage() {
        // * Test awaiting a message for an unregistered MicroService *
        assertThrows(IllegalStateException.class, () -> messageBus.awaitMessage(liDarService),
                "Awaiting message for an unregistered MicroService should throw an exception.");
    }

    @Test
    void subscribeBroadcast() {
        messageBus.subscribeBroadcast(TickBroadcast.class,cameraService);
        ConcurrentLinkedQueue<MicroService> queue = messageBus.getMSPerBrodcast().get(TickBroadcast.class);
        assertTrue(queue.contains(cameraService));
    }

    @Test
    void register() {
        LinkedBlockingQueue<Message> queue = messageBus.getMessPerMS().get(cameraService);
        assertEquals(null,queue);
        messageBus.register(cameraService);
        queue = messageBus.getMessPerMS().get(cameraService);
        assertNotEquals(null,queue);
    }
}