package me.amfero.blmclient.util.imgurutil;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class CopyToClipboard{

    private String copiedText;

    public boolean copy(String message){
        if(message != null){
            StringSelection stringSelection = new StringSelection(message);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
            copiedText = message;
            return true;
        }else{
            return false;
        }
    }

    public String getCopy(){
        return copiedText;
    }
}