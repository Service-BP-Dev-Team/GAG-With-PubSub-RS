/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.controllers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import smartworkflow.gui.lifa.miu.models.ProductionsAreaModel;

/**
 *
 * @author Ndadji Maxime
 */
public class ProductionsAreaController {
    private ProductionsAreaModel model;
    
    public ProductionsAreaController(ProductionsAreaModel model){
        this.model = model;
    }

    public ProductionsAreaModel getModel() {
        return model;
    }

    public void read(DataFlavor[] types, Transferable transferable) {
        model.read(types, transferable);
    }
}
