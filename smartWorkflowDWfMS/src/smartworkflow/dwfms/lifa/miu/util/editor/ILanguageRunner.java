/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package smartworkflow.dwfms.lifa.miu.util.editor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 *
 * @author Ndadji Maxime
 */
public class ILanguageRunner{
    protected Process process = null;
    protected String language;
    protected String command;

    public ILanguageRunner(String language, String command){
        this.language = language;
        this.command = command;
    }
    
    public void startExecProcess() throws IOException{
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        process = processBuilder.start();
    }
	
    public void killExecProcess(){
        if(process != null)
            process.destroy();
    }
	
    public void setExecCode(String code) throws IOException {
        OutputStream stdin = process.getOutputStream();
        OutputStreamWriter stdinWriter = new OutputStreamWriter(stdin);
        try{
            stdinWriter.write(code);
        }finally{
            try{stdinWriter.close();}catch(IOException e){}
            try{stdin.close();}catch(IOException e){}
        }
    }
	
    public void getExecErrors() throws IOException{
        InputStream stdout = process.getErrorStream();
        InputStreamReader stdoutReader = new InputStreamReader(stdout);
        BufferedReader stdoutBuffer = new BufferedReader (stdoutReader);
        StringBuffer errorBuffer = null;
        try{
            String line = null;
            while((line = stdoutBuffer.readLine()) != null){
                if (errorBuffer == null)
                    errorBuffer = new StringBuffer();
                errorBuffer.append(line);
            }
        }finally{
            try{stdoutBuffer.close();}catch(IOException e){}
            try{stdoutReader.close();}catch(IOException e){}
            try{stdout.close();}catch(IOException e){}
        }
        if(errorBuffer != null)
            throw new IOException(errorBuffer.toString());
    }
	
    public String getExecResult() throws IOException{
        InputStream stdout = process.getInputStream();
        InputStreamReader stdoutReader = new InputStreamReader(stdout);
        BufferedReader stdoutBuffer = new BufferedReader(stdoutReader);
        StringBuffer resultBuffer = null;
        try{
            String line = null;
            while((line = stdoutBuffer.readLine()) != null){
                if (resultBuffer == null)
                    resultBuffer = new StringBuffer();
                resultBuffer.append(line);
            }
        }finally{
            try{stdoutBuffer.close();}catch(IOException e){}
            try{stdoutReader.close();}catch(IOException e){}
            try{stdout.close();}catch(IOException e){}
        }
        if(resultBuffer != null)
            return resultBuffer.toString();
        return null;
    }

    public String getCommand(){
        return command;
    }

    public void setCommand(String command){
        this.command = command;
    }

    public String getLanguage(){
        return language;
    }

    public void setLanguage(String language){
        this.language = language;
    }
    
    public String executeCode(String code) throws IOException{
        startExecProcess();
        setExecCode(code);
        getExecErrors();
        String result = getExecResult();
        killExecProcess();
        return result;
    }
}
