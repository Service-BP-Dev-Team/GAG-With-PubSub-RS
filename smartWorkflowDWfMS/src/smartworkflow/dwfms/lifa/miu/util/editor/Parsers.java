/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.util.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import smartworkflow.dwfms.lifa.miu.models.util.ModelModel;

import com.mxgraph.layout.mxCompactTreeLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxDomUtils;
import com.mxgraph.view.mxGraph;

/**
 *
 * @author Ndadji Maxime
 */
public class Parsers {
    public static ArrayList<String> toArrayList(String forest){
        if(forest == null)
            return null;
        ArrayList<String> list = new ArrayList<String>();
        forest = forest.substring(1);
        forest = forest.substring(0, forest.length() - 1);
        int openParenth = 0;
        String tree = "";
        boolean begin = true;
        for(int i = 0; i < forest.length(); i++){
            tree += forest.charAt(i);
            if(forest.charAt(i) == '['){
                openParenth++;
                begin = false;
            }
            if(forest.charAt(i) == ']')
                openParenth--;
            if(openParenth == 0 && !begin){
                if(tree.startsWith(","))
                    tree = tree.substring(1);
                list.add(tree);
                tree = "";
                begin = true;
            }
        }
        return list;
    }
    
    public static mxGraphComponent astToDerMxGraph(String haskellTree, Grammar<String, String> gram){
        if(haskellTree == null)
            return null;
        
        ArrayList<Object> vertexes = new ArrayList<Object>(), sons = new ArrayList<Object>()
                , astSons = new ArrayList<Object>();
        Stack<Object> evalStack = new Stack<Object>();
        Stack<Object> astStack = new Stack<Object>();
        Document xmlDocument = mxDomUtils.createDocument();
        Element node = null;
        mxGraphComponent graphComponent;
        mxGraph graph = new mxGraph();
        graph.setStylesheet(ModelModel.getConfig().getTheme().getmxGraphStylesheet());
        
	Object parent = graph.getDefaultParent(), obj = null, otherObj = null, otherAstObj = null;
        int i = 0;
        String nodeName = "", prodName;
        graph.getModel().beginUpdate();
        try{
            while(i < haskellTree.length()){
                nodeName = "";
                for(; (i < haskellTree.length()) && (haskellTree.charAt(i) != '[') && (haskellTree.charAt(i) != ']'); i++)
                    nodeName += haskellTree.charAt(i);
                if(nodeName.startsWith(","))
                    nodeName = nodeName.substring(1);
                nodeName = nodeName.trim();
                prodName = nodeName;
                if(nodeName.equalsIgnoreCase("bud") || nodeName.equalsIgnoreCase("omeg")){
                    Stack<Object> otherStack = new Stack<Object>(), otherAstStack = new Stack<Object>();
                    while(!evalStack.empty() && !(evalStack.peek() == "[")){
                        otherStack.push(evalStack.pop());
                        otherAstStack.push(astStack.pop());
                    }
                    if(evalStack.empty())
                        nodeName = gram.getAxiom()+"w";
                    else{
                        otherStack.push(evalStack.pop());
                        otherAstStack.push(astStack.pop());
                        nodeName = gram.rhs(((Element)astStack.peek()).getNodeName()).get(otherAstStack.size() - 1)+"w";
                        while(!otherStack.isEmpty()){
                            evalStack.push(otherStack.pop());
                            astStack.push(otherAstStack.pop());
                        }
                    }
                }else{
                    if(!nodeName.isEmpty())
                        nodeName = gram.lhs(nodeName);
                }
                if(!nodeName.isEmpty()){
                    node = xmlDocument.createElement(nodeName);
                    obj = graph.insertVertex(parent, null, node.getNodeName(), 0, 0, 25, 25, 
                            node.getNodeName().endsWith("w") ? ModelModel.getConfig().getTheme().getmxGraphBudStyle() : ModelModel.getConfig().getTheme().getmxGraphStyle());
                    vertexes.add(obj);
                    evalStack.push(obj);
                    evalStack.push("[");
                    
                    node = xmlDocument.createElement(prodName);
                    astStack.push(node);
                    astStack.push("[");
                }else{
                    otherObj = evalStack.pop();
                    otherAstObj = astStack.pop();
                    sons = new ArrayList<Object>();
                    astSons = new ArrayList<Object>();
                    while(!(otherObj instanceof String)){
                        sons.add(0, otherObj);
                        otherObj = evalStack.pop();
                        astSons.add(0, otherAstObj);
                        otherAstObj = astStack.pop();
                    }
                    otherObj = evalStack.pop();
                    otherAstObj = astStack.pop();
                    for(Object son : sons)
                        graph.insertEdge(parent, null, "", otherObj, son);
                    evalStack.push(otherObj);
                    astStack.push(otherAstObj);
                }
                i++;
            }
        }finally
        {
            graph.getModel().endUpdate();
        }
        
        graph.setCellsResizable(false);
        graph.setMultigraph(true);
        graph.setStylesheet(ModelModel.getConfig().getTheme().getmxGraphStylesheet());
        
        mxCompactTreeLayout layout = new mxCompactTreeLayout(graph, false);
        layout.setLevelDistance(15);
        layout.setNodeDistance(5);
        layout.execute(parent);
        
        graphComponent = new mxGraphComponent(graph);
        graphComponent.setConnectable(false);
        graphComponent.setToolTips(true);
        graphComponent.setBorder(null);
        
        return graphComponent;
    }
    
    public static mxGraphComponent astToDerMxGraphWithoutBud(String haskellTree, Grammar<String, String> gram){
        if(haskellTree == null)
            return null;
        
        ArrayList<Object> vertexes = new ArrayList<Object>(), sons = new ArrayList<Object>();
        Stack<Object> evalStack = new Stack<Object>();
        Document xmlDocument = mxDomUtils.createDocument();
        Element node = null;
        mxGraphComponent graphComponent;
        mxGraph graph = new mxGraph();
        graph.setStylesheet(ModelModel.getConfig().getTheme().getmxGraphStylesheet());
        
	Object parent = graph.getDefaultParent(), obj = null, otherObj = null;
        int i = 0;
        String nodeName = "";
        graph.getModel().beginUpdate();
        try{
            while(i < haskellTree.length()){
                nodeName = "";
                for(; (i < haskellTree.length()) && (haskellTree.charAt(i) != '[') && (haskellTree.charAt(i) != ']'); i++)
                    nodeName += haskellTree.charAt(i);
                if(nodeName.startsWith(","))
                    nodeName = nodeName.substring(1);
                nodeName = nodeName.trim();
                if(nodeName.endsWith("omega")){
                    nodeName = nodeName.substring(0, nodeName.length() - 5) + "w";
                }else{
                    if(!nodeName.isEmpty())
                        nodeName = gram.lhs(nodeName);
                }
                if(!nodeName.isEmpty()){
                    node = xmlDocument.createElement(nodeName);
                    obj = graph.insertVertex(parent, null, node.getNodeName(), 0, 0, 25, 25, 
                            node.getNodeName().endsWith("w") ? ModelModel.getConfig().getTheme().getmxGraphBudStyle() : ModelModel.getConfig().getTheme().getmxGraphStyle());
                    vertexes.add(obj);
                    evalStack.push(obj);
                    evalStack.push("[");
                }else{
                    otherObj = evalStack.pop();
                    sons = new ArrayList<Object>();
                    while(!(otherObj instanceof String)){
                        sons.add(0, otherObj);
                        otherObj = evalStack.pop();
                    }
                    otherObj = evalStack.pop();
                    for(Object son : sons)
                        graph.insertEdge(parent, null, "", otherObj, son);
                    evalStack.push(otherObj);
                }
                i++;
            }
        }finally
        {
            graph.getModel().endUpdate();
        }
        
        graph.setCellsResizable(false);
        graph.setMultigraph(true);
        graph.setStylesheet(ModelModel.getConfig().getTheme().getmxGraphStylesheet());
        
        mxCompactTreeLayout layout = new mxCompactTreeLayout(graph, false);
        layout.setLevelDistance(15);
        layout.setNodeDistance(5);
        layout.execute(parent);
        
        graphComponent = new mxGraphComponent(graph);
        graphComponent.setConnectable(false);
        graphComponent.setToolTips(true);
        graphComponent.setBorder(null);
        
        return graphComponent;
    }
    
    public static mxGraphComponent astToMxGraph(String haskellTree, Grammar<String, String> gram){
        if(haskellTree == null)
            return null;
        
        ArrayList<Object> vertexes = new ArrayList<Object>(), sons = new ArrayList<Object>();
        Stack<Object> evalStack = new Stack<Object>();
        Document xmlDocument = mxDomUtils.createDocument();
        Element node = null;
        mxGraphComponent graphComponent;
        mxGraph graph = new mxGraph();
        graph.setStylesheet(ModelModel.getConfig().getTheme().getmxGraphStylesheet());
        
	Object parent = graph.getDefaultParent(), obj = null, otherObj = null;
        int i = 0;
        String nodeName = "";
        graph.getModel().beginUpdate();
        try{
            while(i < haskellTree.length()){
                nodeName = "";
                for(; (i < haskellTree.length()) && (haskellTree.charAt(i) != '[') && (haskellTree.charAt(i) != ']'); i++)
                    nodeName += haskellTree.charAt(i);
                if(nodeName.startsWith(","))
                    nodeName = nodeName.substring(1);
                nodeName = nodeName.trim();
                if(nodeName.equalsIgnoreCase("bud") || nodeName.equalsIgnoreCase("omeg")){
                    Stack<Object> otherStack = new Stack<Object>();
                    while(!evalStack.empty() && !(evalStack.peek() == "[")){
                        otherStack.push(evalStack.pop());
                    }
                    if(evalStack.empty())
                        nodeName = gram.getAxiom()+"w";
                    else{
                        otherStack.push(evalStack.pop());
                        nodeName = gram.rhs((String)(((mxCell)evalStack.peek()).getValue())).get(otherStack.size() - 1)+"w";
                        while(!otherStack.isEmpty()){
                            evalStack.push(otherStack.pop());
                        }
                    }
                }
                if(!nodeName.isEmpty()){
                    node = xmlDocument.createElement(nodeName);
                    obj = graph.insertVertex(parent, null, node.getNodeName(), 0, 0, 25, 25,
                            node.getNodeName().endsWith("w") ? ModelModel.getConfig().getTheme().getmxGraphBudStyle() : ModelModel.getConfig().getTheme().getmxGraphStyle());
                    vertexes.add(obj);
                    evalStack.push(obj);
                    evalStack.push("[");
                }else{
                    otherObj = evalStack.pop();
                    sons = new ArrayList<Object>();
                    while(!(otherObj instanceof String)){
                        sons.add(0, otherObj);
                        otherObj = evalStack.pop();
                    }
                    otherObj = evalStack.pop();
                    for(Object son : sons)
                        graph.insertEdge(parent, null, "", otherObj, son);
                    evalStack.push(otherObj);
                }
                i++;
            }
        }finally
        {
            graph.getModel().endUpdate();
        }
        
        graph.setCellsResizable(false);
        graph.setMultigraph(true);
        graph.setStylesheet(ModelModel.getConfig().getTheme().getmxGraphStylesheet());
        
        mxCompactTreeLayout layout = new mxCompactTreeLayout(graph, false);
        layout.setLevelDistance(15);
        layout.setNodeDistance(5);
        layout.execute(parent);
        
        graphComponent = new mxGraphComponent(graph);
        graphComponent.setConnectable(false);
        graphComponent.setToolTips(true);
        graphComponent.setBorder(null);
        
        return graphComponent;
    }
    
    public static mxGraphComponent astToMxGraphWithoutBud(String haskellTree){
        if(haskellTree == null)
            return null;
        
        ArrayList<Object> vertexes = new ArrayList<Object>(), sons = new ArrayList<Object>();
        Stack<Object> evalStack = new Stack<Object>();
        Document xmlDocument = mxDomUtils.createDocument();
        Element node = null;
        mxGraphComponent graphComponent;
        mxGraph graph = new mxGraph();
        graph.setStylesheet(ModelModel.getConfig().getTheme().getmxGraphStylesheet());
        
	Object parent = graph.getDefaultParent(), obj = null, otherObj = null;
        int i = 0;
        String nodeName = "";
        graph.getModel().beginUpdate();
        try{
            while(i < haskellTree.length()){
                nodeName = "";
                for(; (i < haskellTree.length()) && (haskellTree.charAt(i) != '[') && (haskellTree.charAt(i) != ']'); i++)
                    nodeName += haskellTree.charAt(i);
                if(nodeName.startsWith(","))
                    nodeName = nodeName.substring(1);
                nodeName = nodeName.trim();
                if(nodeName.endsWith("omega")){
                    nodeName = nodeName.substring(0, nodeName.length() - 5) + "w";
                }
                if(!nodeName.isEmpty()){
                    node = xmlDocument.createElement(nodeName);
                    obj = graph.insertVertex(parent, null, node.getNodeName(), 0, 0, 25, 25,
                            node.getNodeName().endsWith("w") ? ModelModel.getConfig().getTheme().getmxGraphBudStyle() : ModelModel.getConfig().getTheme().getmxGraphStyle());
                    vertexes.add(obj);
                    evalStack.push(obj);
                    evalStack.push("[");
                }else{
                    otherObj = evalStack.pop();
                    sons = new ArrayList<Object>();
                    while(!(otherObj instanceof String)){
                        sons.add(0, otherObj);
                        otherObj = evalStack.pop();
                    }
                    otherObj = evalStack.pop();
                    for(Object son : sons)
                        graph.insertEdge(parent, null, "", otherObj, son);
                    evalStack.push(otherObj);
                }
                i++;
            }
        }finally
        {
            graph.getModel().endUpdate();
        }
        
        graph.setCellsResizable(false);
        graph.setMultigraph(true);
        graph.setStylesheet(ModelModel.getConfig().getTheme().getmxGraphStylesheet());
        
        mxCompactTreeLayout layout = new mxCompactTreeLayout(graph, false);
        layout.setLevelDistance(15);
        layout.setNodeDistance(5);
        layout.execute(parent);
        
        graphComponent = new mxGraphComponent(graph);
        graphComponent.setConnectable(false);
        graphComponent.setToolTips(true);
        graphComponent.setBorder(null);
        
        return graphComponent;
    }
    
    public static String linearizeMxGraph(mxGraph graph, Object cell){
        String linearGraph = "";
        Object parent = graph.getDefaultParent();
        mxIGraphModel model = graph.getModel();
        if(cell == null)
            cell = model.getChildAt(parent, 0);
        if(model.isVertex(cell) && graph.isCellVisible(cell))
        {
            linearGraph = ((mxCell)cell).getValue()+"[";
            int edgeCount = ((mxCell)cell).getEdgeCount();
            ArrayList<Object> validEdges = new ArrayList<Object>();
            Object src;
            for(int j = 0; j < edgeCount; j++){
                src = ((mxCell)cell).getEdgeAt(j);
                if(((mxCell)src).getTarget() != cell)
                    validEdges.add(src);
            }
            int validEdgesLength = validEdges.size();
            for(int j = 0; j < validEdgesLength; j++)
            {
                linearGraph += linearizeMxGraph(graph, ((mxCell)(validEdges.get(j))).getTarget());
                if(j != validEdgesLength - 1)
                    linearGraph += ",";
            }
            linearGraph += "]";
        }
        return linearGraph;
    }
    
    public static String fromMxGraph(mxGraph graph, Grammar<String, String> gram){
        String haskellGraph = linearizeMxGraph(graph, null);
        return fromLinearizedDerMxGraph(astToDer(haskellGraph, gram), gram);
    }
    
    public static String fromDerMxGraph(mxGraph graph, Grammar<String, String> gram){
        String haskellGraph = linearizeMxGraph(graph, null);
        return fromLinearizedDerMxGraph(haskellGraph, gram);
    }
    
    public static String fromLinearizedDerMxGraph(String graph, Grammar<String, String> gram){
        String haskellGraph = graph;
        ArrayList<String> symbs = gram.getSymbols();
        for(String s : symbs){
            haskellGraph = haskellGraph.replace(s+"[", "BTree "+s+" (Just [");
            haskellGraph = haskellGraph.replace(s+"w[]", "BTree "+s+" (Nothing)");
            haskellGraph = haskellGraph.replace(s+"omega[]", "BTree "+s+" (Nothing)");
        }
        haskellGraph = haskellGraph.replace("]", "])");
        return haskellGraph;
    }
    
    public static String fromLinearizedDerMxGraphWithoutRoot(String graph, Grammar<String, String> gram){
        String haskellGraph = graph.substring(graph.indexOf("[") + 1, graph.length() - 1);
        return "["+fromLinearizedDerMxGraph(haskellGraph, gram)+"]";
    }
    
    public static String astToDer(String haskellGraph, Grammar<String, String> gram){
        String derGraph = haskellGraph;
        ArrayList<String> prods = new ArrayList<String>();
        prods.addAll(gram.getProductions().keySet());
        for(String p : prods)
            derGraph = derGraph.replace(p, gram.lhs(p));
        derGraph = derGraph.replace("(", "[").replace(")", "]");
        return derGraph;
    }
    
    public static Grammar<String, String> explGram(){
        Grammar<String, String> g = new Grammar<String, String>();
        ArrayList<String> symbs = new ArrayList<String>();
        ArrayList<String> prod = new ArrayList<String>();
        symbs.addAll(Arrays.asList("A", "B", "C"));
        g.setSymbols(symbs);
        g.setAxiom("A");
        try {
            prod.addAll(Arrays.asList("A", "C", "B"));
            g.addProduction("P1", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("A"));
            g.addProduction("P2", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("B", "C", "A"));
            g.addProduction("P3", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("B", "B", "B"));
            g.addProduction("P4", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("C", "A", "C"));
            g.addProduction("P5", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("C", "C", "C"));
            g.addProduction("P6", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("C"));
            g.addProduction("P7", prod);
        } catch (Exception ex) {
            
        }
        return g;
    }
    
    public static Grammar<String, String> explGramAB() {
        Grammar<String, String> g = new Grammar<String, String>();
        ArrayList<String> symbs = new ArrayList<String>();
        ArrayList<String> prod = new ArrayList<String>();
        symbs.addAll(Arrays.asList("A", "B"));
        g.setSymbols(symbs);
        g.setAxiom("A");
        try {
            prod.addAll(Arrays.asList("A", "B"));
            g.addProduction("P1", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("A", "A", "B"));
            g.addProduction("P2", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("A", "A", "A", "B"));
            g.addProduction("P3", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("A", "A", "A", "A", "B"));
            g.addProduction("P4", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("A", "A", "A", "A", "A", "B"));
            g.addProduction("P5", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("A"));
            g.addProduction("P6", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("B", "A"));
            g.addProduction("P7", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("B", "A", "A"));
            g.addProduction("P8", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("B", "A", "A", "A"));
            g.addProduction("P9", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("B", "A", "A", "A", "A"));
            g.addProduction("P10", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("B", "A", "A", "A", "A", "A"));
            g.addProduction("P11", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("B", "B", "B"));
            g.addProduction("P12", prod);
        } catch (Exception ex) {
            
        }
        return g;
    }
    
    public static Grammar<String, String> explGramAC() {
        Grammar<String, String> g = new Grammar<String, String>();
        ArrayList<String> symbs = new ArrayList<String>();
        ArrayList<String> prod = new ArrayList<String>();
        symbs.addAll(Arrays.asList("A", "C"));
        g.setSymbols(symbs);
        g.setAxiom("A");
        try {
            prod.addAll(Arrays.asList("A", "C", "C", "A"));
            g.addProduction("P1", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("A", "C", "C", "A", "C", "A"));
            g.addProduction("P2", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("A", "C", "C", "A", "C", "A", "C", "A"));
            g.addProduction("P3", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("A", "C", "C", "A", "C", "A", "C", "A", "C", "A"));
            g.addProduction("P4", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("A", "C", "C", "A", "C", "A", "C", "A", "C", "A", "C", "A"));
            g.addProduction("P5", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("A"));
            g.addProduction("P6", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("C", "A", "C"));
            g.addProduction("P7", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("C", "C", "C"));
            g.addProduction("P8", prod);
            prod = new ArrayList<String>();
            prod.addAll(Arrays.asList("C"));
            g.addProduction("P9", prod);
        } catch (Exception ex) {
            
        }
        return g;
    }

    public static String derToAstWithoutBud(String haskellTree, Grammar<String, String> gram) {
        Stack<String> evalStack = new Stack<String>();
        Stack<String> astStack = new Stack<String>();
        ArrayList<String> evalSons = new ArrayList<String>(), astSons = new ArrayList<String>();
        int i = 0;
        String nodeName = "", otherObj, otherAst;
        while(i < haskellTree.length()){
            nodeName = "";
            for(; (i < haskellTree.length()) && (haskellTree.charAt(i) != '[') && (haskellTree.charAt(i) != ']'); i++)
                nodeName += haskellTree.charAt(i);
            if(nodeName.startsWith(","))
                nodeName = nodeName.substring(1);
            nodeName = nodeName.trim();
            if(!nodeName.isEmpty()){
                evalStack.push(nodeName);
                evalStack.push("[");
                astStack.push(nodeName);
                astStack.push("[");
            }else{
                otherObj = evalStack.pop();
                otherAst = astStack.pop();
                evalSons = new ArrayList<String>();
                astSons = new ArrayList<String>();
                while(!(otherObj.equals("["))){
                    evalSons.add(0, otherObj);
                    otherObj = evalStack.pop();
                    astSons.add(0, otherAst);
                    otherAst = astStack.pop();
                }
                otherObj = evalStack.peek();
                evalSons.add(0, otherObj);
                otherAst = astStack.pop();
                String tree = "";
                for(String st : astSons)
                    tree += st+",";
                if(!tree.isEmpty())
                    tree = tree.substring(0, tree.length() - 1);
                ArrayList<String> prod = new ArrayList<String>();
                for(int j = 1, l = evalSons.size(); j < l; j++){
                    String sy = evalSons.get(j);
                    if(sy.endsWith("omega"))
                        prod.add(sy.substring(0, sy.length() - 5));
                    else
                        prod.add(sy);
                }
                prod.add(0, evalSons.get(0));
                String p = gram.getProduction(prod);
                if(prod.get(0).endsWith("omega")){
                    if(tree.isEmpty())
                        astStack.push(prod.get(0)+"[]");
                    else
                        return null;
                }else{
                    if(p != null)
                        astStack.push(p+"["+tree+"]");
                    else
                        return null;
                }
            }
            i++;
        }
        return astStack.pop();
    }

    public static String toHaskellGram(Grammar<String, String> grammar) {
        String hGram = "gram :: Gram Prod Symb\ngram = Gram lprod lsymb lhs_ rhs_\n\twhere";
        Set<String> prods = grammar.getProductions().keySet();
        String p = "data Prod = ", s = "data Symb = ", lprod = "\n\t\tlprod = [", lsymb = "\n\t\tlsymb = [", lhs = "\n\t\tlhs_ p = case p of", 
                rhs = "\n\t\trhs_ p = case p of", s2p = "symb2BudProd symb = case symb of";
        int i = 1;
        for(String prod : prods){
            p += (i == 1) ? prod : " | " + prod;
            lprod += (i == 1) ? prod : ", " + prod ;
            lhs += "\n\t\t\t\t" + prod + " -> " + grammar.lhs(prod);
            String r = "[";
            ArrayList<String> rss = grammar.rhs(prod);
            for(int j = 0; j < rss.size(); j++)
                r += (j == 0) ? rss.get(j) : ", " + rss.get(j) ;
            r += "]";
            rhs += "\n\t\t\t\t" + prod + " -> " + r;
            i++;
        }
        lprod += "]";
        i = 1;
        for(String symb : grammar.getSymbolsAxiomTop()){
            s += (i == 1) ? symb : " | " + symb;
            p += " | " + symb + "omega";
            s2p += "\n\t" + symb + " -> " + symb + "omega";
            lsymb += (i == 1) ? symb : ", " + symb;
            i++;
        }
        lsymb += "]";
        p += " deriving (Eq,Show)";
        s += " deriving (Eq,Show)";
        hGram = p + "\n" + s + "\n\n" + s2p + "\n\n" + hGram + lprod + lsymb + lhs + rhs;
        return hGram;
    }

    public static String toHaskellView(ArrayList<String> view) {
        String hView = view.get(0) + " :: Symb -> Bool\n";
        hView += view.get(0) + " symb = case symb of";
        for(int i = 1; i < view.size(); i++)
            hView += "\n\t" + view.get(i) + " -> True";
        hView += "\n\t_ -> False";
        return hView;
    }
}
