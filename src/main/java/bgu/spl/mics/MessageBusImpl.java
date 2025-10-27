package bgu.spl.mics;
import java.util.Map;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.Future;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.services.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
    private final Map<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> msPerEvent;
    private final Map<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> msPerBrodcast;
    private final Map<MicroService, LinkedBlockingQueue<Message>> messPerMS;
    private final Map<Event<?>, Future<?>> futurePerEvent;


    private MessageBusImpl() {
        msPerEvent = new ConcurrentHashMap<>();
        msPerBrodcast = new ConcurrentHashMap<>();
        messPerMS = new ConcurrentHashMap<>();
        futurePerEvent = new ConcurrentHashMap<>();
        this.msPerEvent.put(PoseEvent.class, new ConcurrentLinkedQueue<>());
        this.msPerEvent.put(DetectedObjectEvent.class, new ConcurrentLinkedQueue<>());
        this.msPerEvent.put(TrackedObjectsEvent.class, new ConcurrentLinkedQueue<>());
        this.msPerBrodcast.put(TickBroadcast.class, new ConcurrentLinkedQueue<>());
        this.msPerBrodcast.put(TerminatedBroadcast.class, new ConcurrentLinkedQueue<>());
        this.msPerBrodcast.put(CrashedBroadcast.class, new ConcurrentLinkedQueue<>());
    }

    private static class singletonMSB {
        private static final MessageBusImpl instance = new MessageBusImpl();
    }


    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
            msPerEvent.computeIfAbsent(type, t -> new ConcurrentLinkedQueue<>()).add(m);
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
            msPerBrodcast.computeIfAbsent(type, t -> new ConcurrentLinkedQueue<>()).add(m);
            System.out.println("MicroService " + m.getName() + " subscribed to " + type.getSimpleName());
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        Future<T> f = (Future<T>) futurePerEvent.get(e);
        synchronized (e) {
            if (f != null) {
                f.resolve(result);
                futurePerEvent.remove(e);
            }
        }

    }

    @Override
    public void sendBroadcast(Broadcast b) {
        ConcurrentLinkedQueue<MicroService> Q = msPerBrodcast.get(b.getClass());
        synchronized (Q) {
            if (Q != null && !Q.isEmpty()) {
                for ( MicroService MS : Q) {
                    try {
    
                        messPerMS.get(MS).put(b); // Add to message queue
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        Queue<MicroService> queue = msPerEvent.get(e.getClass());
        if (queue == null || queue.isEmpty()) {
            return null; 
        }

        synchronized (queue) { 
            MicroService m = queue.poll(); 
            if (m != null) {
                queue.add(m); 
                BlockingQueue<Message> msgQueue = messPerMS.get(m);
                    msgQueue.add(e); 
                    Future<T> f = new Future<>();
                    futurePerEvent.put(e, f);
                    return f; 
                }
            }
        return null; 
    }



    @Override
        public void register(MicroService m){
        System.out.println("registering");
            messPerMS.putIfAbsent(m, new LinkedBlockingQueue<>());
        }

        @Override
        public void unregister (MicroService m){
            for (Queue<MicroService> e : msPerEvent.values()) {
                synchronized (e) {
                    e.remove(m);
                }
            }
            for (Queue<MicroService> e : msPerBrodcast.values()) {
                synchronized (e) {
                    e.remove(m);
                }
            }
            messPerMS.remove(m);
        }

        @Override
        public Message awaitMessage (MicroService m) throws InterruptedException {
            LinkedBlockingQueue<Message> Q = messPerMS.get(m);
            if (Q == null) {
                throw new IllegalStateException("Microservice is not registered");
            }
            else{
                return Q.take();

            }

        }

        public static MessageBusImpl getInstance() {
            return singletonMSB.instance;
        }
    public Map<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> getMSPerEvent() {
        return msPerEvent;
    }
    public Map<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> getMSPerBrodcast() {
        return msPerBrodcast;
    }
    public Map<MicroService, LinkedBlockingQueue<Message>> getMessPerMS() {
        return messPerMS;
    }
    }


