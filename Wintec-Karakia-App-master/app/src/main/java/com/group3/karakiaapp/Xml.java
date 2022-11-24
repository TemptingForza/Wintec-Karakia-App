package com.group3.karakiaapp;

import android.content.res.XmlResourceParser;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;

public class Xml {
    public String Name;
    public HashMap<String, String> Attributes = new HashMap<>();
    public List<Xml> Children = new ArrayList<>();
    public String Text;

    @NonNull
    @Override
    public String toString() {
        return Text;
    }

    public static Xml FromParser(XmlResourceParser parser)
    {
        int event = -1;
        List<Xml> Path = new ArrayList<>();
        Path.add(new Xml());
        while (event != XmlResourceParser.END_DOCUMENT)
        {
            try {
                Xml current = Path.get(0);
                if (event == XmlResourceParser.START_TAG)
                {
                    Xml n = new Xml();
                    n.Name = parser.getName();
                    for (int i = 0; i < parser.getAttributeCount(); i++)
                        n.Attributes.put(parser.getAttributeName(i), parser.getAttributeValue(i));
                    current.Children.add(n);
                    Path.add(0,n);
                } else if (event == XmlResourceParser.END_TAG) {
                    int count = 0;
                    for (int i = 0; i < Path.size(); i++)
                        if (Path.get(i).Name.equals(parser.getName())) {
                            count = i + 1;
                            break;
                        }
                    for (int i = 0; i < count; i++)
                        Path.remove(0);
                }
                else if (event == XmlResourceParser.TEXT)
                {
                    Xml n = new Xml();
                    n.Text = parser.getText();
                    current.Children.add(n);
                }
                parser.next();
                event = parser.getEventType();
            } catch (Exception e) {}
        }
        return Path.get(0);
    }
}
