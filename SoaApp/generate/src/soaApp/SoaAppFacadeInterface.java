/**
 **/
package soaApp;


/**
 **/
public interface SoaAppFacadeInterface {
   //
   // Methods 
   //

   /**
    * invokeTo
    * null
    * @param expeditor is the component name who sent this message
    **/
   public  void inInvokeTo(String expeditor, gag.ServiceNode service, gag.behaviour.SubscriptionTable subscriptions);

   /**
    * returnTo
    * null
    * @param expeditor is the component name who sent this message
    **/
   public  void inReturnTo(String expeditor, gag.Term term);

   /**
    * disconnect
    * disconnect
    * @param expeditor is the component name who sent this message
    **/
   public  void disconnectInput(String expeditor);

   /**
    * quit
    * quit
    * @param expeditor is the component name who sent this message
    **/
   public  void quit(String expeditor);

   /**
    * shutdown
    * shutdown
    * @param expeditor is the component name who sent this message
    **/
   public  void shutdown(String expeditor);

   /**
    * requestInitData
    * 
    * @param expeditor is the component name who sent this message
    **/
   public  Object requestTree(String expeditor);

   /**
    * invokeTo
    * null
    **/
   public  void addInvokeToListener(InvokeToListener data);

   /**
    * invokeTo
    * null
    **/
   public  void removeInvokeToListener(InvokeToListener data);

   /**
    * exit
    * 
    **/
   public  void addExitListener(ExitListener data);

   /**
    * exit
    * 
    **/
   public  void removeExitListener(ExitListener data);

   /**
    * disconnect
    * 
    **/
   public  void addDisconnectListener(DisconnectListener data);

   /**
    * disconnect
    * 
    **/
   public  void removeDisconnectListener(DisconnectListener data);

   /**
    * initData
    * 
    **/
   public  void addInitDataListener(InitDataListener data);

   /**
    * initData
    * 
    **/
   public  void removeInitDataListener(InitDataListener data);

   /**
    * undo
    * 
    **/
   public  void addUndoListener(UndoListener data);

   /**
    * undo
    * 
    **/
   public  void removeUndoListener(UndoListener data);

   /**
    * log
    * 
    **/
   public  void addLogListener(LogListener data);

   /**
    * log
    * 
    **/
   public  void removeLogListener(LogListener data);

   /**
    * logUndo
    * 
    **/
   public  void addLogUndoListener(LogUndoListener data);

   /**
    * logUndo
    * 
    **/
   public  void removeLogUndoListener(LogUndoListener data);

   /**
    * returnTo
    * null
    **/
   public  void addReturnToListener(ReturnToListener data);

   /**
    * returnTo
    * null
    **/
   public  void removeReturnToListener(ReturnToListener data);

   /**
    * connectTo
    * 
    **/
   public  void addConnectToListener(ConnectToListener data);

   /**
    * connectTo
    * 
    **/
   public  void removeConnectToListener(ConnectToListener data);

   /**
    * banane
    * null
    **/
   public  void addBananeListener(BananeListener data);

   /**
    * banane
    * null
    **/
   public  void removeBananeListener(BananeListener data);

   /**
    * send
    * 
    **/
   public  void addSendListener(SendListener data);

   /**
    * send
    * 
    **/
   public  void removeSendListener(SendListener data);


}
