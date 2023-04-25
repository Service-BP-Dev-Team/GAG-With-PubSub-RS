package smartworkflow.gui.lifa.miu.view.util;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import smartworkflow.gui.lifa.miu.controllers.util.ControllerModel;
import smartworkflow.gui.lifa.miu.util.ConfigurationManager;
import smartworkflow.gui.lifa.miu.util.exceptions.ElementNotFoundException;
import smartworkflow.gui.lifa.miu.util.observer.Observer;

/**
 *
 * @author Ndadji Maxime
 */
public abstract class FrameModel extends JFrame{
	private static final long serialVersionUID = 1L;
	protected List<Observer> updaterList;
    protected static ControllerModel controller;
    protected static ConfigurationManager config;
    
    public FrameModel(){
        updaterList = new ArrayList<Observer>();
    }
    
    public FrameModel(ControllerModel controller){
        updaterList = new ArrayList<Observer>();
        FrameModel.controller = controller;
    }
    
    public FrameModel(List<Observer> updaterList){
        this.updaterList = updaterList;
    }
    
    public FrameModel(List<Observer> updaterList, ControllerModel controller){
        this.updaterList = updaterList;
        FrameModel.controller = controller;
    }
    
    public void addUpdater(Observer observer) {
        if(!this.updaterList.contains(observer)){
            this.updaterList.add(observer);
            FrameModel.controller.getModel().addObserver(observer);
        }
    }

    public void removeUpdater(Observer observer) throws ElementNotFoundException {
        try{
            this.updaterList.remove(observer);
            FrameModel.controller.getModel().removeObserver(observer);
        }
        catch(Exception e){
            throw new ElementNotFoundException(e.getMessage());
        }
    }
    
    protected abstract void initComponents();

    public static void setController(ControllerModel controller) {
    	FrameModel.controller = controller;
    }
}
