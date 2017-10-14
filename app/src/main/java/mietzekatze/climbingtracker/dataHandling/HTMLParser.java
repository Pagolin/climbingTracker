package mietzekatze.climbingtracker.dataHandling;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lisza on 10.10.17.
 * As long as there's no server REST based service to retreve route Data, this class is responsible for
 * parsing html data tables into database entries
 */

public class HTMLParser {


    public static String readRaw(Context context,int res_id) {
        InputStream rawStream = context.getResources().openRawResource(res_id);
        String htmlString = null;
        try {
            htmlString = readFromStream(rawStream);
        } catch (IOException e) {
            Log.e("readRaw: ", e.getMessage());
        }
        Log.i("HTMLParser","parsed String:" + htmlString);
        return htmlString;
    }

    @NonNull
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
            return output.toString();
        } else {
            return null;
        }
    }

    //TODO: Chech for a smarter formate with respect to subsequent SQL

    @Nullable
    public static Map<String, List<String>> parseHTMLTable(Context context, int res_id){
        Map<String, List<String>> mapHeadersToValues = new HashMap<>();
        String htmlAsString = readRaw(context, res_id);
        if(htmlAsString != null){
            Document doc = Jsoup.parse(htmlAsString, "UTF-8");
            Element firstTable = doc.getElementsByTag("table").first();
            Elements rows = firstTable.getElementsByTag("tr");
            Elements colHeaders = rows.first().select("td");
            /*Getting the keys from the first row*/
            List<String> headers = new ArrayList<>();
            for(Element entry : colHeaders){
                String colTitle = entry.text();
                headers.add(colTitle);
                mapHeadersToValues.put(colTitle, new ArrayList<String>());
            }
            /*Getting the value lists from 2nd to last row*/

            for(int i = 1; i < rows.size(); i++){
                Elements cells = rows.get(i).select("td");
                for(int j = 0; j < cells.size(); j++){
                    Element cell = cells.get(j);
                    mapHeadersToValues.get(headers.get(j)).add(cell.text());
                }
            }
        } else {
            Log.i("HTMLParser", "could not read from raw");
        }
        Log.i("parseHTMLTable","Entries of mapHeadersToValues "+ mapHeadersToValues.entrySet());
        return mapHeadersToValues;
    }
}