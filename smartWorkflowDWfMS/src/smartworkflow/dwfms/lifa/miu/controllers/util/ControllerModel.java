/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.controllers.util;

import smartworkflow.dwfms.lifa.miu.util.ConfigurationManager;
import smartworkflow.dwfms.lifa.miu.util.observer.Observable;


/**
 *
 * @author Ndadji Maxime
 */
public abstract class ControllerModel {
    
    protected Observable model;
    protected static ConfigurationManager config;
    
    public ControllerModel(){
        
    }
    
    public ControllerModel(Observable model){
        this.model = model;
    }

    public Observable getModel() {
        return model;
    }

    public void setModel(Observable model) {
        this.model = model;
    }
}
