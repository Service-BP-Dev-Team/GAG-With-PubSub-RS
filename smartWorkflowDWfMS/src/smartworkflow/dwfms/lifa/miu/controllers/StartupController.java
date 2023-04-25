/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.controllers;

import smartworkflow.dwfms.lifa.miu.controllers.util.ControllerModel;
import smartworkflow.dwfms.lifa.miu.models.StartupModel;

public class StartupController extends ControllerModel{

    public StartupController(StartupModel model) {
        super(model);
    }

    public void startupLoading() {
        ((StartupModel)model).startupLoading();
    }
}
