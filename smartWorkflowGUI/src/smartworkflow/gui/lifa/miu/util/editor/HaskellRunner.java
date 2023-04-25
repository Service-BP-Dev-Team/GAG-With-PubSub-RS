/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.util.editor;

import java.io.IOException;

import smartworkflow.gui.lifa.miu.models.util.ModelModel;

/**
 *
 * @author Ndadji Maxime
 */
public final class HaskellRunner extends ILanguageRunner{

    public HaskellRunner() {
        super("Haskell", ModelModel.getConfig().getHaskellInterpreter());
    }

    @Override
    public String getExecResult() throws IOException {
        String execResult = super.getExecResult(), str;
        if(execResult == null)
            return null;
        String[] tab = execResult.split("Prelude> ");
        if(tab.length == 2)
            tab = tab[1].split("\\*[a-zA-Z0-9_]{1,}>");
        StringBuilder goodResult = new StringBuilder();
        for(int i = 1; i < tab.length - 1; i++){
            str = tab[i].trim();
            if(!str.isEmpty()){
                goodResult.append(str);
                goodResult.append("\n");
            }
        }
        return goodResult.toString();
    }

    @Override
    public void setCommand(String command) {
        
    }

    @Override
    public void setLanguage(String language) {
        
    }
    
    public String test() throws IOException {
        return executeCode("\nlet fn x = x\nfn 6");
    }
}
