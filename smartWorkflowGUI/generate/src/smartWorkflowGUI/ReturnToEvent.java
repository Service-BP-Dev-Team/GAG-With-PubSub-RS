/**
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * null
 **/
package smartWorkflowGUI;

import inria.smarttools.core.util.*;

/**
 **/
public class ReturnToEvent extends StEventImpl {
   //
   // Fields 
   //

   /**
    **/
   protected java.lang.String repliquePartielle;

   /**
    **/
   public void setRepliquePartielle(java.lang.String v){
      this.repliquePartielle = v;
   }

   public java.lang.String getRepliquePartielle(){
      return repliquePartielle;
   }

   //
   // Constructors 
   //

   /**
    * Constructor
    **/
   public   ReturnToEvent(java.lang.String repliquePartielle){
      setRepliquePartielle(repliquePartielle);
   }

   /**
    * Constructor
    **/
   public   ReturnToEvent(String adressee, java.lang.String repliquePartielle){
      super(adressee);
      setRepliquePartielle(repliquePartielle);
   }


   //
   // Methods 
   //

   /**
    * Return a short description of the ReturnToEvent object.
    * @return a value of the type 'String' : a string representation of this ReturnToEvent
    **/
   public  String toString(){
      String res = "ReturnToEvent";
      return res;
   }


}
