package co.com.ies.fidelizacioncliente.utils;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Clase usada para cargar y leer archivos xml
 */
public class XmlUtils {
    private static final String ns = null;

    /**
     * Obtener los campos existentes en el archivo
     * @param in
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public HashMap<String, String> getConfig(InputStream in) throws XmlPullParserException, IOException {
        HashMap<String, String> hashMap = new HashMap<>();

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            /// readFeed(parser);
            parser.require(XmlPullParser.START_TAG, ns, AppConstants.ConfigTags.CONFIG);
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                // Starts by looking for the entry tag

                String value = readTag(parser, name);
                if (!StringUtils.isNullOrEmpty(value)) {
                    hashMap.put(name, value);
                }

                /*} else {
                    skip(parser);
                }*/
            }
        } finally {
            in.close();
        }

        return hashMap;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    /**
     * Obtener valor de una etiqueta
     * @param parser
     * @param tagName
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readTag(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tagName);
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tagName);
        return summary;
    }

}

