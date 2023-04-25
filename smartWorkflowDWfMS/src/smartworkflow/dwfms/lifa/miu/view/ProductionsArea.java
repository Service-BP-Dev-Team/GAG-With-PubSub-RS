/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.view;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JTextArea;

import smartworkflow.dwfms.lifa.miu.controllers.ProductionsAreaController;
import smartworkflow.dwfms.lifa.miu.models.ProductionsAreaModel;
import smartworkflow.dwfms.lifa.miu.util.editor.GrammarAndViews;
import smartworkflow.dwfms.lifa.miu.util.observer.Observer;
import smartworkflow.dwfms.lifa.miu.util.observer.adapters.ObserverAdapter;

/**
 *
 * @author Ndadji Maxime
 */
public class ProductionsArea extends JTextArea{
	private static final long serialVersionUID = 1L;
	private DropTargetListener dropListener;
    private ProductionsAreaController controller;
    private Updater updater;
    
    public ProductionsArea(Observer observer){
        updater = new Updater();
        ProductionsAreaModel model = new ProductionsAreaModel();
        model.addObserver(updater);
        model.addObserver(observer);
        controller = new ProductionsAreaController(model);
        
        dropListener = new DropTargetAdapter(){

            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                selectAll();
                if (!isDragAcceptable(dtde)) dtde.rejectDrag();
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {
                if (!isDragAcceptable(dtde))  dtde.rejectDrag();
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                if (!isDropAcceptable(dtde)) {
                    dtde.rejectDrop();
                    return;
                }
                
                dtde.acceptDrop(DnDConstants.ACTION_COPY);
                Transferable transferable = dtde.getTransferable();
                DataFlavor[] types = transferable.getTransferDataFlavors();
                
                controller.read(types, transferable);
                dtde.dropComplete(true);
                requestFocusInWindow();
            }
            
        };
        
        DropTarget target = new DropTarget(this, dropListener);
        target.setActive(true);
    }
    public boolean isDragAcceptable(DropTargetDragEvent dtde) {
        return (dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
    }

    public boolean isDropAcceptable(DropTargetDropEvent dtde) {
        return (dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0;
    }   
    
    public class Updater extends ObserverAdapter{

        public Updater() {
        }

        @Override
        public void updateGrammarAndViews(GrammarAndViews<String> gramAndViews) {
            setText(gramAndViews.getGrammar().getGrammar());
        }
    }
}
