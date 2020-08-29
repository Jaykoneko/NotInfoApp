package net.sourceforge.jtds.jdbcx;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import net.sourceforge.jtds.jdbc.Driver;
import net.sourceforge.jtds.jdbc.Messages;

public class JtdsObjectFactory implements ObjectFactory {
    public Object getObjectInstance(Object obj, Name name, Context context, Hashtable hashtable) throws Exception {
        Reference reference = (Reference) obj;
        if (reference.getClassName().equals(JtdsDataSource.class.getName())) {
            return new JtdsDataSource(loadProps(reference, new String[]{"description", Driver.APPNAME, Driver.AUTOCOMMIT, Driver.BATCHSIZE, Driver.BINDADDRESS, Driver.BUFFERDIR, Driver.BUFFERMAXMEMORY, Driver.BUFFERMINPACKETS, Driver.CACHEMETA, Driver.CHARSET, Driver.DATABASENAME, Driver.DOMAIN, Driver.INSTANCE, Driver.LANGUAGE, Driver.LASTUPDATECOUNT, Driver.LOBBUFFER, Driver.LOGFILE, Driver.LOGINTIMEOUT, Driver.MACADDRESS, Driver.MAXSTATEMENTS, Driver.NAMEDPIPE, Driver.PACKETSIZE, Driver.PASSWORD, Driver.PORTNUMBER, Driver.PREPARESQL, Driver.PROGNAME, Driver.SERVERNAME, Driver.SERVERTYPE, Driver.SOTIMEOUT, Driver.SOKEEPALIVE, Driver.PROCESSID, Driver.SSL, Driver.TCPNODELAY, Driver.TDS, Driver.USECURSORS, Driver.USEJCIFS, Driver.USENTLMV2, Driver.USEKERBEROS, Driver.USELOBS, Driver.USER, Driver.SENDSTRINGPARAMETERSASUNICODE, Driver.WSID, Driver.XAEMULATION}));
        }
        return null;
    }

    private HashMap loadProps(Reference reference, String[] strArr) {
        HashMap hashMap = new HashMap();
        HashMap hashMap2 = new HashMap();
        Enumeration all = reference.getAll();
        while (all.hasMoreElements()) {
            RefAddr refAddr = (RefAddr) all.nextElement();
            hashMap2.put(refAddr.getType().toLowerCase(), refAddr.getContent());
        }
        for (int i = 0; i < strArr.length; i++) {
            String str = (String) hashMap2.get(strArr[i].toLowerCase());
            if (str == null) {
                str = (String) hashMap2.get(Messages.get(strArr[i].toLowerCase()));
            }
            if (str != null) {
                hashMap.put(strArr[i], str);
            }
        }
        return hashMap;
    }
}
