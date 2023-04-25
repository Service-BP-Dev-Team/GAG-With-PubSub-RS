/**
 * null
 * null
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
package soaApp;

import inria.smarttools.core.util.*;

/**
 **/
public class ReturnToEvent extends StEventImpl {
   //
   // Fields 
   //

   /**
    **/
   protected gag.Term term;

   /**
    **/
   public void setTerm(gag.Term v){
      this.term = v;
   }

   public gag.Term getTerm(){
      return term;
   }

   //
   // Constructors 
   //

   /**
    * Constructor
    **/
   public   ReturnToEvent(gag.Term term){
      setTerm(term);
   }

   /**
    * Constructor
    **/
   public   ReturnToEvent(String adressee, gag.Term term){
      super(adressee);
      setTerm(term);
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
