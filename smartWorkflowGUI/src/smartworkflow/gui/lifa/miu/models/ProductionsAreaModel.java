/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.models;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import smartworkflow.gui.lifa.miu.models.util.ModelModel;
import smartworkflow.gui.lifa.miu.util.editor.GrammarAndViews;

/**
 *
 * @author Ndadji Maxime
 */
public class ProductionsAreaModel extends ModelModel{

    public void read(DataFlavor[] types, Transferable transferable) {
        try{
            GrammarAndViews<String> value = config.readGrammarAndViews(types, transferable);
            notifyGrammarAndViews(value);
        }catch(Throwable e){
            notifyGrammarError(e.getMessage());
        }
    } 
}
