package me.amfero.blmclient.util;

import java.io.*;
import java.util.ArrayList;

public class FileUtil
{
    public static void saveFile(File file, ArrayList<String> content) throws IOException 
    {
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        for (String s : content) 
        {
            out.write(s);
            out.write("\r\n");
        }
        out.close();
    }

    public static ArrayList<String> loadFile(File file) throws IOException 
    {
        ArrayList<String> content = new ArrayList<>();
        FileInputStream stream = new FileInputStream(file.getAbsolutePath());
        DataInputStream in = new DataInputStream(stream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        while ((line = br.readLine()) != null) content.add(line);
        br.close();
        return content;
    }

	public static void saveFileString(File file, String string) throws IOException 
	{
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(string);
        out.close();
	}
}
