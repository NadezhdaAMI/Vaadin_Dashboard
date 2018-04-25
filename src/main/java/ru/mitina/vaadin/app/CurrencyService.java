package ru.mitina.vaadin.app;


import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CurrencyService {

    public static String url = "https://www.cbr-xml-daily.ru/daily_json.js";

    public static String file = "/home/user/demo/app/daily_json.js";

    public static void downloadUsingStream(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

    public static String getStringJson(){
        String contents = "";
        try {
            contents = readUsingFiles(file);
        } catch (IOException e) {

            e.printStackTrace();
        }
        return contents;
    }

    private static String readUsingFiles(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
}
