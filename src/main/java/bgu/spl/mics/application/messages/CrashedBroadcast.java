package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.CloudPoint;

import java.util.List;
import java.util.Objects;

public class CrashedBroadcast implements Broadcast {
     private String Error;
     private String faultySensor;


      public CrashedBroadcast(String ERROR, String faultySensor) {

         this.Error = ERROR;
         this.faultySensor = faultySensor;
     }

    public String getError() {
        return Error;
    }

    public String getFaultySensor() {
          return faultySensor;
    }
    
}
