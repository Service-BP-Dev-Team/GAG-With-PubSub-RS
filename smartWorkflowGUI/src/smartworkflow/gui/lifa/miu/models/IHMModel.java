package smartworkflow.gui.lifa.miu.models;

import smartworkflow.gui.lifa.miu.models.util.ModelModel;
import smartworkflow.gui.lifa.miu.view.SettingGUIDialog;

public class IHMModel extends ModelModel{
    
    public IHMModel (){
        
    }

    public void editDesign() {
        SettingGUIDialog dialog = new SettingGUIDialog(config, true, null);
        dialog.showDialog();
        if(dialog.changed){
            notifyThemeChanged();
        }
    }
    
    public void startTask(String task){
        notifyTaskStarted(task);
    }
    
    public void stopTask(String task){
        notifyTaskEnded(task);
    }
}
