/**
 **/
package soaApp;

import inria.communicationprotocol.CommunicationProtocolContainer;
import java.util.List;
import java.util.ArrayList;
import inria.smarttools.core.component.*;
import gag.ServiceNode;
import gag.behaviour.SubscriptionTable;
import inria.smarttools.core.component.PropertyMap;
import java.lang.String;
import gag.Term;

/**
 **/
public class SoaAppContainer extends CommunicationProtocolContainer implements inria.smarttools.core.component.Container, soaApp.InvokeToListener, soaApp.ExitListener, soaApp.DisconnectListener, soaApp.InitDataListener, soaApp.UndoListener, soaApp.LogListener, soaApp.LogUndoListener, soaApp.ReturnToListener, soaApp.ConnectToListener, soaApp.BananeListener, soaApp.SendListener {
   {
      List<MethodCall> methodCalls;
      methodCalls = calls.get("invokeTo");
      if (methodCalls == null) {
         methodCalls = new ArrayList<MethodCall>();
         calls.put("invokeTo", methodCalls);
      }
      methodCalls.add(new MethodCall() {
         public Object call(ContainerProxy expeditor, String expeditorId, String expeditorType, PropertyMap parameters) {
            ((SoaAppFacade) facade).inInvokeTo(expeditorId, (gag.ServiceNode) parameters.get("service"), (gag.behaviour.SubscriptionTable) parameters.get("subscriptions"));
            return null;
         	}});
      methodCalls = calls.get("returnTo");
      if (methodCalls == null) {
         methodCalls = new ArrayList<MethodCall>();
         calls.put("returnTo", methodCalls);
      }
      methodCalls.add(new MethodCall() {
         public Object call(ContainerProxy expeditor, String expeditorId, String expeditorType, PropertyMap parameters) {
            ((SoaAppFacade) facade).inReturnTo(expeditorId, (gag.Term) parameters.get("term"));
            return null;
         	}});
      methodCalls = calls.get("disconnect");
      if (methodCalls == null) {
         methodCalls = new ArrayList<MethodCall>();
         calls.put("disconnect", methodCalls);
      }
      methodCalls.add(new MethodCall() {
         public Object call(ContainerProxy expeditor, String expeditorId, String expeditorType, PropertyMap parameters) {
            ((SoaAppFacade) facade).disconnectInput(expeditorId);
            return null;
         	}});
      methodCalls = calls.get("quit");
      if (methodCalls == null) {
         methodCalls = new ArrayList<MethodCall>();
         calls.put("quit", methodCalls);
      }
      methodCalls.add(new MethodCall() {
         public Object call(ContainerProxy expeditor, String expeditorId, String expeditorType, PropertyMap parameters) {
            ((SoaAppFacade) facade).quit(expeditorId);
            return null;
         	}});
      methodCalls = calls.get("shutdown");
      if (methodCalls == null) {
         methodCalls = new ArrayList<MethodCall>();
         calls.put("shutdown", methodCalls);
      }
      methodCalls.add(new MethodCall() {
         public Object call(ContainerProxy expeditor, String expeditorId, String expeditorType, PropertyMap parameters) {
            ((SoaAppFacade) facade).shutdown(expeditorId);
            return null;
         	}});
      methodCalls = calls.get("requestInitData");
      if (methodCalls == null) {
         methodCalls = new ArrayList<MethodCall>();
         calls.put("requestInitData", methodCalls);
      }
      methodCalls.add(new MethodCall() {
         public Object call(ContainerProxy expeditor, String expeditorId, String expeditorType, PropertyMap parameters) {
            Object res = ((SoaAppFacade) facade).requestInitData(expeditorId);
            if (res != null) {
               buildResponseForInOut(expeditor, expeditorId, expeditorType, getContainerDescription().getInOuts().get("requestInitData"), res);
            }
            return null;
         	}});
   }

   //
   // Constructors 
   //

   /**
    * Constructor
    **/
   public   SoaAppContainer(String name, String componentDescriptionResource){
      super(name, componentDescriptionResource);
   }


   //
   // Methods 
   //

   /**
    * createFacade()
    * Do NOT invoke super.createFacade()
    **/
   protected  void createFacade(){
      facade = new soaApp.SoaAppFacade(getIdName());
      initFacadeListeners();
   }

   /**
    * getFacade()

    * Cast to the proper facade class
    **/
   public  soaApp.SoaAppFacade getFacade(){
      return (soaApp.SoaAppFacade) facade;
   }

   /**
    * initFacadeListeners()
    **/
   protected  void initFacadeListeners(){
      super.initFacadeListeners();
      ((SoaAppFacadeInterface) facade).addInvokeToListener(this);
      ((SoaAppFacadeInterface) facade).addExitListener(this);
      ((SoaAppFacadeInterface) facade).addDisconnectListener(this);
      ((SoaAppFacadeInterface) facade).addInitDataListener(this);
      ((SoaAppFacadeInterface) facade).addUndoListener(this);
      ((SoaAppFacadeInterface) facade).addLogListener(this);
      ((SoaAppFacadeInterface) facade).addLogUndoListener(this);
      ((SoaAppFacadeInterface) facade).addReturnToListener(this);
      ((SoaAppFacadeInterface) facade).addConnectToListener(this);
      ((SoaAppFacadeInterface) facade).addBananeListener(this);
      ((SoaAppFacadeInterface) facade).addSendListener(this);
   }

   /**
    * InvokeToListener
    * @throws IllegalStateException to absolutely care in business methods
    **/
   public  void outInvokeTo(InvokeToEvent e){
      send(new MessageImpl("invokeTo", e.getAttributes() , null, e.getAdressee()));
   }

   /**
    * ExitListener
    * @throws IllegalStateException to absolutely care in business methods
    **/
   public  void exit(ExitEvent e){
      send(new MessageImpl("exit", e.getAttributes() , null, e.getAdressee()));
   }

   /**
    * DisconnectListener
    * @throws IllegalStateException to absolutely care in business methods
    **/
   public  void disconnectOut(DisconnectEvent e){
      send(new MessageImpl("disconnect", e.getAttributes() , null, e.getAdressee()));
   }

   /**
    * InitDataListener
    * @throws IllegalStateException to absolutely care in business methods
    **/
   public  void initData(InitDataEvent e){
      send(new MessageImpl("initData", e.getAttributes() , null, e.getAdressee()));
   }

   /**
    * UndoListener
    * @throws IllegalStateException to absolutely care in business methods
    **/
   public  void undo(UndoEvent e){
      send(new MessageImpl("undo", e.getAttributes() , null, e.getAdressee()));
   }

   /**
    * LogListener
    * @throws IllegalStateException to absolutely care in business methods
    **/
   public  void log(LogEvent e){
      send(new MessageImpl("log", e.getAttributes() , null, e.getAdressee()));
   }

   /**
    * LogUndoListener
    * @throws IllegalStateException to absolutely care in business methods
    **/
   public  void logUndo(LogUndoEvent e){
      send(new MessageImpl("logUndo", e.getAttributes() , null, e.getAdressee()));
   }

   /**
    * ReturnToListener
    * @throws IllegalStateException to absolutely care in business methods
    **/
   public  void outReturnTo(ReturnToEvent e){
      send(new MessageImpl("returnTo", e.getAttributes() , null, e.getAdressee()));
   }

   /**
    * ConnectToListener
    * @throws IllegalStateException to absolutely care in business methods
    **/
   public  void connectTo(ConnectToEvent e){
      send(new MessageImpl("connectTo", e.getAttributes() , null, e.getAdressee()));
   }

   /**
    * BananeListener
    * @throws IllegalStateException to absolutely care in business methods
    **/
   public  void outBanane(BananeEvent e){
      send(new MessageImpl("banane", e.getAttributes() , null, e.getAdressee()));
   }

   /**
    * SendListener
    * @throws IllegalStateException to absolutely care in business methods
    **/
   public  void send(SendEvent e){
      send(new MessageImpl("send", e.getAttributes() , null, e.getAdressee()));
   }

   /**
    *  Describe how to serialize this component
    **/
   public  void serialize(){
   }


}
