/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.gui.lifa.miu.util.editor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Ndadji Maxime
 */
public class Grammar<P, S> implements Serializable{
	private static final long serialVersionUID = 1L;
	private ArrayList<S> symbols;
    private S axiom;
    private HashMap<P, ArrayList<S>> productions;

    public Grammar(){
        symbols = new ArrayList<S>();
        productions = new HashMap<P, ArrayList<S>>();
    }

    public Grammar(ArrayList<S> symbols, HashMap<P, ArrayList<S>> productions) {
        this.symbols = symbols;
        this.productions = productions;
    }
    
    public HashMap<P, ArrayList<S>> getProductions() {
        return productions;
    }

    public void setProductions(HashMap<P, ArrayList<S>> productions) {
        this.productions = productions;
    }

    public ArrayList<S> getSymbols() {
        return symbols;
    }

    public void setSymbols(ArrayList<S> symbols) {
        this.symbols = symbols;
    }
    
    public ArrayList<S> getProduction(P prodKey){
        return productions.get(prodKey);
    }

    public S getAxiom() {
        return axiom;
    }

    public void setAxiom(S axiom) {
        this.axiom = axiom;
    }
    
    public S lhs(P prodKey){
        ArrayList<S> p = getProduction(prodKey);
        return (p != null && !p.isEmpty()) ? p.get(0) : null;
    }
    
    public ArrayList<S> rhs(P prodKey){
        ArrayList<S> p = getProduction(prodKey), list = null;
        if(p != null){
            list = new ArrayList<S>();
            for(int i = 1; i < p.size(); i++)
                list.add(p.get(i));
        }
        return list;
    }
    
    public int rhsLength(P prodKey){
        ArrayList<S> p = getProduction(prodKey);
        return (p != null && !p.isEmpty()) ? p.size() - 1 : 0;
    }
    
    public void addSymbol(S symb){
        if(!symbols.contains(symb))
            symbols.add(0, symb);
    }
    
    public void addProduction(P prod, S lhs, ArrayList<S> rhs) throws Exception{
        ArrayList<S> p = new ArrayList<S>();
        p.add(lhs);
        p.addAll(1, rhs);
        addProduction(prod, p);
    }
    
    public void addProduction(P prod, ArrayList<S> symbs) throws Exception{
        boolean strangeSymb = false;
        for(S s : symbs){
            if(!symbols.contains(s)){
                strangeSymb = true;
                break;
            }
        }
        if(!strangeSymb)
            productions.put(prod, symbs);
        else
            throw new Exception();
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Grammar<P, S> other = (Grammar<P, S>) obj;
        if(!axiom.equals(other.axiom))
            return false;
        
        if((symbols == null && other.symbols != null) || (symbols != null && other.symbols == null) || 
                (symbols != null && other.symbols != null && symbols.size() != other.symbols.size()))
            return false;
        
        for(S symb : symbols)
            if(!other.symbols.contains(symb))
                return false;
        
        if((productions == null && other.productions != null) || (productions != null && other.productions == null) || 
                (productions != null && other.productions != null && productions.keySet().size() != other.productions.keySet().size()))
            return false;
        
        Set<P> prods = productions.keySet();
        Set<P> otherProds = other.productions.keySet();
        for(P prod : prods){
            boolean isIn = false;
            for(P otherProd : otherProds){
                if(productions.get(prod).equals(other.productions.get(otherProd))){
                    isIn = true;
                    break;
                }
            }
            if(!isIn)
                return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (this.symbols != null ? this.symbols.hashCode() : 0);
        hash = 23 * hash + (this.axiom != null ? this.axiom.hashCode() : 0);
        hash = 23 * hash + (this.productions != null ? this.productions.hashCode() : 0);
        return hash;
    }
    
    public String getGrammar(){
        String gram = "";
        Set<P> prods = productions.keySet();
        for(S sy : getSymbolsAxiomTop()){
            for(P prod : prods){
                if(lhs(prod).equals(sy)){
                    gram += lhs(prod)+" ->";
                    if(rhsLength(prod) == 0)
                        gram += " £\n";
                    else{
                        ArrayList<S> rh = rhs(prod);
                        for(S symb : rh)
                            gram += " "+symb;
                        gram += "\n";
                    }
                }
            }
        }
        return gram.substring(0, gram.length() - 1);
    }
    
    public ArrayList<S> getSymbolsAxiomTop() {
        ArrayList<S> list = new ArrayList<S>();
        list.add(axiom);
        for(S symb : symbols){
            if(!symb.equals(axiom))
                list.add(symb);
        }
        return list;
    }
    
    public boolean isSymbol(S symb){
        if(symbols != null)
            return symbols.contains(symb);
        return false;
    }
    
    public boolean isProduction(P prod){
        if(productions != null)
            return productions.keySet().contains(prod);
        return false;
    }
    
    public boolean isProduction(ArrayList<S> prod){
        Set<P> prods = productions.keySet();
        for(P p : prods)
            if(productions.get(p).equals(prod))
                return true;
        return false;
    }
    
    public P getProduction(ArrayList<S> prod){
        Set<P> prods = productions.keySet();
        for(P p : prods)
            if(productions.get(p).equals(prod))
                return p;
        return null;
    }
}
