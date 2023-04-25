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
 * null
 * null
 **/
package smartWorkflowDWfMS;

import inria.smarttools.core.util.*;

/**
 **/
public class ForwardToEvent extends StEventImpl {
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
   public   ForwardToEvent(java.lang.String repliquePartielle){
      setRepliquePartielle(repliquePartielle);
   }

   /**
    * Constructor
    **/
   public   ForwardToEvent(String adressee, java.lang.String repliquePartielle){
      super(adressee);
      setRepliquePartielle(repliquePartielle);
   }


   //
   // Methods 
   //

   /**
    * Return a short description of the ForwardToEvent object.
    * @return a value of the type 'String' : a string representation of this ForwardToEvent
    **/
   public  String toString(){
      String res = "ForwardToEvent";
      return res;
   }


}
