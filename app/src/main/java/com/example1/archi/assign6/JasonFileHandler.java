package com.example1.archi.assign6;

import android.content.Context;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by archi on 3/14/2017.
 */

public class JasonFileHandler {
    private final static String TAG="JasonFileHandler";
    private List<Source> sourceList;
    private List<Artical> articalList;
    private Context context;
    public JasonFileHandler(Context context) {
        this.context = context;
    }


    public List<Source> readSourceData(InputStream in, String jsonData) throws IOException {

        JsonReader jsonReader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
               readSources(jsonReader);
        } finally {
            jsonReader.close();
        }
        return sourceList;
    }
    public List<Artical> readArticalData(InputStream in, String jsonData) throws IOException {

        JsonReader jsonReader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            readArtical(jsonReader);
        } finally {
            jsonReader.close();
        }
        return articalList;
    }
    /*************************************Reading of Sources Start******************************************/
    private List<Source> readSources(JsonReader reader) throws IOException {
        sourceList = new ArrayList<>();
        String offices=null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("sources")) {
                readSourceArray(reader);
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return sourceList;
    }

    private void readSourceArray(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
                readSourceObject(reader);
        }
        reader.endArray();
    }

    private void readSourceObject(JsonReader reader) throws IOException {
         String sourceID= null;
         String sourceName =null;
         String sourceURL = null;
         String sourceCategory = null;
         Source newSource;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
               sourceID = reader.nextString();
            }else if (name.equals("name")) {
                sourceName=reader.nextString();
            }else if (name.equals("url")) {
                sourceURL = reader.nextString();
            }else if (name.equals("category")) {
                sourceCategory = reader.nextString();
            }else{
                reader.skipValue();
            }
        }
        newSource = new Source(sourceID,sourceName,sourceURL,sourceCategory);
        sourceList.add(newSource);
        Log.d(TAG,":: "+sourceID+" :: ");
        reader.endObject();
    }

    /*************************************Reading of offices End******************************************/

    /*************************************Reading of officials start******************************************/
    private List<Artical> readArtical(JsonReader reader) throws IOException {
        articalList = new ArrayList<>();
        String offices=null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("articles")) {
                readArticalArray(reader);
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return articalList;
    }

    private void readArticalArray(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readArticalObject(reader);
        }
        reader.endArray();
    }

    private void readArticalObject(JsonReader reader) throws IOException {
        String articalAuthor = null;
        String articalTitle = null;
        String articalDescrption = null;
        String articalURL = null;
        String articalUrlToImag = null;
        String articalPublishedAt = null;
        Artical newArtical;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("author") && reader.peek() != JsonToken.NULL) {
                articalAuthor = reader.nextString();
            } else if (name.equals("title") && reader.peek() != JsonToken.NULL) {
                articalTitle = reader.nextString();
            } else if (name.equals("description") && reader.peek() != JsonToken.NULL) {
                articalDescrption = reader.nextString();
            } else if (name.equals("url") && reader.peek() != JsonToken.NULL) {
                articalURL = reader.nextString();
            } else if (name.equals("urlToImage") && reader.peek() != JsonToken.NULL) {
                articalUrlToImag = reader.nextString();
            } else if (name.equals("publishedAt") && reader.peek() != JsonToken.NULL) {
                articalPublishedAt = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        newArtical = new Artical(articalAuthor,articalTitle,articalDescrption,articalURL,articalUrlToImag,articalPublishedAt);
        Log.d(TAG," :: "+newArtical);
        articalList.add(newArtical);
        reader.endObject();
    }
}
