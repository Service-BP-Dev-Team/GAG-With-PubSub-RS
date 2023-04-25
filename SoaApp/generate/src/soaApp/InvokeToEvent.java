/**
 * null
 **/
package soaApp;

import inria.smarttools.core.util.*;

/**
 **/
public class InvokeToEvent extends StEventImpl {
   //
   // Fields 
   //

   /**
    **/
   protected gag.ServiceNode service;
   /**
    **/
   protected gag.behaviour.SubscriptionTable subscriptions;

   /**
    **/
   public void setService(gag.ServiceNode v){
      this.service = v;
   }

   public gag.ServiceNode getService(){
      return service;
   }
   /**
    **/
   public void setSubscriptions(gag.behaviour.SubscriptionTable v){
      this.subscriptions = v;
   }

   public gag.behaviour.SubscriptionTable getSubscriptions(){
      return subscriptions;
   }

   //
   // Constructors 
   //

   /**
    * Constructor
    **/
   public   InvokeToEvent(gag.ServiceNode service, gag.behaviour.SubscriptionTable subscriptions){
      setService(service);
      setSubscriptions(subscriptions);
   }

   /**
    * Constructor
    **/
   public   InvokeToEvent(String adressee, gag.ServiceNode service, gag.behaviour.SubscriptionTable subscriptions){
      super(adressee);
      setService(service);
      setSubscriptions(subscriptions);
   }


   //
   // Methods 
   //

   /**
    * Return a short description of the InvokeToEvent object.
    * @return a value of the type 'String' : a string representation of this InvokeToEvent
    **/
   public  String toString(){
      String res = "InvokeToEvent";
      return res;
   }


}
