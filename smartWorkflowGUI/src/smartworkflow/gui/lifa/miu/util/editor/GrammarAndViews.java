/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.util.editor;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Ndadji Maxime
 */
public class GrammarAndViews<S> implements Serializable{
	private static final long serialVersionUID = 1L;

	public static final String
            TGF = ".tgf",
            XLS = ".xls",
            TXT = ".txt";
    
    private Grammar<S, S> grammar;
    private ArrayList<ArrayList<S>> views;

    public GrammarAndViews(Grammar<S, S> grammar, ArrayList<ArrayList<S>> views) {
        this.grammar = grammar;
        this.views = views;
    }

    public Grammar<S, S> getGrammar() {
        return grammar;
    }

    public void setGrammar(Grammar<S, S> grammar) {
        this.grammar = grammar;
    }

    public ArrayList<ArrayList<S>> getViews() {
        return views;
    }

    public void setViews(ArrayList<ArrayList<S>> views) {
        this.views = views;
    }
}
