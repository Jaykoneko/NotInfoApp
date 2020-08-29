package net.sourceforge.jtds.jdbc;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class Messages {
    private static final String DEFAULT_RESOURCE = "net.sourceforge.jtds.jdbc.Messages";
    private static ResourceBundle defaultResource;

    private Messages() {
    }

    public static String get(String str) {
        return get(str, (Object[]) null);
    }

    public static String get(String str, Object obj) {
        return get(str, new Object[]{obj});
    }

    static String get(String str, Object obj, Object obj2) {
        return get(str, new Object[]{obj, obj2});
    }

    private static String get(String str, Object[] objArr) {
        try {
            String string = loadResourceBundle().getString(str);
            if (objArr != null) {
                if (objArr.length != 0) {
                    return new MessageFormat(string).format(objArr);
                }
            }
            return string;
        } catch (MissingResourceException unused) {
            StringBuilder sb = new StringBuilder();
            sb.append("No message resource found for message property ");
            sb.append(str);
            throw new RuntimeException(sb.toString());
        }
    }

    static void loadDriverProperties(Map map, Map map2) {
        ResourceBundle loadResourceBundle = loadResourceBundle();
        Enumeration keys = loadResourceBundle.getKeys();
        while (keys.hasMoreElements()) {
            String str = (String) keys.nextElement();
            if (str.startsWith("prop.desc.")) {
                map2.put(str.substring(10), loadResourceBundle.getString(str));
            } else if (str.startsWith("prop.")) {
                map.put(str.substring(5), loadResourceBundle.getString(str));
            }
        }
    }

    private static ResourceBundle loadResourceBundle() {
        if (defaultResource == null) {
            defaultResource = ResourceBundle.getBundle(DEFAULT_RESOURCE);
        }
        return defaultResource;
    }
}
