/**
 **/
package smartWorkflowGUI;

import inria.communicationprotocol.CommunicationProtocolContainer;
import java.util.List;
import java.util.ArrayList;
import inria.smarttools.core.component.*;
import inria.smarttools.core.component.PropertyMap;
import java.lang.String;

/**
 **/
public class SmartWorkflowGUIContainer extends CommunicationProtocolContainer implements inria.smarttools.core.component.Container, smartWorkflowGUI.ExitListener, smartWorkflowGUI.DisconnectListener, smartWorkflowGUI.InitDataListener, smartWorkflowGUI.UndoListener, smartWorkflowGUI.LogListener, smartWorkflowGUI.LogUndoListener, smartWorkflowGUI.ReturnToListener, smartWorkflowGUI.ForwardToListener, smartWorkflowGUI.ConnectToListener, smartWorkflowGUI.SendListener {
   {
      List<MethodCall> methodCalls;
      methodCalls = calls.get("returnTo");
      if (methodCalls == null) {
         methodCalls = new ArrayList<MethodCall>();
         calls.put("returnTo", methodCalls);
      }
      methodCalls.add(new MethodCall() {
         public Object call(ContainerProxy expeditor, String expeditorId, String expeditorType, PropertyMap parameters) {
            ((SmartWorkflowGUIFacade) facade).inReturnTo(expeditorId, (java.lang.String) parameters.get("repliquePartielle"));
            return null;
         	}});
      methodCalls = calls.get("disconnect");
      if (methodCalls == null) {
         methodCalls = new ArrayList<MethodCall>();
         calls.put("disconnect", methodCalls);
      }
      methodCalls.add(new MethodCall() {
         public Object call(ContainerProxy expeditor, String expeditorId, String expeditorType, PropertyMap parameters) {
            ((SmartWorkflowGUIFacade) facade).disconnectInput(expeditorId);
            return null;
         	}});
      methodCalls = calls.get("quit");
      if (methodCalls == null) {
         methodCalls = new ArrayList<MethodCall>();
         calls.put("quit", methodCalls);
      }
      methodCalls.add(new MethodCall() {
         public Object call(ContainerProxy expeditor, String expeditorId, String expeditorType, PropertyMap parameters) {
            ((SmartWorkflowGUIFacade) facade).quit(expeditorId);
            return null;
         	}});
      methodCalls = calls.get("forwardTo");
      if (methodCalls == null) {
         methodCalls = new ArrayList<MethodCall>();
         calls.put("forwardTo", methodCalls);
      }
      methodCalls.add(new MethodCall() {
         public Object call(ContainerProxy expeditor, String expeditorId, String expeditorType, PropertyMap parameters) {
            ((SmartWorkflowGUIFacade) facade).inForwardTo(expeditorId, (java.lang.String) parameters.get("repliquePartielle"));
            return null;
         	}});
      methodCalls = calls.get("shutdown");
      if (methodCalls == null) {
         methodCalls = new ArrayList<MethodCall>();
         calls.put("shutdown", methodCalls);
      }
      methodCalls.add(new MethodCall() {
         public Object call(ContainerProxy expeditor, String expeditorId, String expeditorType, PropertyMap parameters) {
            ((SmartWorkflowGUIFacade) facade).shutdown(expeditorId);
            return null;
         	}});
      methodCalls = calls.get("requestInitData");
      if (methodCalls == null) {
         methodCalls = new ArrayList<MethodCall>();
         calls.put("requestInitData", methodCalls);
      }
      methodCalls.add(new MethodCall() {
         public Object call(ContainerProxy expeditor, String expeditorId, String expeditorType, PropertyMap parameters) {
            Object res = ((SmartWorkflowGUIFacade) facade).requestInitData(expeditorId);
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
   public   SmartWorkflowGUIContainer(String name, String componentDescriptionResource){
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
      facade = new smartWorkflowGUI.SmartWorkflowGUIFacade(getIdName());
      initFacadeListeners();
   }

   /**
    * getFacade()

    * Cast to the proper facade class
    **/
   public  smartWorkflowGUI.SmartWorkflowGUIFacade getFacade(){
      return (smartWorkflowGUI.SmartWorkflowGUIFacade) facade;
   }

   /**
    * initFacadeListeners()
    **/
   protected  void initFacadeListeners(){
      super.initFacadeListeners();
      ((SmartWorkflowGUIFacadeInterface) facade).addExitListener(this);
      ((SmartWorkflowGUIFacadeInterface) facade).addDisconnectListener(this);
      ((SmartWorkflowGUIFacadeInterface) facade).addInitDataListener(this);
      ((SmartWorkflowGUIFacadeInterface) facade).addUndoListener(this);
      ((SmartWorkflowGUIFacadeInterface) facade).addLogListener(this);
      ((SmartWorkflowGUIFacadeInterface) facade).addLogUndoListener(this);
      ((SmartWorkflowGUIFacadeInterface) facade).addReturnToListener(this);
      ((SmartWorkflowGUIFacadeInterface) facade).addForwardToListener(this);
      ((SmartWorkflowGUIFacadeInterface) facade).addConnectToListener(this);
      ((SmartWorkflowGUIFacadeInterface) facade).addSendListener(this);
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
    * ForwardToListener
    * @throws IllegalStateException to absolutely care in business methods
    **/
   public  void outForwardTo(ForwardToEvent e){
      send(new MessageImpl("forwardTo", e.getAttributes() , null, e.getAdressee()));
   }

   /**
    * ConnectToListener
    * @throws IllegalStateException to absolutely care in business methods
    **/
   public  void connectTo(ConnectToEvent e){
      send(new MessageImpl("connectTo", e.getAttributes() , null, e.getAdressee()));
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
