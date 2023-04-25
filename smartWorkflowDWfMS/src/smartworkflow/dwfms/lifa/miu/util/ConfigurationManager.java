package smartworkflow.dwfms.lifa.miu.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import smartworkflow.dwfms.lifa.miu.remote.interfaces.CommunicatorInterface;
import smartworkflow.dwfms.lifa.miu.remote.interfaces.ConnectionInterface;
import smartworkflow.dwfms.lifa.miu.util.editor.DistributedEditionWorkflow;
import smartworkflow.dwfms.lifa.miu.util.editor.EditionWorkflow;
import smartworkflow.dwfms.lifa.miu.util.editor.Grammar;
import smartworkflow.dwfms.lifa.miu.util.editor.GrammarAndViews;
import smartworkflow.dwfms.lifa.miu.util.editor.HaskellRunner;
import smartworkflow.dwfms.lifa.miu.util.editor.LocalEditionWorkflow;
import smartworkflow.dwfms.lifa.miu.util.editor.Parsers;
import smartworkflow.dwfms.lifa.miu.util.editor.UserInfos;
import smartworkflow.dwfms.lifa.miu.util.exceptions.ApplException;
import smartworkflow.dwfms.lifa.miu.util.exceptions.CryptographyException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * @author Ndadji Maxime
 * 
 * Voici l'objet de configuration. Celui-ci sera chargé de manipuler les fichiers de
 * configuration. Lorsqu'on voudra donc accéder à une configuration, c'est à cet objet
 * qu'il faudra s'adresser.
 * 
 */

@SuppressWarnings("deprecation")
public class ConfigurationManager {
    private LanguageManager languageManager;
    private URL filePath;
    private String configurationFile = APPLConstants.CONFIG_FOLDER + APPLConstants.CONFIG_FILE_NAME;
    private Document document = null;
    private DOMParser parser = null;
    private Theme theme;
    private CommunicatorInterface communicatorInterface;
    private HashMap<String, String> user;
    
    public ConfigurationManager() throws ApplException{
        updateConfigurationFiles();
        loadConfigurationFile();
        languageManager = new LanguageManager(this);
        try{
            theme = new Theme(this);
        }catch(Exception e){
            theme = new Theme();
        }
    }
    
    public final void loadConfigurationFile() throws ApplException{
        try{
            parser = new DOMParser();
            parser.parse(filePath.toExternalForm().replace("%20", " "));
            document = parser.getDocument();
        }catch(Exception ex){
            filePath = getClass().getResource(APPLConstants.RESOURCES_FOLDER
        		+ configurationFile);
            File file = new File(configurationFile);
            try {
                if(!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                byte[] bytes = new byte[1024];
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file) );
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(filePath.getFile().replace("%20", " ")));
                int i = 0;
                while ((i = in.read(bytes)) != -1){
                    out.write(bytes,0,i);
                }
                out.flush();
                out.close();
                in.close();

                filePath = (new File(configurationFile)).toURI().toURL();
                loadConfigurationFile();
            }catch (Exception e) {
                throw new ApplException("Erreur lors du chargement du fichier " +
            		"de configuration. ");
            }
        }
        
        return;
    }
    
    
    /*######################################################################*/
    /*#                         LANGUAGES MANAGING                         #*/
    /*######################################################################*/
    
    public final ArrayList<String> getLangTags(){
        NodeList nodeList = document.getElementsByTagName("lang-tag");
        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i < nodeList.getLength(); i++)
            list.add(nodeList.item(i).getTextContent());
        
        return list;
    }
    
    public final String getLangName(String langTag){
    	 NodeList nodeList = document.getElementsByTagName("lang-tag");
    	 for(int i = 0; i < nodeList.getLength(); i++){
            if(nodeList.item(i).getTextContent().equalsIgnoreCase(langTag))
                return nodeList.item(i).getParentNode().getChildNodes().item(1).getTextContent();
    	 }
    	 return null;
    }
    
    public final ArrayList<String> getLangNames(){
        NodeList nodeList = document.getElementsByTagName("lang-name");
        ArrayList<String> list = new ArrayList<String>();
        for(int i = 0; i < nodeList.getLength(); i++)
                list.add(nodeList.item(i).getTextContent());

        return list;
    }
	
    public final String getDefaultLangTag(){
        NodeList nodeList = document.getElementsByTagName("lang-default");
        return nodeList.item(0).getTextContent().split("~")[1];
    }

    public final String getDefaultLangName(){
        NodeList nodeList = document.getElementsByTagName("lang-default");
        return nodeList.item(0).getTextContent().split("~")[0];
    }

    public final String getLangTag(String langName){
         NodeList nodeList = document.getElementsByTagName("lang-name");
         for(int i = 0; i < nodeList.getLength(); i++){
            if(nodeList.item(i).getTextContent().equalsIgnoreCase(langName))
                return nodeList.item(i).getParentNode().getChildNodes().item(3).getTextContent();
         }
         return null;
    }

    /*
     * defaultLang form is : lang-name~lang-tag
     */
    public final void changeDefaultLang(String defaultLang) throws ApplException{
        if((getLangTag(defaultLang.split("~")[0]) != null) && (getLangName(defaultLang.split("~")[1]) != null)){
            NodeList nodeList = document.getElementsByTagName("lang-default");
            nodeList.item(0).setTextContent(defaultLang);
            
            save();
        }
        else
            throw new ApplException("");
    }

    /*######################################################################*/
    /*#                     END OF LANGUAGES MANAGING                      #*/
    /*######################################################################*/



    /*######################################################################*/
    /*#                    APPLICATION INFOS MANAGING                      #*/
    /*######################################################################*/

    
    public final HashMap<String, String> getSoftwareInfos() throws ApplException{
        Document doc = null;
        DOMParser pars = null;
        try{
            pars = new DOMParser();
            pars.parse(getClass().getResource(APPLConstants.RESOURCES_FOLDER + configurationFile).toExternalForm().replace("%20", " "));
            doc = pars.getDocument();
        }catch(Exception ex){
            throw new ApplException("Erreur lors du chargement du fichier " +
            		"de configuration. ");
        }
        NodeList nodeList = doc.getElementsByTagName("software").item(0).getChildNodes();
        HashMap<String, String> sinfos = new HashMap<String, String>();
        for(int i = 1; i < nodeList.getLength(); i += 2)
            sinfos.put(nodeList.item(i).getNodeName().split("-")[1], nodeList.item(i).getTextContent());
        return sinfos;
    }
    
    public final boolean isAskThemActivated(){
        return document.getElementsByTagName("theme-loadingask").item(0).getTextContent().equalsIgnoreCase("Activated");
    }
    
    public final boolean isAskWorkspaceActivated(){
        return document.getElementsByTagName("workspace-loadingask").item(0).getTextContent().equalsIgnoreCase("Activated");
    }
    
    public final boolean isWorkspaceValid(){
        String dir = document.getElementsByTagName("workspace-directory").item(0).getTextContent();
        try{
            File f = new File(dir);
            if(f.exists() && f.isDirectory() && f.canRead() && f.canWrite() && f.canExecute()){
                f = new File(dir + File.separator + APPLConstants.LOCAL_ID);
                if(!f.exists())
                    f.mkdir();
                f = new File(dir + File.separator + APPLConstants.DISTRIBUTED_ID);
                if(!f.exists())
                    f.mkdir();
                f = new File(dir + File.separator + APPLConstants.TEMPLATES_ID);
                if(!f.exists())
                    f.mkdir();
                f = new File(dir + File.separator + APPLConstants.GRAMMARS_ID);
                if(!f.exists())
                    f.mkdir();
                f = new File(dir + File.separator + APPLConstants.WORK_ID);
                if(!f.exists())
                    f.mkdir();
                return true;
            }
            return false;
        }catch(Exception e){
            return false;
        }
    }
    
    public final void setAskThemState(boolean state) throws ApplException{
        NodeList nodeList = document.getElementsByTagName("theme-loadingask");
        nodeList.item(0).setTextContent(state ? "Activated" : "Deactivated");
        
        save();
    }
    
    public final void setAskWorkspaceState(boolean state) throws ApplException{
        NodeList nodeList = document.getElementsByTagName("workspace-loadingask");
        nodeList.item(0).setTextContent(state ? "Activated" : "Deactivated");
        
        save();
    }
    
    public final String getWorkspace(){
        return document.getElementsByTagName("workspace-directory").item(0).getTextContent();
    }
    
    public final void setWorkspace(String workspace) throws ApplException{
        NodeList nodeList = document.getElementsByTagName("workspace-directory");
        nodeList.item(0).setTextContent(workspace);
        
        save();
    }
    
    /*######################################################################*/
    /*#                END OF APPLICATION INFOS MANAGING                   #*/
    /*######################################################################*/
    
    
    /*######################################################################*/
    /*#                    SERVER INFOS MANAGING                           #*/
    /*######################################################################*/

    public final ServerInfos getServerInfos(){
        NodeList nodeList = document.getElementsByTagName("server").item(0).getChildNodes();
        ServerInfos dbinfos = new ServerInfos();
        for(int i = 1; i < nodeList.getLength(); i += 2)
            dbinfos.put(nodeList.item(i).getNodeName().split("-")[1], nodeList.item(i).getTextContent());
        return dbinfos;
    }
    
    public final void setServerInfos(ServerInfos dbinfos) throws ApplException{
        NodeList nodeList = document.getElementsByTagName("sv-host");
        nodeList.item(0).setTextContent(dbinfos.get("host"));
        nodeList = document.getElementsByTagName("sv-url");
        nodeList.item(0).setTextContent(dbinfos.get("url"));
        
        save();
    }
    
    public final void setServerInfos(String host) throws ApplException{
        NodeList nodeList = document.getElementsByTagName("sv-host");
        nodeList.item(0).setTextContent(host);
        nodeList = document.getElementsByTagName("sv-url");
        nodeList.item(0).setTextContent("rmi://" + host + ":" + APPLConstants.PORT);
        
        save();
    }
    
    public void setUserInfos(HashMap<String, String> userInfos) throws ApplException{
        try {
            NodeList nodeList = document.getElementsByTagName("user-login");
            nodeList.item(0).setTextContent(APPLConstants.encryptMessage(userInfos.get("login")));
            nodeList = document.getElementsByTagName("user-password");
            nodeList.item(0).setTextContent(APPLConstants.encryptMessage(userInfos.get("password")));
            
            save();
        } catch (CryptographyException ex) {
            throw new ApplException("");
        }
    }
    
    public HashMap<String, String> getUserInfos() throws ApplException {
        try {
            HashMap<String, String> userInfos = new HashMap<String, String>();
            NodeList nodeList = document.getElementsByTagName("user").item(0).getChildNodes();
            for(int i = 3; i < nodeList.getLength(); i += 2)
                userInfos.put(nodeList.item(i).getNodeName().split("-")[1], APPLConstants.decryptMessage(nodeList.item(i).getTextContent()));
            return userInfos;
        } catch (CryptographyException ex) {
            throw new ApplException("");
        }
    }
    
    public final boolean isSoundActivated(){
        return document.getElementsByTagName("sound-activated").item(0).getTextContent().equalsIgnoreCase("Activated");
    }
    
    public final void setSoundState(boolean state) throws ApplException{
        NodeList nodeList = document.getElementsByTagName("sound-activated");
        nodeList.item(0).setTextContent(state ? "Activated" : "Deactivated");
        
        save();
    }

    public HashMap<String, String> getUser() {
        return user;
    }

    public void setUser(HashMap<String, String> user) {
        this.user = user;
    }
    
    public final boolean isUserRememberActivated(){
        return document.getElementsByTagName("user-remember").item(0).getTextContent().equalsIgnoreCase("Activated");
    }
    
    public final void setUserRememberState(boolean state) throws ApplException{
        NodeList nodeList = document.getElementsByTagName("user-remember");
        nodeList.item(0).setTextContent(state ? "Activated" : "Deactivated");
        
        save();
    }
    
    /*######################################################################*/
    /*#                END OF SERVER INFOS MANAGING                        #*/
    /*######################################################################*/
    

    /*######################################################################*/
    /*#                      WINDOW INFOS MANAGING                         #*/
    /*######################################################################*/
    
    public final HashMap<String, String> getWindowInfos(){
        NodeList nodeList = document.getElementsByTagName("window").item(0).getChildNodes();
        HashMap<String, String> winfos = new HashMap<String, String>();
        for(int i = 1; i < nodeList.getLength(); i += 2)
            winfos.put(nodeList.item(i).getNodeName().split("-")[1], nodeList.item(i).getTextContent());
        return winfos;
    }
    
    public final void setWindowInfos(HashMap<String, String> winfos) throws ApplException{
        NodeList nodeList = document.getElementsByTagName("window-width");
        nodeList.item(0).setTextContent(winfos.get("width"));
        nodeList = document.getElementsByTagName("window-height");
        nodeList.item(0).setTextContent(winfos.get("height"));
        
        save();
    }
    
    public final String getDefaultTheme(){
        return document.getElementsByTagName("default-theme").item(0).getTextContent();
    }
    
    public final void setDefaultTheme(String theme) throws ApplException{
        NodeList nodeList = document.getElementsByTagName("default-theme");
        nodeList.item(0).setTextContent(theme);
        this.theme = new Theme(this);
        
        save();
    }
    
    public final String getSelectionRangeIndex(){
        return document.getElementsByTagName("pagin_selection_range").item(0).getTextContent();
    }
    
    public final void setSelectionRangeIndex(String index) throws ApplException{
        NodeList nodeList = document.getElementsByTagName("pagin_selection_range");
        nodeList.item(0).setTextContent(index);
        
        save();
    }
    
    public final String getHaskellInterpreter(){
        return document.getElementsByTagName("interpreter").item(0).getTextContent();
    }
    
    public int getTokenNum() {
        return 20;
    }
    
    public LanguageManager getLangManager() {
        return languageManager;
    }

    public String getLangValue(String key){
        return this.languageManager.getValue(key, this.getDefaultLangTag());
    }

    public Theme getTheme() {
        return theme;
    }
    
    public void initServer() throws ApplException {
        try {
            String url = getServerInfos().get("url");
            ConnectionInterface ci = (ConnectionInterface)Naming.lookup(url+"/ServerConnection");
            String token = ci.getToken();
            communicatorInterface = (CommunicatorInterface)Naming.lookup(url+"/Communicator" + token);
        } catch (Exception ex) {
            throw new ApplException("");
        }
    }
    
    public void initServer(String host) throws ApplException {
        try {
            String url = "rmi://" + host + ":" + APPLConstants.PORT;
            ConnectionInterface ci = (ConnectionInterface)Naming.lookup(url+"/ServerConnection");
            String token = ci.getToken();
            communicatorInterface = (CommunicatorInterface)Naming.lookup(url+"/Communicator" + token);
        } catch (Exception ex) {
            throw new ApplException("");
        }
    }
    
    public CommunicatorInterface getCommunicator(String host) throws ApplException {
        try {
            String url = "rmi://" + host + ":" + APPLConstants.PORT;
            ConnectionInterface ci = (ConnectionInterface)Naming.lookup(url+"/ServerConnection");
            String token = ci.getToken();
            return (CommunicatorInterface)Naming.lookup(url+"/Communicator" + token);
        } catch (Exception ex) {
            throw new ApplException("");
        }
    }

    public CommunicatorInterface getCommunicatorInterface() {
        return communicatorInterface;
    }

    private void save() throws ApplException {
        try{
            XMLSerializer ser = new XMLSerializer(
                new FileOutputStream(configurationFile), new OutputFormat("xml", "UTF-8", true));
            ser.serialize(document);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, "L'application a rencontré un problème et va s'arrêter."
                        + "\nVeuillez contacter un développeur pour régler ce problème. ", "Arrêt brusque"
                        + " de l'application", 
                        JOptionPane.ERROR_MESSAGE); 
                System.exit(0); 
            throw new ApplException("");
        }
    }

    private void updateConfigurationFiles() {
        File file = new File(configurationFile);
        try {
            if(file.exists())
                filePath = file.toURI().toURL();
            else{
                filePath = getClass().getResource(APPLConstants.RESOURCES_FOLDER
        		+ configurationFile.toLowerCase());
                try {
                    if(!file.getParentFile().exists())
                        file.getParentFile().mkdirs();
                    byte[] bytes = new byte[1024];
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file) );
                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(filePath.getFile().replace("%20", " ")));
                    int i = 0;
                    while ((i = in.read(bytes)) != -1){
                        out.write(bytes,0,i);
                    }
                    out.flush();
                    out.close();
                    in.close();
                    
                    filePath = (new File(configurationFile)).toURI().toURL();
                } catch (Exception ex) {
                	
                }    
            }
        } catch (MalformedURLException ex) {
            
        }
        String englishFile = APPLConstants.LANG_FOLDER + APPLConstants.LANG_FILE_PREFIX + APPLConstants.ENGLISH_TAG + APPLConstants.LANG_FILE_EXTENSION;
        String frenchFile = APPLConstants.LANG_FOLDER + APPLConstants.LANG_FILE_PREFIX + APPLConstants.FRENCH_TAG + APPLConstants.LANG_FILE_EXTENSION;
        file = new File(englishFile);
        if(!file.exists()){
            try {
                if(!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                byte[] bytes = new byte[1024];
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file) );
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(getClass().getResource(
                        APPLConstants.RESOURCES_FOLDER + englishFile).getFile().replace("%20", " ")));
                int i = 0;
                while ((i = in.read(bytes)) != -1){
                    out.write(bytes,0,i);
                }
                out.flush();
                out.close();
                in.close();

                file = new File(frenchFile);
                out = new BufferedOutputStream(new FileOutputStream(file) );
                in = new BufferedInputStream(new FileInputStream(getClass().getResource(
                        APPLConstants.RESOURCES_FOLDER + frenchFile).getFile().replace("%20", " ")));
                i = 0;
                while ((i = in.read(bytes)) != -1){
                    out.write(bytes,0,i);
                }
                out.flush();
                out.close();
                in.close();
            } catch (Exception ex) {

            }
        }
        String engine = APPLConstants.ENGINE_FOLDER + APPLConstants.ENGINE_FILE_NAME;
        file = new File(engine);
        URL fileP;
        try {
            if(!file.exists()){
                fileP = getClass().getResource(APPLConstants.RESOURCES_FOLDER
        		+ engine);
                try {
                    if(!file.getParentFile().exists())
                        file.getParentFile().mkdirs();
                    byte[] bytes = new byte[1024];
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file) );
                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileP.getFile().replace("%20", " ")));
                    int i = 0;
                    while ((i = in.read(bytes)) != -1){
                        out.write(bytes,0,i);
                    }
                    out.flush();
                    out.close();
                    in.close();
                } catch (Exception ex) {
                    
                }
            }
        } catch (Exception ex) {
            
        }
    }

    public ArrayList<String> getTemplatesNames() {
        String path = getWorkspace() + File.separator + APPLConstants.TEMPLATES_ID;
        File dir = new File(path);
        ArrayList<String> elts = APPLConstants.getFilesNamesMatchingPattern(dir, "templ_([a-zA-Z0-9_ -]{1,})\\.json");
        ArrayList<String> names = new ArrayList<String>();
        for(String name : elts)
            names.add(name.substring(6, name.length() - 5));
        return names;
    }
    
    private static char getElt(){
       int nb = Math.round((float)Math.random()*40);
       switch(nb){
           case 1 : {return 'e';} case 2 : {return 'y';} case 3 : {return ')';} case 4 : {return 'r';}
           case 5 : {return '}';} case 6 : {return '8';} case 7 : {return 'o';} case 8 : {return 'j';}
           case 9 : {return '.';} case 10 : {return 'w';} case 11 : {return '@';} case 12 : {return 't';}
           case 13 : {return 'x';} case 14 : {return 'l';}case 15 : {return '9';} case 16 : {return '_';}
           case 17 : {return '-';} case 18 : {return '(';} case 19 : {return '%';} case 20 : {return '#';}
           case 21 : {return 'K';} case 22 : {return '"';} case 23 : {return ':';} case 24 : {return '+';}
           case 25 : {return ',';} case 26 : {return '~';} case 27 : {return '^';} case 28 : {return '&';}
           case 29 : {return '>';} case 30 : {return '<';} case 31 : {return '*';} case 32 : {return 't';}
           case 33 : {return '�';} case 34 : {return '$';} case 35 : {return '{';} case 36 : {return 'z';}
           case 37 : {return '!';} case 38 : {return '[';} case 39 : {return '�';} case 40 : {return ']';}
           default : {return '?';}
       }
   }
    
    public static String generate(int length){
        String key = "";
        while(key.getBytes().length < length){
            key += getElt();
            if(key.getBytes().length > length)
                key = ""+getElt();
        }
        return key;
    }

    @SuppressWarnings("unchecked")
	public DistributedEditionWorkflow<String, String> getEditionFromTemplate(String suffix) {
        DistributedEditionWorkflow<String, String> ed = new DistributedEditionWorkflow<String, String>();
        String fileName = "templ_" + suffix + ".json";
        File file = new File(getWorkspace() + File.separator + APPLConstants.TEMPLATES_ID + File.separator + fileName);
        if(!file.exists() || !file.isFile() || !file.canRead())
            return null;
        String line = new String();
        String json = "";
        try{  
            FileReader fileReader = new FileReader(file);   
            BufferedReader bufferedReader = new BufferedReader(fileReader);  
            while((line = bufferedReader.readLine()) != null)  
                json += line+"\n";
            bufferedReader.close();
            final GsonBuilder builder = new GsonBuilder();
            final Gson gson = builder.create();
            ed = gson.fromJson(json, DistributedEditionWorkflow.class);
        }catch(Exception ex){
            return null;
        }
        return ed;
    }
    
    @SuppressWarnings("unchecked")
	public DistributedEditionWorkflow<String, String> getDistributedEdition(String suffix) {
        DistributedEditionWorkflow<String, String> ed = new DistributedEditionWorkflow<String, String>();
        String fileName = "tdw_" + suffix + ".tdw";
        File file = new File(getWorkspace() + File.separator + APPLConstants.DISTRIBUTED_ID + File.separator + fileName);
        if(!file.exists() || !file.isFile() || !file.canRead())
            return null;
        String line = new String();
        String value = "";
        try{  
            FileReader fileReader = new FileReader(file);   
            BufferedReader bufferedReader = new BufferedReader(fileReader);  
            while((line = bufferedReader.readLine()) != null)  
                value += line+"~~~";
            bufferedReader.close();
            value = value.substring(0, value.length() - 3);
            String[] vals = value.split("~~~");
            final GsonBuilder builder = new GsonBuilder();
            final Gson gson = builder.create();
            String json = null;
            if(vals.length > 4){
                String key = APPLConstants.decryptMessage(vals[2]);
                String initVect = APPLConstants.decryptMessage(vals[3]);
                String mess = vals[4];
                for(int j = 5; j < vals.length; j++)
                    mess += vals[j];
                json = APPLConstants.decryptMessage(mess, key, initVect);
            }else{
                throw new Exception(getLangValue("incorrect_file_input"));
            }
            ed = gson.fromJson(json, DistributedEditionWorkflow.class);
        }catch(Throwable ex){
            return null;
        }
        return ed;
    }
    
    

    public void saveEditionAsTemplate(DistributedEditionWorkflow<String, String> edition, String nameSuffix) {
        if(nameSuffix == null || nameSuffix.trim().isEmpty())
            nameSuffix = "default_";
        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        final String json = gson.toJson(edition);
        String path = getWorkspace() + File.separator + APPLConstants.TEMPLATES_ID + File.separator + "templ_" + nameSuffix;
        File f = new File(path + ".json");
        int j = 1;
        while(f.exists()){
            f = new File(path + j + ".json");
            j++;
        }
        try {
            byte[] bytes = new byte[1024];
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
            BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(json.getBytes()));
            int i = 0;
            while ((i = in.read(bytes)) != -1){
                out.write(bytes,0,i);
            }
            out.flush();
            out.close();
            in.close();
        } catch (Exception ex) {
            
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public GrammarAndViews<String> readGrammarAndViews(DataFlavor[] types, Transferable transferable) throws Throwable{
        Grammar<String, String> gram = new Grammar<String, String>();
        ArrayList<ArrayList<String>> views = new ArrayList<ArrayList<String>>();
        for (DataFlavor type : types) {
            try {
               if(type.equals(DataFlavor.javaFileListFlavor)) {
                  List listeFichiers = (List) transferable.getTransferData(type);
                  Iterator iterateur = listeFichiers.iterator();
                  if (iterateur.hasNext()) {
                     File fichier = (File) iterateur.next();
                     try{
                        POIFSFileSystem fileSystem = new POIFSFileSystem(new FileInputStream(fichier));
                        HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
                        HSSFSheet sheet = workBook.getSheetAt(0);
                        HSSFRow row = null;
                        String key = null, initVect = null;
                        boolean production = true, done = false;
                        int prodNum = 1000000;
                        
                        Iterator it = sheet.rowIterator();
                        while(it.hasNext()){
                            row = (HSSFRow)it.next();
                            if(row.getRowNum() == 3){
                                HSSFCell cell = row.getCell(0);
                                if(cell != null)
                                    key = APPLConstants.decryptMessage(cell.getStringCellValue());
                            }
                            
                            if(row.getRowNum() == 4){
                                HSSFCell cell = row.getCell(0);
                                if(cell != null)
                                    initVect = APPLConstants.decryptMessage(cell.getStringCellValue());
                                if((key == null && initVect != null) || (initVect == null && key != null))
                                    throw new Exception("erreur");
                            }
                            
                            if(!done && row.getRowNum() > 7 && (row.getCell(0) == null || !row.getCell(0).getStringCellValue().matches("P([0-9]{1,})"))){
                                production = false;
                                prodNum = row.getRowNum() + 1;
                                done = true;
                            }
                            
                            if(production && row.getRowNum() > 7){
                                HSSFCell cell = row.getCell(0);
                                String lhs = "", rhs = "", id;
                                if(cell != null){
                                    id = cell.getStringCellValue();
                                    if(key != null && initVect != null)
                                        id = APPLConstants.decryptMessage(id, key, initVect);
                                    if(!id.matches("P([0-9]{1,})"))
                                        throw new Exception("erreur");
                                }else
                                    throw new Exception("erreur");
                                
                                cell = row.getCell(1);
                                if(cell != null){
                                    lhs = cell.getStringCellValue();
                                    if(key != null && initVect != null)
                                        lhs = APPLConstants.decryptMessage(lhs, key, initVect);
                                    if(!lhs.matches("[a-zA-Z0-9_]{1,}"))
                                        throw new Exception("erreur");
                                }else
                                    throw new Exception("erreur");
                                
                                cell = row.getCell(2);
                                if(cell != null){
                                    rhs = cell.getStringCellValue();
                                    if(key != null && initVect != null)
                                        rhs = APPLConstants.decryptMessage(rhs, key, initVect);
                                }else
                                    throw new Exception("erreur");
                                
                                ArrayList<String> prod = new ArrayList<String>();
                                String[] rhss = rhs.split(",");
                                prod.addAll(Arrays.asList(rhss));
                                prod.add(0, lhs);
                                
                                for(String sy : prod)
                                    gram.addSymbol(sy);
                                
                                if(gram.getAxiom() == null)
                                    gram.setAxiom(lhs);
                                
                                gram.addProduction(id, prod);
                            }
                            
                            if(row.getRowNum() > prodNum){
                                HSSFCell cell = row.getCell(0);
                                String symbs = "", id;
                                if(cell != null){
                                    id = cell.getStringCellValue();
                                    if(key != null && initVect != null)
                                        id = APPLConstants.decryptMessage(id, key, initVect);
                                }else
                                    throw new Exception("erreur");
                                
                                cell = row.getCell(1);
                                if(cell != null){
                                    symbs = cell.getStringCellValue();
                                    if(key != null && initVect != null)
                                        symbs = APPLConstants.decryptMessage(symbs, key, initVect);
                                }else
                                    throw new Exception("erreur");
                                
                                ArrayList<String> view = new ArrayList<String>();
                                String[] rhss = symbs.split(",");
                                view.addAll(Arrays.asList(rhss));
                                view.add(0, id);
                                
                                views.add(view);
                            }
                        }
                        return new GrammarAndViews<String>(gram, views);       
                     }catch(Exception e){
                         if(e.getMessage().equalsIgnoreCase("erreur")){
                             throw new Exception(getLangValue("incorrect_file_input"));
                         }else{
                             if(fichier.getName().endsWith(".txt") || fichier.getName().endsWith(".tgf") || fichier.getName().endsWith(".TXT") || fichier.getName().endsWith(".TGF")){
                                String line = new String(), value = new String();
                                try{  
                                    FileReader fileReader = new FileReader(fichier);   
                                    BufferedReader bufferedReader = new BufferedReader(fileReader);  
                                    while((line = bufferedReader.readLine()) != null)  
                                        value += line+"~~~";
                                    bufferedReader.close();
                                    value = value.substring(0, value.length() - 3);
                                    String[] vals = value.split("~~~");
                                    final GsonBuilder builder = new GsonBuilder();
                                    final Gson gson = builder.create();
                                    String json = null;
                                    if(vals.length > 4){
                                        String key = APPLConstants.decryptMessage(vals[2]);
                                        String initVect = APPLConstants.decryptMessage(vals[3]);
                                        String mess = vals[4];
                                        for(int j = 5; j < vals.length; j++)
                                            mess += vals[j];
                                        json = APPLConstants.decryptMessage(mess, key, initVect);
                                    }else{
                                        if(vals.length == 3){
                                            json = vals[2];
                                        }else
                                            throw new Exception(getLangValue("incorrect_file_input"));
                                    }
                                    return gson.fromJson(json, GrammarAndViews.class);
                                }catch(Exception ex){
                                    throw new Exception(getLangValue("incorrect_file_input"));
                                }
                             }else{
                                throw new Exception(getLangValue("not_supported_file"));
                             }
                         }
                     }
                  }
               } 
            }
            catch (Exception ex) {
                throw new Exception(ex.getMessage());
            }
         }
        throw new Exception(getLangValue("not_supported_file"));
    }

    public void exportGrammarAndViews(GrammarAndViews<String> gramAndViews, String nameSuffix, boolean encrypt, String type, String folder) {
        if(!GrammarAndViews.TGF.equals(type) && !GrammarAndViews.XLS.equals(type) && !GrammarAndViews.TXT.equals(type))
            type = GrammarAndViews.TGF;
        if(folder == null)
            folder = getWorkspace() + File.separator + APPLConstants.GRAMMARS_ID;
        else{
            if(folder.endsWith("/"))
                folder = folder.substring(0, folder.length() - 1);
            File f = new File(folder);
            if(!f.exists() || !f.isDirectory() || !f.canRead() || !f.canWrite())
                folder = getWorkspace() + File.separator + APPLConstants.GRAMMARS_ID;
        }
        String path = folder + File.separator + "gramAndView_" + nameSuffix;
        File f = new File(path + type);
        int j = 1;
        while(f.exists()){
            f = new File(path + j + type);
            j++;
        }
        if(GrammarAndViews.TGF.equals(type) || GrammarAndViews.TXT.equals(type)){
            try {
                byte[] bytes = new byte[1024];
                final GsonBuilder builder = new GsonBuilder();
                final Gson gson = builder.create();
                String json = getLangValue("grammar_views_file").toUpperCase()+"\n";
                json += getLangValue("generate_by_tinyce")+" - "+DateTime.now()+"\n";
                if(encrypt){
                    String key = generate(16);
                    String initVect = generate(16);
                    json += APPLConstants.encryptMessage(key)+"\n";
                    json += APPLConstants.encryptMessage(initVect)+"\n";
                    json += APPLConstants.encryptMessage(gson.toJson(gramAndViews), key, initVect);
                }else
                    json += gson.toJson(gramAndViews);
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
                BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(json.getBytes()));
                int i = 0;
                while ((i = in.read(bytes)) != -1){
                    out.write(bytes,0,i);
                }
                out.flush();
                out.close();
                in.close();
            } catch (Throwable ex) {

            }
        }else{
            try{
                HSSFWorkbook workBook = new HSSFWorkbook();
                HSSFSheet sheet = workBook.createSheet(getLangValue("grammar_views_file"));
                for(int i = 0; i < 3; i++)
                    sheet.setColumnWidth(i, 6500);
                int index = 0;
                HSSFRow row = sheet.createRow(index);
                HSSFCell cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
                HSSFFont font = workBook.createFont();
                HSSFCellStyle style = workBook.createCellStyle();

                cell.setCellValue(getLangValue("grammar_views_file").toUpperCase());
                style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                font.setFontHeightInPoints((short) 18);
                font.setFontName("Cambria");
                font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
                style.setFont(font);
                style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
                style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cell.setCellStyle(style);
                sheet.addMergedRegion(new Region(index, (short)0, index, (short)2));

                index++;

                row = sheet.createRow(index);
                cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
                font = workBook.createFont();
                style = workBook.createCellStyle();
                cell.setCellValue(getLangValue("generate_by_tinyce")+" - "+DateTime.now());
                style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                font.setFontHeightInPoints((short) 12);
                font.setFontName("Cambria");
                style.setFont(font);
                cell.setCellStyle(style);
                sheet.addMergedRegion(new Region(index, (short)0, index, (short)2));

                index += 2;
                String key = generate(16);
                String initVect = generate(16);
                if(encrypt){
                    row = sheet.createRow(index);
                    cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
                    font = workBook.createFont();
                    style = workBook.createCellStyle();
                    cell.setCellValue(APPLConstants.encryptMessage(key));
                    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                    font.setFontHeightInPoints((short) 14);
                    font.setFontName("Cambria");
                    style.setFont(font);
                    cell.setCellStyle(style);
                    sheet.addMergedRegion(new Region(index, (short)0, index, (short)2));
                    index++;
                    
                    row = sheet.createRow(index);
                    cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
                    font = workBook.createFont();
                    style = workBook.createCellStyle();
                    cell.setCellValue(APPLConstants.encryptMessage(initVect));
                    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                    font.setFontHeightInPoints((short) 14);
                    font.setFontName("Cambria");
                    style.setFont(font);
                    cell.setCellStyle(style);
                    sheet.addMergedRegion(new Region(index, (short)0, index, (short)2));
                    index++;
                }else{
                    index += 2;
                }
                
                index++;
                
                row = sheet.createRow(index);
                cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
                font = workBook.createFont();
                style = workBook.createCellStyle();
                cell.setCellValue(getLangValue("productions"));
                style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                font.setFontHeightInPoints((short) 16);
                font.setFontName("Cambria");
                font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
                style.setFont(font);
                style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
                style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cell.setCellStyle(style);
                sheet.addMergedRegion(new Region(index, (short)0, index, (short)2));
                
                index++;
                row = sheet.createRow(index);
                
                cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
                font = workBook.createFont();
                style = workBook.createCellStyle();
                cell.setCellValue(getLangValue("name"));
                style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                font.setFontHeightInPoints((short) 15);
                font.setFontName("Cambria");
                font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
                style.setFont(font);
                style.setFillForegroundColor(HSSFColor.GOLD.index);
                style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cell.setCellStyle(style);
                
                cell = row.createCell(1, HSSFCell.CELL_TYPE_STRING);
                font = workBook.createFont();
                style = workBook.createCellStyle();
                cell.setCellValue(getLangValue("lhs"));
                style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                font.setFontHeightInPoints((short) 15);
                font.setFontName("Cambria");
                font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
                style.setFont(font);
                style.setFillForegroundColor(HSSFColor.GOLD.index);
                style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cell.setCellStyle(style);
                
                cell = row.createCell(2, HSSFCell.CELL_TYPE_STRING);
                font = workBook.createFont();
                style = workBook.createCellStyle();
                cell.setCellValue(getLangValue("rhs"));
                style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                font.setFontHeightInPoints((short) 15);
                font.setFontName("Cambria");
                font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
                style.setFont(font);
                style.setFillForegroundColor(HSSFColor.GOLD.index);
                style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cell.setCellStyle(style);
                
                index++;
                
                Set<String> prods = gramAndViews.getGrammar().getProductions().keySet();
                for(String prod : prods){
                    row = sheet.createRow(index);
                    cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
                    font = workBook.createFont();
                    style = workBook.createCellStyle();
                    cell.setCellValue(encrypt ? APPLConstants.encryptMessage(prod, key, initVect) : prod);
                    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                    font.setFontHeightInPoints((short) 14);
                    font.setFontName("Cambria");
                    style.setFont(font);
                    cell.setCellStyle(style);
                    
                    String lhs = gramAndViews.getGrammar().lhs(prod);
                    ArrayList<String> rhss = gramAndViews.getGrammar().rhs(prod);
                    String rhs = "";
                    if(rhss.isEmpty())
                        rhs = "£";
                    else{
                        for(String st : rhss)
                            rhs += st + ",";
                        rhs = rhs.substring(0, rhs.length() - 1);
                    }
                    
                    cell = row.createCell(1, HSSFCell.CELL_TYPE_STRING);
                    font = workBook.createFont();
                    style = workBook.createCellStyle();
                    cell.setCellValue(encrypt ? APPLConstants.encryptMessage(lhs, key, initVect) : lhs);
                    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                    font.setFontHeightInPoints((short) 14);
                    font.setFontName("Cambria");
                    style.setFont(font);
                    cell.setCellStyle(style);
                    
                    cell = row.createCell(2, HSSFCell.CELL_TYPE_STRING);
                    font = workBook.createFont();
                    style = workBook.createCellStyle();
                    cell.setCellValue(encrypt ? APPLConstants.encryptMessage(rhs, key, initVect) : rhs);
                    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                    font.setFontHeightInPoints((short) 14);
                    font.setFontName("Cambria");
                    style.setFont(font);
                    cell.setCellStyle(style);
                    
                    index++;
                }
                
                index++;
                row = sheet.createRow(index);
                cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
                font = workBook.createFont();
                style = workBook.createCellStyle();
                cell.setCellValue(getLangValue("views"));
                style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                font.setFontHeightInPoints((short) 16);
                font.setFontName("Cambria");
                font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
                style.setFont(font);
                style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
                style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cell.setCellStyle(style);
                sheet.addMergedRegion(new Region(index, (short)0, index, (short)2));
                
                index++;
                row = sheet.createRow(index);
                
                cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
                font = workBook.createFont();
                style = workBook.createCellStyle();
                cell.setCellValue(getLangValue("name"));
                style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                font.setFontHeightInPoints((short) 15);
                font.setFontName("Cambria");
                font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
                style.setFont(font);
                style.setFillForegroundColor(HSSFColor.GOLD.index);
                style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cell.setCellStyle(style);
                
                cell = row.createCell(1, HSSFCell.CELL_TYPE_STRING);
                font = workBook.createFont();
                style = workBook.createCellStyle();
                cell.setCellValue(getLangValue("symbols"));
                style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                font.setFontHeightInPoints((short) 15);
                font.setFontName("Cambria");
                font.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
                style.setFont(font);
                style.setFillForegroundColor(HSSFColor.GOLD.index);
                style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cell.setCellStyle(style);
                
                index++;
                
                for(ArrayList<String> symbs : gramAndViews.getViews()){
                    row = sheet.createRow(index);
                    cell = row.createCell(0, HSSFCell.CELL_TYPE_STRING);
                    font = workBook.createFont();
                    style = workBook.createCellStyle();
                    cell.setCellValue(encrypt ? APPLConstants.encryptMessage(symbs.get(0), key, initVect) : symbs.get(0));
                    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                    font.setFontHeightInPoints((short) 14);
                    font.setFontName("Cambria");
                    style.setFont(font);
                    cell.setCellStyle(style);
                    
                    String symb = "";
                    for(int k = 1, l = symbs.size(); k < l; k++)
                        symb += symbs.get(k) + ",";
                    symb = symb.substring(0, symb.length() - 1);
                    
                    cell = row.createCell(1, HSSFCell.CELL_TYPE_STRING);
                    font = workBook.createFont();
                    style = workBook.createCellStyle();
                    cell.setCellValue(encrypt ? APPLConstants.encryptMessage(symb, key, initVect) : symb);
                    style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                    font.setFontHeightInPoints((short) 14);
                    font.setFontName("Cambria");
                    style.setFont(font);
                    cell.setCellStyle(style);
                    
                    index++;
                }
                FileOutputStream outputStream;
                outputStream = new FileOutputStream(f);
                workBook.write(outputStream);
                outputStream.close();
            }
            catch (Throwable ex) {
                
            }
        }
    }

    public String saveNewDistributedEdition(DistributedEditionWorkflow<String, String> edition) throws Throwable {
        String configID = null;
        String nameSuffix = APPLConstants.getFileNameFromString(edition.getWorkflowName());
        if(nameSuffix == null || nameSuffix.isEmpty())
            nameSuffix = "default_";
        String path = getWorkspace() + File.separator + APPLConstants.DISTRIBUTED_ID + File.separator + "tdw_" + nameSuffix;
        File f = new File(path + ".tdw");
        int j = 1;
        while(f.exists()){
            f = new File(path + j + ".tdw");
            j++;
        }
        if(j > 1)
            nameSuffix += (j - 1);
        configID = nameSuffix;
        edition.setWorkflowID(nameSuffix);
        String key = generate(16);
        String initVect = generate(16);
        edition.setWorkflowKey(APPLConstants.encryptMessage(key));
        edition.setWorkflowInitVector(APPLConstants.encryptMessage(initVect));
        
        String engine = APPLConstants.ENGINE_FOLDER + APPLConstants.ENGINE_FILE_NAME;
        String enginePath = getWorkspace() + File.separator + APPLConstants.WORK_ID + File.separator + configID;
        File file = new File(enginePath + ".hs");
        j = 1;
        while(file.exists()){
            file = new File(enginePath + j + ".hs");
            j++;
        }
        String wfEngine = configID + ((j > 1) ? j : "");
        URL fileP;
        try {
            fileP = getClass().getResource(APPLConstants.RESOURCES_FOLDER
                    + engine);
            try {
                if(!file.getParentFile().exists())
                    file.getParentFile().mkdirs();
                byte[] bytes = new byte[1024];
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file) );
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileP.getFile().replace("%20", " ")));
                int i = 0;
                while ((i = in.read(bytes)) != -1){
                    out.write(bytes,0,i);
                }
                
                String gen = "-- Generated grammar views and others\n\n";
                gen += Parsers.toHaskellGram(edition.getGrammar())+"\n\n";
                for(ArrayList<String> view : edition.getViews())
                    gen += Parsers.toHaskellView(view)+"\n\n";
                gen += "globalView  :: Symb -> Bool\nglobalView _ = True\n\n";
                gen += "-- End of generation";
                
                in = new BufferedInputStream(new ByteArrayInputStream(gen.getBytes()));
                i = 0;
                while ((i = in.read(bytes)) != -1){
                    out.write(bytes,0,i);
                }
                
                out.flush();
                out.close();
                in.close();
            } catch (Exception ex) {

            }
        } catch (Exception ex) {
            
        }
        edition.setEngine(wfEngine);
        
        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        String json = gson.toJson(edition);
        String encryptedJson = APPLConstants.encryptMessage(json, key, initVect);
        String toSave = getLangValue("tiny_distributed_workflow")+"\n";
        toSave += getLangValue("generate_by_tinyce")+" - "+DateTime.now()+"\n";
        toSave += APPLConstants.encryptMessage(key)+"\n";
        toSave += APPLConstants.encryptMessage(initVect)+"\n";
        toSave += encryptedJson;
        byte[] bytes = new byte[1024];
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
        BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(toSave.getBytes()));
        int i = 0;
        while ((i = in.read(bytes)) != -1){
            out.write(bytes,0,i);
        }
        out.flush();
        out.close();
        in.close();
        
        return configID;
    }
    
    public String saveNewLocalEdition(LocalEditionWorkflow<String, String> edition) throws Throwable {
        String configID = null;
        String nameSuffix = edition.getWorkflowID();
        if(nameSuffix == null)
            nameSuffix= APPLConstants.getFileNameFromString(edition.getWorkflowName());
        if(nameSuffix == null || nameSuffix.isEmpty())
            nameSuffix = "default_";
        String path = getWorkspace() + File.separator + APPLConstants.LOCAL_ID + File.separator + "tlw_" + nameSuffix;
        File f = new File(path + ".tlw");
        int j = 1;
        while(f.exists()){
            f = new File(path + j + ".tlw");
            j++;
        }
        if(j > 1)
            nameSuffix += (j - 1);
        configID = nameSuffix;
        edition.setLocalID(nameSuffix);
        String key = generate(16);
        String initVect = generate(16);
        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        String json = gson.toJson(edition);
        String encryptedJson = APPLConstants.encryptMessage(json, key, initVect);
        String toSave = getLangValue("tiny_local_workflow")+"\n";
        toSave += getLangValue("generate_by_tinyce")+" - "+DateTime.now()+"\n";
        toSave += APPLConstants.encryptMessage(key)+"\n";
        toSave += APPLConstants.encryptMessage(initVect)+"\n";
        toSave += encryptedJson;
        byte[] bytes = new byte[1024];
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
        BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(toSave.getBytes()));
        int i = 0;
        while ((i = in.read(bytes)) != -1){
            out.write(bytes,0,i);
        }
        out.flush();
        out.close();
        in.close();
        return configID;
    }
    
    public void saveSynchroDistributedEdition(DistributedEditionWorkflow<String, String> edition) throws Throwable {
        edition.setWorkflowPreviousKey(edition.getWorkflowKey());
        edition.setWorkflowPreviousInitVector(edition.getWorkflowInitVector());
        changeCryptoKeys(edition);
        saveOldDistributedEdition(edition);
    }
    
    public LocalEditionWorkflow<String, String> getLocalWorkflowFrom(String nameSuffix, String login, String pass){
        DistributedEditionWorkflow<String, String> edition = getDistributedEdition(nameSuffix);
        if(edition == null)
            return null;
        UserInfos<String> uInf = null;
        uInf = edition.getCoAuthors().get(login);
        if((!edition.getOwnerLogin().equals(login)) && (uInf == null || !uInf.getPassword().equals(pass)))
            return null;
        if(uInf == null && !edition.getOwnerPassword().equals(pass))
            return null;
        if(uInf == null){
            uInf = new UserInfos<String>();
            uInf.setLogin(login);
            uInf.setPassword(pass);
            uInf.setCanDecideSynchro(true);
            uInf.setCanSeeGlobalWorkflow(true);
            ArrayList<String> view = new ArrayList<String>();
            view.add("globalView");
            view.addAll(edition.getGrammar().getSymbolsAxiomTop());
            uInf.setView(view);
        }
        Grammar<String, String> grammar = projectGrammar(edition.getGrammar(), uInf.getView());
        String doc = projectDocument(edition.getCurrentDocument(), uInf.getView(), edition);
        if(grammar == null || doc == null)
            return null;
        LocalEditionWorkflow<String, String> locEdit = new LocalEditionWorkflow<String, String>();
        locEdit.setCreationTime(DateTime.now().getMillis());
        locEdit.setOwnerLogin(edition.getOwnerLogin());
        locEdit.setOwnerPassword(edition.getOwnerPassword());
        locEdit.setLocalOwnerLogin(login);
        locEdit.setLocalOwnerPassword(pass);
        locEdit.setView(uInf.getView());
        locEdit.setCanDecideSynchro(uInf.isCanDecideSynchro());
        locEdit.setCanSeeGlobalWorkflow(uInf.isCanSeeGlobalWorkflow());
        locEdit.setWorkflowServer(edition.getWorkflowServer());
        locEdit.setWorkflowName(edition.getWorkflowName());
        locEdit.setWorkflowKey(edition.getWorkflowKey());
        locEdit.setWorkflowInitVector(edition.getWorkflowInitVector());
        locEdit.setWorkflowID(edition.getWorkflowID());
        locEdit.setGrammar(grammar);
        locEdit.setInitialDocument(doc);
        locEdit.setCurrentDocument(doc);
        return locEdit;
    }
    
    public void saveOldDistributedEdition(DistributedEditionWorkflow<String, String> edition) throws Throwable {
        String nameSuffix = edition.getWorkflowID();
        
        String path = getWorkspace() + File.separator + APPLConstants.DISTRIBUTED_ID + File.separator + "tdw_" + nameSuffix;
        File f = new File(path + ".tdw");
        if(f.exists()){
            f.delete();
        }
        String key = APPLConstants.decryptMessage(edition.getWorkflowKey());
        String initVect = APPLConstants.decryptMessage(edition.getWorkflowInitVector());
        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        String json = gson.toJson(edition);
        String encryptedJson = APPLConstants.encryptMessage(json, key, initVect);
        String toSave = getLangValue("tiny_distributed_workflow")+"\n";
        toSave += getLangValue("generate_by_tinyce")+" - "+DateTime.now()+"\n";
        toSave += APPLConstants.encryptMessage(key)+"\n";
        toSave += APPLConstants.encryptMessage(initVect)+"\n";
        toSave += encryptedJson;
        byte[] bytes = new byte[1024];
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
        BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(toSave.getBytes()));
        int i = 0;
        while ((i = in.read(bytes)) != -1){
            out.write(bytes,0,i);
        }
        out.flush();
        out.close();
        in.close();
    }
    
    public void saveOldLocalEdition(LocalEditionWorkflow<String, String> edition) throws Throwable {
        String nameSuffix = edition.getLocalID();
        
        String path = getWorkspace() + File.separator + APPLConstants.LOCAL_ID + File.separator + "tlw_" + nameSuffix;
        File f = new File(path + ".tlw");
        if(f.exists()){
            f.delete();
        }
        String key = generate(16);
        String initVect = generate(16);
        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();
        String json = gson.toJson(edition);
        String encryptedJson = APPLConstants.encryptMessage(json, key, initVect);
        String toSave = getLangValue("tiny_local_workflow")+"\n";
        toSave += getLangValue("generate_by_tinyce")+" - "+DateTime.now()+"\n";
        toSave += APPLConstants.encryptMessage(key)+"\n";
        toSave += APPLConstants.encryptMessage(initVect)+"\n";
        toSave += encryptedJson;
        byte[] bytes = new byte[1024];
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
        BufferedInputStream in = new BufferedInputStream(new ByteArrayInputStream(toSave.getBytes()));
        int i = 0;
        while ((i = in.read(bytes)) != -1){
            out.write(bytes,0,i);
        }
        out.flush();
        out.close();
        in.close();
    }
    
    public void changeCryptoKeys(EditionWorkflow<String, String> edition){
        edition.setWorkflowKey(generate(16));
        edition.setWorkflowInitVector(generate(16));
    }

    public Grammar<String, String> projectGrammar(Grammar<String, String> currentGram, ArrayList<String> view) {
        if(view.contains("A") && view.contains("B"))
            return Parsers.explGramAB();
        if(view.contains("A") && view.contains("C"))
            return Parsers.explGramAC();
        return null;
    }
    
    public String projectDocument(String currentDocument, ArrayList<String> view, DistributedEditionWorkflow<String, String> edition) {
        HaskellRunner runner = new HaskellRunner();
        String enginePath = getWorkspace() + "/"+ APPLConstants.WORK_ID + "/" + edition.getEngine() + ".hs";
        enginePath = enginePath.replace("\\", "/");
        String code = ":l \"" + enginePath + "\"\nprojection " + view.get(0) + " (" + Parsers.fromLinearizedDerMxGraph(currentDocument, edition.getGrammar()) + ")";
        String forest;
        try{
            forest = runner.executeCode(code);
            return forest;
        }catch(IOException ex){
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
	public LocalEditionWorkflow<String, String> getLocalEdition(String suffix) {
        LocalEditionWorkflow<String, String> ed = new LocalEditionWorkflow<String, String>();
        String fileName = "tlw_" + suffix + ".tlw";
        File file = new File(getWorkspace() + File.separator + APPLConstants.LOCAL_ID + File.separator + fileName);
        if(!file.exists() || !file.isFile() || !file.canRead())
            return null;
        String line = new String();
        String value = "";
        try{  
            FileReader fileReader = new FileReader(file);   
            BufferedReader bufferedReader = new BufferedReader(fileReader);  
            while((line = bufferedReader.readLine()) != null)  
                value += line+"~~~";
            bufferedReader.close();
            value = value.substring(0, value.length() - 3);
            String[] vals = value.split("~~~");
            final GsonBuilder builder = new GsonBuilder();
            final Gson gson = builder.create();
            String json = null;
            if(vals.length > 4){
                String key = APPLConstants.decryptMessage(vals[2]);
                String initVect = APPLConstants.decryptMessage(vals[3]);
                String mess = vals[4];
                for(int j = 5; j < vals.length; j++)
                    mess += vals[j];
                json = APPLConstants.decryptMessage(mess, key, initVect);
            }else{
                throw new Exception(getLangValue("incorrect_file_input"));
            }
            ed = gson.fromJson(json, LocalEditionWorkflow.class);
        }catch(Throwable ex){
            return null;
        }
        return ed;
    }
    
    public ArrayList<LocalEditionWorkflow<String, String>> getLocalEditions(){
        ArrayList<LocalEditionWorkflow<String, String>> localEditions = new ArrayList<LocalEditionWorkflow<String, String>>();
        String path = getWorkspace() + File.separator + APPLConstants.LOCAL_ID;
        File f = new File(path);
        try{
            ArrayList<String> names = APPLConstants.getFilesNamesMatchingPattern(f, "tlw_([a-zA-Z0-9_ -]{1,}).tlw");
            ArrayList<String> nameSuffixes = new ArrayList<String>();
            for(String name : names)
                nameSuffixes.add(name.substring(4, name.length() - 4));
            LocalEditionWorkflow<String, String> edition = null;
            for(String suffix : nameSuffixes){
                edition = getLocalEdition(suffix);
                if(edition != null && edition.getLocalOwnerLogin().equals(APPLConstants.encryptMessage(user.get("login"))) && 
                        edition.getLocalOwnerPassword().equals(APPLConstants.encryptMessage(user.get("password"))))
                    localEditions.add(edition);
            }
        }catch(Throwable ex){
            return null;
        }
        return localEditions;
    }
    
    public ArrayList<HashMap<String, String>> getPotentialWorkflowsFor(String login, String pass){
        ArrayList<HashMap<String, String>> potentialWorkflows = new ArrayList<HashMap<String, String>>();
        String path = getWorkspace() + File.separator + APPLConstants.DISTRIBUTED_ID;
        File f = new File(path);
        try{
            ArrayList<String> names = APPLConstants.getFilesNamesMatchingPattern(f, "tdw_([a-zA-Z0-9_ -]{1,}).tdw");
            ArrayList<String> nameSuffixes = new ArrayList<String>();
            for(String name : names)
                nameSuffixes.add(name.substring(4, name.length() - 4));
            DistributedEditionWorkflow<String, String> edition = null;
            for(String suffix : nameSuffixes){
                edition = getDistributedEdition(suffix);
                if(edition != null && isCoauthor(login, pass, edition)){
                    HashMap<String, String> wf = new HashMap<String, String>();
                    wf.put("name", edition.getWorkflowName());
                    wf.put("creator", edition.getOwnerLogin());
                    wf.put("id", APPLConstants.encryptMessage(edition.getWorkflowID()));
                    wf.put("server", edition.getWorkflowServer());
                    potentialWorkflows.add(wf);
                }
            }
        }catch(Throwable ex){
            return null;
        }
        return potentialWorkflows;
    }

    public boolean isCoauthor(DistributedEditionWorkflow<String, String> edition) {
        try{
            String login = APPLConstants.encryptMessage(user.get("login"));
            String pass = APPLConstants.encryptMessage(user.get("password"));
            if(edition.getOwnerLogin().equals(login) && edition.getOwnerPassword().equals(pass))
                return true;
            if(edition.getCoAuthors() != null && edition.getCoAuthors().get(login) != null)
                return edition.getCoAuthors().get(login).getPassword().equals(pass);
            return false;
        }catch(Throwable e){
            return false;
        }
    }
    
    public boolean isCoauthor(String login, String pass, DistributedEditionWorkflow<String, String> edition) {
        if(edition.getOwnerLogin().equals(login) && edition.getOwnerPassword().equals(pass))
            return true;
        if(edition.getCoAuthors() != null && edition.getCoAuthors().get(login) != null)
            return edition.getCoAuthors().get(login).getPassword().equals(pass);
        return false;
    }

    public boolean deleteWorkflow(HashMap<String, String> workflow, String cryptedLogin, String cryptedPass) {
        boolean deleted = false;
        try{
            String suffix = APPLConstants.decryptMessage(workflow.get("id"));
            if(suffix != null){
                DistributedEditionWorkflow<String, String> edition = getDistributedEdition(suffix);
                if(edition != null && edition.getOwnerLogin().equals(cryptedLogin) && edition.getOwnerPassword().equals(cryptedPass)){
                    String path = getWorkspace() + File.separator + APPLConstants.DISTRIBUTED_ID + File.separator + "tdw_" + suffix;
                    File f = new File(path + ".tdw");
                    f.delete();
                    return true;
                }
            }
        }catch(Throwable e){
            return false;
        }
        return deleted;
    }
}
