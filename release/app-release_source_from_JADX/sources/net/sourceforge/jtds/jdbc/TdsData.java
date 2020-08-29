package net.sourceforge.jtds.jdbc;

import androidx.core.provider.FontsContractCompat.FontRequestCallback;
import com.bumptech.glide.load.Key;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;

public class TdsData {
    private static final int DATEN = 40;
    private static final int DATETIME2N = 42;
    private static final int DATETIMEOFFSETN = 43;
    static final int DEFAULT_PRECISION_28 = 28;
    static final int DEFAULT_PRECISION_38 = 38;
    static final int DEFAULT_SCALE = 10;
    private static final int MS_LONGVAR_MAX = 8000;
    private static final int SYBBINARY = 45;
    private static final int SYBBIT = 50;
    private static final int SYBBITN = 104;
    private static final int SYBCHAR = 47;
    private static final int SYBDATE = 49;
    private static final int SYBDATEN = 123;
    private static final int SYBDATETIME = 61;
    private static final int SYBDATETIME4 = 58;
    private static final int SYBDATETIMN = 111;
    private static final int SYBDECIMAL = 106;
    private static final int SYBFLT8 = 62;
    private static final int SYBFLTN = 109;
    private static final int SYBIMAGE = 34;
    private static final int SYBINT1 = 48;
    private static final int SYBINT2 = 52;
    private static final int SYBINT4 = 56;
    private static final int SYBINT8 = 127;
    private static final int SYBINTN = 38;
    private static final int SYBLONGBINARY = 225;
    static final int SYBLONGDATA = 36;
    private static final int SYBMONEY = 60;
    private static final int SYBMONEY4 = 122;
    private static final int SYBMONEYN = 110;
    private static final int SYBNTEXT = 99;
    private static final int SYBNUMERIC = 108;
    private static final int SYBNVARCHAR = 103;
    private static final int SYBREAL = 59;
    private static final int SYBSINT1 = 64;
    private static final int SYBSINT8 = 191;
    private static final int SYBTEXT = 35;
    private static final int SYBTIME = 51;
    private static final int SYBTIMEN = 147;
    private static final int SYBUINT2 = 65;
    private static final int SYBUINT4 = 66;
    private static final int SYBUINT8 = 67;
    private static final int SYBUINTN = 68;
    private static final int SYBUNIQUE = 36;
    private static final int SYBUNITEXT = 174;
    private static final int SYBVARBINARY = 37;
    private static final int SYBVARCHAR = 39;
    private static final int SYBVARIANT = 98;
    private static final int SYBVOID = 31;
    private static final int SYB_CHUNK_SIZE = 8192;
    private static final int SYB_LONGVAR_MAX = 16384;
    private static final int TIMEN = 41;
    private static final int UDT_BINARY = 3;
    private static final int UDT_CHAR = 1;
    private static final int UDT_LONGSYSNAME = 42;
    private static final int UDT_NCHAR = 24;
    private static final int UDT_NEWSYSNAME = 256;
    private static final int UDT_NVARCHAR = 25;
    private static final int UDT_SYSNAME = 18;
    private static final int UDT_TIMESTAMP = 80;
    private static final int UDT_UNICHAR = 34;
    private static final int UDT_UNITEXT = 36;
    private static final int UDT_UNIVARCHAR = 35;
    private static final int UDT_VARBINARY = 4;
    private static final int UDT_VARCHAR = 2;
    private static final int VAR_MAX = 255;
    private static final int XML = 241;
    private static final int XSYBBINARY = 173;
    private static final int XSYBCHAR = 175;
    private static final int XSYBNCHAR = 239;
    private static final int XSYBNVARCHAR = 231;
    private static final int XSYBVARBINARY = 165;
    private static final int XSYBVARCHAR = 167;
    private static final TypeInfo[] types;

    private static class TypeInfo {
        public final int displaySize;
        public final boolean isCollation;
        public final boolean isSigned;
        public final int jdbcType;
        public final int precision;
        public final int size;
        public final String sqlType;

        TypeInfo(String str, int i, int i2, int i3, boolean z, boolean z2, int i4) {
            this.sqlType = str;
            this.size = i;
            this.precision = i2;
            this.displaySize = i3;
            this.isSigned = z;
            this.isCollation = z2;
            this.jdbcType = i4;
        }
    }

    public static int getTdsVersion(int i) {
        if (i >= 1895825409) {
            return 5;
        }
        if (i >= 117506048) {
            return 4;
        }
        if (i >= 117440512) {
            return 3;
        }
        return i >= 83886080 ? 2 : 1;
    }

    static {
        TypeInfo[] typeInfoArr = new TypeInfo[256];
        types = typeInfoArr;
        TypeInfo typeInfo = new TypeInfo("char", -1, -1, 1, false, false, 1);
        typeInfoArr[47] = typeInfo;
        TypeInfo[] typeInfoArr2 = types;
        TypeInfo typeInfo2 = new TypeInfo("varchar", -1, -1, 1, false, false, 12);
        typeInfoArr2[39] = typeInfo2;
        TypeInfo[] typeInfoArr3 = types;
        TypeInfo typeInfo3 = new TypeInfo("int", -1, 10, 11, true, false, 4);
        typeInfoArr3[38] = typeInfo3;
        TypeInfo[] typeInfoArr4 = types;
        TypeInfo typeInfo4 = new TypeInfo("tinyint", 1, 3, 4, false, false, -6);
        typeInfoArr4[48] = typeInfo4;
        TypeInfo[] typeInfoArr5 = types;
        TypeInfo typeInfo5 = new TypeInfo("smallint", 2, 5, 6, true, false, 5);
        typeInfoArr5[52] = typeInfo5;
        TypeInfo[] typeInfoArr6 = types;
        TypeInfo typeInfo6 = new TypeInfo("int", 4, 10, 11, true, false, 4);
        typeInfoArr6[56] = typeInfo6;
        TypeInfo[] typeInfoArr7 = types;
        TypeInfo typeInfo7 = new TypeInfo("bigint", 8, 19, 20, true, false, -5);
        typeInfoArr7[SYBINT8] = typeInfo7;
        TypeInfo[] typeInfoArr8 = types;
        TypeInfo typeInfo8 = new TypeInfo("float", 8, 15, 24, true, false, 8);
        typeInfoArr8[62] = typeInfo8;
        TypeInfo[] typeInfoArr9 = types;
        TypeInfo typeInfo9 = new TypeInfo("datetime", 8, 23, 23, false, false, 93);
        typeInfoArr9[61] = typeInfo9;
        TypeInfo[] typeInfoArr10 = types;
        TypeInfo typeInfo10 = new TypeInfo("bit", 1, 1, 1, false, false, -7);
        typeInfoArr10[50] = typeInfo10;
        TypeInfo[] typeInfoArr11 = types;
        TypeInfo typeInfo11 = new TypeInfo("text", -4, -1, -1, false, true, 2005);
        typeInfoArr11[35] = typeInfo11;
        TypeInfo[] typeInfoArr12 = types;
        TypeInfo typeInfo12 = new TypeInfo("ntext", -4, -1, -1, false, true, 2005);
        typeInfoArr12[99] = typeInfo12;
        TypeInfo[] typeInfoArr13 = types;
        TypeInfo typeInfo13 = new TypeInfo("unitext", -4, -1, -1, false, true, 2005);
        typeInfoArr13[SYBUNITEXT] = typeInfo13;
        TypeInfo[] typeInfoArr14 = types;
        TypeInfo typeInfo14 = new TypeInfo("image", -4, -1, -1, false, false, 2004);
        typeInfoArr14[34] = typeInfo14;
        TypeInfo[] typeInfoArr15 = types;
        TypeInfo typeInfo15 = new TypeInfo("smallmoney", 4, 10, 12, true, false, 3);
        typeInfoArr15[122] = typeInfo15;
        TypeInfo[] typeInfoArr16 = types;
        TypeInfo typeInfo16 = new TypeInfo("money", 8, 19, 21, true, false, 3);
        typeInfoArr16[60] = typeInfo16;
        TypeInfo[] typeInfoArr17 = types;
        TypeInfo typeInfo17 = new TypeInfo("smalldatetime", 4, 16, 19, false, false, 93);
        typeInfoArr17[58] = typeInfo17;
        TypeInfo[] typeInfoArr18 = types;
        TypeInfo typeInfo18 = new TypeInfo("real", 4, 7, 14, true, false, 7);
        typeInfoArr18[59] = typeInfo18;
        TypeInfo[] typeInfoArr19 = types;
        TypeInfo typeInfo19 = new TypeInfo("binary", -1, -1, 2, false, false, -2);
        typeInfoArr19[45] = typeInfo19;
        TypeInfo[] typeInfoArr20 = types;
        TypeInfo typeInfo20 = new TypeInfo("void", -1, 1, 1, false, false, 0);
        typeInfoArr20[31] = typeInfo20;
        TypeInfo[] typeInfoArr21 = types;
        TypeInfo typeInfo21 = new TypeInfo("varbinary", -1, -1, -1, false, false, -3);
        typeInfoArr21[37] = typeInfo21;
        TypeInfo[] typeInfoArr22 = types;
        TypeInfo typeInfo22 = new TypeInfo("nvarchar", -1, -1, -1, false, false, 12);
        typeInfoArr22[103] = typeInfo22;
        TypeInfo[] typeInfoArr23 = types;
        TypeInfo typeInfo23 = new TypeInfo("bit", -1, 1, 1, false, false, -7);
        typeInfoArr23[104] = typeInfo23;
        TypeInfo[] typeInfoArr24 = types;
        TypeInfo typeInfo24 = new TypeInfo("numeric", -1, -1, -1, true, false, 2);
        typeInfoArr24[108] = typeInfo24;
        TypeInfo[] typeInfoArr25 = types;
        TypeInfo typeInfo25 = new TypeInfo("decimal", -1, -1, -1, true, false, 3);
        typeInfoArr25[106] = typeInfo25;
        TypeInfo[] typeInfoArr26 = types;
        TypeInfo typeInfo26 = new TypeInfo("float", -1, 15, 24, true, false, 8);
        typeInfoArr26[109] = typeInfo26;
        TypeInfo[] typeInfoArr27 = types;
        TypeInfo typeInfo27 = new TypeInfo("money", -1, 19, 21, true, false, 3);
        typeInfoArr27[110] = typeInfo27;
        TypeInfo[] typeInfoArr28 = types;
        TypeInfo typeInfo28 = new TypeInfo("datetime", -1, 23, 23, false, false, 93);
        typeInfoArr28[111] = typeInfo28;
        TypeInfo[] typeInfoArr29 = types;
        TypeInfo typeInfo29 = new TypeInfo("date", 4, 10, 10, false, false, 91);
        typeInfoArr29[49] = typeInfo29;
        TypeInfo[] typeInfoArr30 = types;
        TypeInfo typeInfo30 = new TypeInfo("time", 4, 8, 8, false, false, 92);
        typeInfoArr30[51] = typeInfo30;
        TypeInfo[] typeInfoArr31 = types;
        TypeInfo typeInfo31 = new TypeInfo("date", -1, 10, 10, false, false, 91);
        typeInfoArr31[123] = typeInfo31;
        TypeInfo[] typeInfoArr32 = types;
        TypeInfo typeInfo32 = new TypeInfo("time", -1, 8, 8, false, false, 92);
        typeInfoArr32[SYBTIMEN] = typeInfo32;
        TypeInfo[] typeInfoArr33 = types;
        TypeInfo typeInfo33 = new TypeInfo("char", -2, -1, -1, false, true, 1);
        typeInfoArr33[XSYBCHAR] = typeInfo33;
        TypeInfo[] typeInfoArr34 = types;
        TypeInfo typeInfo34 = new TypeInfo("varchar", -2, -1, -1, false, true, 12);
        typeInfoArr34[XSYBVARCHAR] = typeInfo34;
        TypeInfo[] typeInfoArr35 = types;
        TypeInfo typeInfo35 = new TypeInfo("nvarchar", -2, -1, -1, false, true, 12);
        typeInfoArr35[XSYBNVARCHAR] = typeInfo35;
        TypeInfo[] typeInfoArr36 = types;
        TypeInfo typeInfo36 = new TypeInfo("nchar", -2, -1, -1, false, true, 1);
        typeInfoArr36[XSYBNCHAR] = typeInfo36;
        TypeInfo[] typeInfoArr37 = types;
        TypeInfo typeInfo37 = new TypeInfo("varbinary", -2, -1, -1, false, false, -3);
        typeInfoArr37[XSYBVARBINARY] = typeInfo37;
        TypeInfo[] typeInfoArr38 = types;
        TypeInfo typeInfo38 = new TypeInfo("binary", -2, -1, -1, false, false, -2);
        typeInfoArr38[XSYBBINARY] = typeInfo38;
        TypeInfo[] typeInfoArr39 = types;
        TypeInfo typeInfo39 = new TypeInfo("varbinary", -5, -1, 2, false, false, -2);
        typeInfoArr39[SYBLONGBINARY] = typeInfo39;
        TypeInfo[] typeInfoArr40 = types;
        TypeInfo typeInfo40 = new TypeInfo("tinyint", 1, 2, 3, false, false, -6);
        typeInfoArr40[64] = typeInfo40;
        TypeInfo[] typeInfoArr41 = types;
        TypeInfo typeInfo41 = new TypeInfo("unsigned smallint", 2, 5, 6, false, false, 4);
        typeInfoArr41[65] = typeInfo41;
        TypeInfo[] typeInfoArr42 = types;
        TypeInfo typeInfo42 = new TypeInfo("unsigned int", 4, 10, 11, false, false, -5);
        typeInfoArr42[66] = typeInfo42;
        TypeInfo[] typeInfoArr43 = types;
        TypeInfo typeInfo43 = new TypeInfo("unsigned bigint", 8, 20, 20, false, false, 3);
        typeInfoArr43[67] = typeInfo43;
        TypeInfo[] typeInfoArr44 = types;
        TypeInfo typeInfo44 = new TypeInfo("unsigned int", -1, 10, 11, true, false, -5);
        typeInfoArr44[68] = typeInfo44;
        TypeInfo[] typeInfoArr45 = types;
        TypeInfo typeInfo45 = new TypeInfo("uniqueidentifier", -1, 36, 36, false, false, 1);
        typeInfoArr45[36] = typeInfo45;
        TypeInfo[] typeInfoArr46 = types;
        TypeInfo typeInfo46 = new TypeInfo("sql_variant", -5, 0, MS_LONGVAR_MAX, false, false, 12);
        typeInfoArr46[98] = typeInfo46;
        TypeInfo[] typeInfoArr47 = types;
        TypeInfo typeInfo47 = new TypeInfo("bigint", 8, 19, 20, true, false, -5);
        typeInfoArr47[SYBSINT8] = typeInfo47;
        TypeInfo[] typeInfoArr48 = types;
        TypeInfo typeInfo48 = new TypeInfo("xml", -4, -1, -1, false, true, 2009);
        typeInfoArr48[XML] = typeInfo48;
        TypeInfo[] typeInfoArr49 = types;
        TypeInfo typeInfo49 = new TypeInfo("date", 3, 10, 10, false, false, 91);
        typeInfoArr49[40] = typeInfo49;
        TypeInfo[] typeInfoArr50 = types;
        TypeInfo typeInfo50 = new TypeInfo("time", -1, -1, -1, false, false, 92);
        typeInfoArr50[41] = typeInfo50;
        TypeInfo[] typeInfoArr51 = types;
        TypeInfo typeInfo51 = new TypeInfo("datetime2", -1, -1, -1, false, false, 93);
        typeInfoArr51[42] = typeInfo51;
        TypeInfo[] typeInfoArr52 = types;
        TypeInfo typeInfo52 = new TypeInfo("datetimeoffset", -1, -1, -1, false, false, 93);
        typeInfoArr52[43] = typeInfo52;
    }

    static int getCollation(ResponseStream responseStream, ColInfo colInfo) throws IOException {
        if (!isCollation(colInfo)) {
            return 0;
        }
        colInfo.collation = new byte[5];
        responseStream.read(colInfo.collation);
        return 5;
    }

    static void setColumnCharset(ColInfo colInfo, JtdsConnection jtdsConnection) throws SQLException {
        if (jtdsConnection.isCharsetSpecified()) {
            colInfo.charsetInfo = jtdsConnection.getCharsetInfo();
        } else if (colInfo.collation != null) {
            byte[] bArr = colInfo.collation;
            byte[] collation = jtdsConnection.getCollation();
            int i = 0;
            while (i < 5 && bArr[i] == collation[i]) {
                i++;
            }
            if (i == 5) {
                colInfo.charsetInfo = jtdsConnection.getCharsetInfo();
            } else {
                colInfo.charsetInfo = CharsetInfo.getCharset(bArr);
            }
        }
    }

    static int readType(ResponseStream responseStream, ColInfo colInfo) throws IOException, ProtocolException {
        int i;
        ColInfo colInfo2 = colInfo;
        int tdsVersion = responseStream.getTdsVersion();
        boolean z = tdsVersion >= 4;
        boolean z2 = tdsVersion >= 3;
        boolean z3 = tdsVersion == 2;
        boolean z4 = tdsVersion == 1;
        int read = responseStream.read();
        if (types[read] == null || (z3 && read == 36)) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid TDS data type 0x");
            sb.append(Integer.toHexString(read & 255));
            throw new ProtocolException(sb.toString());
        }
        colInfo2.tdsType = read;
        colInfo2.jdbcType = types[read].jdbcType;
        colInfo2.bufferSize = types[read].size;
        if (colInfo2.bufferSize == -5) {
            colInfo2.bufferSize = responseStream.readInt();
            i = 5;
            ResponseStream responseStream2 = responseStream;
        } else if (colInfo2.bufferSize == -4) {
            colInfo2.bufferSize = responseStream.readInt();
            int collation = z ? getCollation(responseStream, colInfo) + 1 : 1;
            int readShort = responseStream.readShort();
            colInfo2.tableName = responseStream.readString(readShort);
            if (responseStream.getTdsVersion() >= 3) {
                readShort *= 2;
            }
            i = collation + readShort + 6;
        } else {
            ResponseStream responseStream3 = responseStream;
            if (colInfo2.bufferSize == -2) {
                if (!z3 || colInfo2.tdsType != XSYBCHAR) {
                    colInfo2.bufferSize = responseStream.readShort();
                    i = 3;
                } else {
                    colInfo2.bufferSize = responseStream.readInt();
                    i = 5;
                }
                if (z) {
                    i += getCollation(responseStream, colInfo);
                }
            } else if (colInfo2.bufferSize == -1) {
                colInfo2.bufferSize = responseStream.read();
                i = 2;
            } else {
                i = 1;
            }
        }
        colInfo2.displaySize = types[read].displaySize;
        colInfo2.precision = types[read].precision;
        colInfo2.sqlType = types[read].sqlType;
        switch (read) {
            case 34:
                colInfo2.precision = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                colInfo2.displaySize = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                break;
            case 35:
            case 39:
            case 47:
            case 103:
            case XSYBVARCHAR /*167*/:
            case XSYBCHAR /*175*/:
                colInfo2.precision = colInfo2.bufferSize;
                colInfo2.displaySize = colInfo2.precision;
                break;
            case 37:
            case 45:
            case XSYBVARBINARY /*165*/:
            case XSYBBINARY /*173*/:
            case SYBLONGBINARY /*225*/:
                colInfo2.precision = colInfo2.bufferSize;
                colInfo2.displaySize = colInfo2.precision * 2;
                break;
            case 38:
                if (colInfo2.bufferSize != 8) {
                    if (colInfo2.bufferSize != 4) {
                        if (colInfo2.bufferSize != 2) {
                            colInfo2.displaySize = types[48].displaySize;
                            colInfo2.precision = types[48].precision;
                            colInfo2.jdbcType = -6;
                            colInfo2.sqlType = types[48].sqlType;
                            break;
                        } else {
                            colInfo2.displaySize = types[52].displaySize;
                            colInfo2.precision = types[52].precision;
                            colInfo2.jdbcType = 5;
                            colInfo2.sqlType = types[52].sqlType;
                            break;
                        }
                    } else {
                        colInfo2.displaySize = types[56].displaySize;
                        colInfo2.precision = types[56].precision;
                        break;
                    }
                } else {
                    colInfo2.displaySize = types[SYBINT8].displaySize;
                    colInfo2.precision = types[SYBINT8].precision;
                    colInfo2.jdbcType = -5;
                    colInfo2.sqlType = types[SYBINT8].sqlType;
                    break;
                }
            case 60:
            case 122:
                colInfo2.scale = 4;
                break;
            case 61:
                colInfo2.scale = 3;
                break;
            case 68:
                if (colInfo2.bufferSize == 8) {
                    colInfo2.displaySize = types[67].displaySize;
                    colInfo2.precision = types[67].precision;
                    colInfo2.jdbcType = types[67].jdbcType;
                    colInfo2.sqlType = types[67].sqlType;
                    break;
                } else if (colInfo2.bufferSize == 4) {
                    colInfo2.displaySize = types[66].displaySize;
                    colInfo2.precision = types[66].precision;
                    break;
                } else if (colInfo2.bufferSize == 2) {
                    colInfo2.displaySize = types[65].displaySize;
                    colInfo2.precision = types[65].precision;
                    colInfo2.jdbcType = types[65].jdbcType;
                    colInfo2.sqlType = types[65].sqlType;
                    break;
                } else {
                    throw new ProtocolException("unsigned int null (size 1) not supported");
                }
            case 99:
                colInfo2.precision = 1073741823;
                colInfo2.displaySize = 1073741823;
                break;
            case 106:
            case 108:
                colInfo2.precision = responseStream.read();
                colInfo2.scale = responseStream.read();
                colInfo2.displaySize = (colInfo2.scale > 0 ? 2 : 1) + colInfo2.precision;
                i += 2;
                colInfo2.sqlType = types[read].sqlType;
                break;
            case 109:
                if (colInfo2.bufferSize != 8) {
                    colInfo2.displaySize = types[59].displaySize;
                    colInfo2.precision = types[59].precision;
                    colInfo2.jdbcType = 7;
                    colInfo2.sqlType = types[59].sqlType;
                    break;
                } else {
                    colInfo2.displaySize = types[62].displaySize;
                    colInfo2.precision = types[62].precision;
                    break;
                }
            case 110:
                if (colInfo2.bufferSize == 8) {
                    colInfo2.displaySize = types[60].displaySize;
                    colInfo2.precision = types[60].precision;
                } else {
                    colInfo2.displaySize = types[122].displaySize;
                    colInfo2.precision = types[122].precision;
                    colInfo2.sqlType = types[122].sqlType;
                }
                colInfo2.scale = 4;
                break;
            case 111:
                if (colInfo2.bufferSize != 8) {
                    colInfo2.displaySize = types[58].displaySize;
                    colInfo2.precision = types[58].precision;
                    colInfo2.sqlType = types[58].sqlType;
                    colInfo2.scale = 0;
                    break;
                } else {
                    colInfo2.displaySize = types[61].displaySize;
                    colInfo2.precision = types[61].precision;
                    colInfo2.scale = 3;
                    break;
                }
            case SYBUNITEXT /*174*/:
                colInfo2.precision = 1073741823;
                colInfo2.displaySize = 1073741823;
                break;
            case XSYBNVARCHAR /*231*/:
            case XSYBNCHAR /*239*/:
                colInfo2.displaySize = colInfo2.bufferSize / 2;
                colInfo2.precision = colInfo2.displaySize;
                break;
        }
        if (colInfo2.isIdentity) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(colInfo2.sqlType);
            sb2.append(" identity");
            colInfo2.sqlType = sb2.toString();
        }
        if (z4 || z3) {
            int i2 = colInfo2.userType;
            if (i2 == 1) {
                colInfo2.sqlType = "char";
                colInfo2.displaySize = colInfo2.bufferSize;
                colInfo2.jdbcType = 1;
            } else if (i2 == 2) {
                colInfo2.sqlType = "varchar";
                colInfo2.displaySize = colInfo2.bufferSize;
                colInfo2.jdbcType = 12;
            } else if (i2 == 3) {
                colInfo2.sqlType = "binary";
                colInfo2.displaySize = colInfo2.bufferSize * 2;
                colInfo2.jdbcType = -2;
            } else if (i2 == 4) {
                colInfo2.sqlType = "varbinary";
                colInfo2.displaySize = colInfo2.bufferSize * 2;
                colInfo2.jdbcType = -3;
            } else if (i2 == 18) {
                colInfo2.sqlType = "sysname";
                colInfo2.displaySize = colInfo2.bufferSize;
                colInfo2.jdbcType = 12;
            } else if (i2 == 80) {
                colInfo2.sqlType = "timestamp";
                colInfo2.displaySize = colInfo2.bufferSize * 2;
                colInfo2.jdbcType = -3;
            }
        }
        if (z3) {
            int i3 = colInfo2.userType;
            if (i3 == 24) {
                colInfo2.sqlType = "nchar";
                colInfo2.displaySize = colInfo2.bufferSize;
                colInfo2.jdbcType = 1;
            } else if (i3 == 25) {
                colInfo2.sqlType = "nvarchar";
                colInfo2.displaySize = colInfo2.bufferSize;
                colInfo2.jdbcType = 12;
            } else if (i3 == 34) {
                colInfo2.sqlType = "unichar";
                colInfo2.displaySize = colInfo2.bufferSize / 2;
                colInfo2.precision = colInfo2.displaySize;
                colInfo2.jdbcType = 1;
            } else if (i3 == 35) {
                colInfo2.sqlType = "univarchar";
                colInfo2.displaySize = colInfo2.bufferSize / 2;
                colInfo2.precision = colInfo2.displaySize;
                colInfo2.jdbcType = 12;
            } else if (i3 == 42) {
                colInfo2.sqlType = "longsysname";
                colInfo2.jdbcType = 12;
                colInfo2.displaySize = colInfo2.bufferSize;
            }
        }
        if (z2) {
            int i4 = colInfo2.userType;
            if (i4 == 80) {
                colInfo2.sqlType = "timestamp";
                colInfo2.jdbcType = -2;
            } else if (i4 == 256) {
                colInfo2.sqlType = "sysname";
                colInfo2.jdbcType = 12;
            }
        }
        return i;
    }

    /* JADX INFO: used method not loaded: net.sourceforge.jtds.jdbc.ResponseStream.read(byte[]):null, types can be incorrect */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x01a8, code lost:
        if (r2.charsetInfo != null) goto L_0x01af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x01aa, code lost:
        r0 = r18.getCharsetInfo();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x01af, code lost:
        r0 = r2.charsetInfo;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x01b5, code lost:
        return r1.readNonUnicodeString(r3, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x01b6, code lost:
        r0 = r19.readShort();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x01ba, code lost:
        if (r0 == -1) goto L_0x047f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:107:0x01bc, code lost:
        r0 = new byte[r0];
        r1.read(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:0x01c1, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x01c6, code lost:
        if (r2.tdsType != SYBTIMEN) goto L_0x01cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x01c8, code lost:
        r0 = r19.read();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x01cd, code lost:
        r0 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:113:0x01ce, code lost:
        if (r0 != 4) goto L_0x01da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x01d9, code lost:
        return new net.sourceforge.jtds.jdbc.DateTime(Integer.MIN_VALUE, r19.readInt());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x01da, code lost:
        r1.skip(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x01e9, code lost:
        r0 = r19.read();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x01ed, code lost:
        if (r0 <= 0) goto L_0x047f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x01ef, code lost:
        r3 = r19.read();
        r0 = r0 - 1;
        r4 = new byte[r0];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x01fa, code lost:
        if (r19.getServerType() != 2) goto L_0x0211;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x01fc, code lost:
        if (r12 >= r0) goto L_0x0208;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x01fe, code lost:
        r4[r12] = (byte) r19.read();
        r12 = r12 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x020a, code lost:
        if (r3 != 0) goto L_0x020d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:127:0x020c, code lost:
        r10 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x020d, code lost:
        r0 = new java.math.BigInteger(r10, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x0211, code lost:
        r5 = r0 - 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x0213, code lost:
        if (r0 <= 0) goto L_0x021e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x0215, code lost:
        r4[r5] = (byte) r19.read();
        r0 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x0220, code lost:
        if (r3 != 0) goto L_0x0223;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x0223, code lost:
        r10 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x0224, code lost:
        r0 = new java.math.BigInteger(r10, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x022e, code lost:
        return new java.math.BigDecimal(r0, r2.scale);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x0239, code lost:
        r3 = r19.read();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x023d, code lost:
        if (r3 <= 0) goto L_0x047f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x0241, code lost:
        if (r2.charsetInfo != null) goto L_0x0248;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0243, code lost:
        r0 = r18.getCharsetInfo();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x0248, code lost:
        r0 = r2.charsetInfo;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x024a, code lost:
        r0 = r1.readNonUnicodeString(r3, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x024e, code lost:
        if (r3 != 1) goto L_0x0265;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x0254, code lost:
        if (r2.tdsType != 39) goto L_0x0265;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x025a, code lost:
        if (r19.getTdsVersion() >= 3) goto L_0x0265;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x0260, code lost:
        if (r6.equals(r0) == false) goto L_0x0263;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x0263, code lost:
        r7 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x0264, code lost:
        return r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0265, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x029e, code lost:
        r0 = r19.read();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x02a2, code lost:
        if (r0 <= 0) goto L_0x047f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x02a4, code lost:
        r0 = new byte[r0];
        r1.read(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:173:0x02a9, code lost:
        return r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0108, code lost:
        return getDatetimeValue(r1, r2.tdsType);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x0181, code lost:
        if (r19.getTdsVersion() != 2) goto L_0x01a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0183, code lost:
        r0 = r19.readInt();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x0187, code lost:
        if (r0 <= 0) goto L_0x047f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x0189, code lost:
        r0 = r1.readNonUnicodeString(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x0191, code lost:
        if (r6.equals(r0) == false) goto L_0x019e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x019b, code lost:
        if ("char".equals(r2.sqlType) != false) goto L_0x019e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x019e, code lost:
        r7 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x019f, code lost:
        return r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x01a0, code lost:
        r3 = r19.readShort();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x01a4, code lost:
        if (r3 == -1) goto L_0x047f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.lang.Object readData(net.sourceforge.jtds.jdbc.JtdsConnection r18, net.sourceforge.jtds.jdbc.ResponseStream r19, net.sourceforge.jtds.jdbc.ColInfo r20) throws java.io.IOException, net.sourceforge.jtds.jdbc.ProtocolException {
        /*
            r0 = r18
            r1 = r19
            r2 = r20
            int r3 = r2.tdsType
            r4 = 98
            if (r3 == r4) goto L_0x0481
            r4 = 99
            r5 = 32
            r8 = 24
            r10 = -1
            r11 = 1
            r12 = 0
            r13 = 2
            if (r3 == r4) goto L_0x040e
            r4 = 103(0x67, float:1.44E-43)
            if (r3 == r4) goto L_0x0402
            r4 = 104(0x68, float:1.46E-43)
            if (r3 == r4) goto L_0x03f0
            r4 = 122(0x7a, float:1.71E-43)
            if (r3 == r4) goto L_0x03e9
            r4 = -2147483648(0xffffffff80000000, float:-0.0)
            r14 = 123(0x7b, float:1.72E-43)
            r15 = 4
            if (r3 == r14) goto L_0x03cc
            r14 = 3
            java.lang.String r6 = " "
            java.lang.String r7 = ""
            r9 = 8
            switch(r3) {
                case 34: goto L_0x036f;
                case 35: goto L_0x02bb;
                case 36: goto L_0x02aa;
                case 37: goto L_0x029e;
                case 38: goto L_0x0266;
                case 39: goto L_0x0239;
                default: goto L_0x0035;
            }
        L_0x0035:
            switch(r3) {
                case 45: goto L_0x029e;
                case 56: goto L_0x022f;
                case 106: goto L_0x01e9;
                case 127: goto L_0x01df;
                case 147: goto L_0x01c2;
                case 165: goto L_0x01b6;
                case 167: goto L_0x017d;
                case 191: goto L_0x0173;
                case 225: goto L_0x0137;
                case 231: goto L_0x012b;
                case 239: goto L_0x012b;
                default: goto L_0x0038;
            }
        L_0x0038:
            switch(r3) {
                case 47: goto L_0x0239;
                case 48: goto L_0x011f;
                case 49: goto L_0x03cc;
                case 50: goto L_0x0113;
                case 51: goto L_0x01c2;
                case 52: goto L_0x0109;
                default: goto L_0x003b;
            }
        L_0x003b:
            switch(r3) {
                case 58: goto L_0x0102;
                case 59: goto L_0x00f4;
                case 60: goto L_0x03e9;
                case 61: goto L_0x0102;
                case 62: goto L_0x00e6;
                default: goto L_0x003e;
            }
        L_0x003e:
            r16 = 4294967295(0xffffffff, double:2.1219957905E-314)
            r4 = 65535(0xffff, float:9.1834E-41)
            switch(r3) {
                case 65: goto L_0x00db;
                case 66: goto L_0x00ce;
                case 67: goto L_0x00c9;
                case 68: goto L_0x0092;
                default: goto L_0x0049;
            }
        L_0x0049:
            switch(r3) {
                case 108: goto L_0x01e9;
                case 109: goto L_0x006e;
                case 110: goto L_0x03e9;
                case 111: goto L_0x0102;
                default: goto L_0x004c;
            }
        L_0x004c:
            switch(r3) {
                case 173: goto L_0x01b6;
                case 174: goto L_0x040e;
                case 175: goto L_0x017d;
                default: goto L_0x004f;
            }
        L_0x004f:
            net.sourceforge.jtds.jdbc.ProtocolException r0 = new net.sourceforge.jtds.jdbc.ProtocolException
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Unsupported TDS data type 0x"
            r1.append(r3)
            int r2 = r2.tdsType
            r2 = r2 & 255(0xff, float:3.57E-43)
            java.lang.String r2 = java.lang.Integer.toHexString(r2)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.<init>(r1)
            throw r0
        L_0x006e:
            int r0 = r19.read()
            if (r0 != r15) goto L_0x0082
            java.lang.Float r0 = new java.lang.Float
            int r1 = r19.readInt()
            float r1 = java.lang.Float.intBitsToFloat(r1)
            r0.<init>(r1)
            return r0
        L_0x0082:
            if (r0 != r9) goto L_0x047f
            java.lang.Double r0 = new java.lang.Double
            long r1 = r19.readLong()
            double r1 = java.lang.Double.longBitsToDouble(r1)
            r0.<init>(r1)
            return r0
        L_0x0092:
            int r0 = r19.read()
            if (r0 == r11) goto L_0x00bd
            if (r0 == r13) goto L_0x00b2
            if (r0 == r15) goto L_0x00a5
            if (r0 == r9) goto L_0x00a0
            goto L_0x047f
        L_0x00a0:
            java.math.BigDecimal r0 = r19.readUnsignedLong()
            return r0
        L_0x00a5:
            java.lang.Long r0 = new java.lang.Long
            int r1 = r19.readInt()
            long r1 = (long) r1
            long r1 = r1 & r16
            r0.<init>(r1)
            return r0
        L_0x00b2:
            java.lang.Integer r0 = new java.lang.Integer
            short r1 = r19.readShort()
            r1 = r1 & r4
            r0.<init>(r1)
            return r0
        L_0x00bd:
            java.lang.Integer r0 = new java.lang.Integer
            int r1 = r19.read()
            r1 = r1 & 255(0xff, float:3.57E-43)
            r0.<init>(r1)
            return r0
        L_0x00c9:
            java.math.BigDecimal r0 = r19.readUnsignedLong()
            return r0
        L_0x00ce:
            java.lang.Long r0 = new java.lang.Long
            int r1 = r19.readInt()
            long r1 = (long) r1
            long r1 = r1 & r16
            r0.<init>(r1)
            return r0
        L_0x00db:
            java.lang.Integer r0 = new java.lang.Integer
            short r1 = r19.readShort()
            r1 = r1 & r4
            r0.<init>(r1)
            return r0
        L_0x00e6:
            java.lang.Double r0 = new java.lang.Double
            long r1 = r19.readLong()
            double r1 = java.lang.Double.longBitsToDouble(r1)
            r0.<init>(r1)
            return r0
        L_0x00f4:
            java.lang.Float r0 = new java.lang.Float
            int r1 = r19.readInt()
            float r1 = java.lang.Float.intBitsToFloat(r1)
            r0.<init>(r1)
            return r0
        L_0x0102:
            int r0 = r2.tdsType
            java.lang.Object r0 = getDatetimeValue(r1, r0)
            return r0
        L_0x0109:
            java.lang.Integer r0 = new java.lang.Integer
            short r1 = r19.readShort()
            r0.<init>(r1)
            return r0
        L_0x0113:
            int r0 = r19.read()
            if (r0 == 0) goto L_0x011c
            java.lang.Boolean r0 = java.lang.Boolean.TRUE
            goto L_0x011e
        L_0x011c:
            java.lang.Boolean r0 = java.lang.Boolean.FALSE
        L_0x011e:
            return r0
        L_0x011f:
            java.lang.Integer r0 = new java.lang.Integer
            int r1 = r19.read()
            r1 = r1 & 255(0xff, float:3.57E-43)
            r0.<init>(r1)
            return r0
        L_0x012b:
            short r0 = r19.readShort()
            if (r0 == r10) goto L_0x047f
            int r0 = r0 / r13
            java.lang.String r0 = r1.readUnicodeString(r0)
            return r0
        L_0x0137:
            int r0 = r19.readInt()
            if (r0 == 0) goto L_0x047f
            java.lang.String r3 = r2.sqlType
            java.lang.String r4 = "unichar"
            boolean r3 = r4.equals(r3)
            if (r3 != 0) goto L_0x0158
            java.lang.String r2 = r2.sqlType
            java.lang.String r3 = "univarchar"
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x0152
            goto L_0x0158
        L_0x0152:
            byte[] r0 = new byte[r0]
            r1.read(r0)
            return r0
        L_0x0158:
            int r2 = r0 / 2
            char[] r2 = new char[r2]
            r1.read(r2)
            r3 = r0 & 1
            if (r3 == 0) goto L_0x0166
            r1.skip(r11)
        L_0x0166:
            if (r0 != r13) goto L_0x016d
            char r0 = r2[r12]
            if (r0 != r5) goto L_0x016d
            return r7
        L_0x016d:
            java.lang.String r0 = new java.lang.String
            r0.<init>(r2)
            return r0
        L_0x0173:
            java.lang.Long r0 = new java.lang.Long
            long r1 = r19.readLong()
            r0.<init>(r1)
            return r0
        L_0x017d:
            int r3 = r19.getTdsVersion()
            if (r3 != r13) goto L_0x01a0
            int r0 = r19.readInt()
            if (r0 <= 0) goto L_0x047f
            java.lang.String r0 = r1.readNonUnicodeString(r0)
            boolean r1 = r6.equals(r0)
            if (r1 == 0) goto L_0x019e
            java.lang.String r1 = r2.sqlType
            java.lang.String r2 = "char"
            boolean r1 = r2.equals(r1)
            if (r1 != 0) goto L_0x019e
            goto L_0x019f
        L_0x019e:
            r7 = r0
        L_0x019f:
            return r7
        L_0x01a0:
            short r3 = r19.readShort()
            if (r3 == r10) goto L_0x047f
            net.sourceforge.jtds.jdbc.CharsetInfo r4 = r2.charsetInfo
            if (r4 != 0) goto L_0x01af
            net.sourceforge.jtds.jdbc.CharsetInfo r0 = r18.getCharsetInfo()
            goto L_0x01b1
        L_0x01af:
            net.sourceforge.jtds.jdbc.CharsetInfo r0 = r2.charsetInfo
        L_0x01b1:
            java.lang.String r0 = r1.readNonUnicodeString(r3, r0)
            return r0
        L_0x01b6:
            short r0 = r19.readShort()
            if (r0 == r10) goto L_0x047f
            byte[] r0 = new byte[r0]
            r1.read(r0)
            return r0
        L_0x01c2:
            int r0 = r2.tdsType
            r2 = 147(0x93, float:2.06E-43)
            if (r0 != r2) goto L_0x01cd
            int r0 = r19.read()
            goto L_0x01ce
        L_0x01cd:
            r0 = 4
        L_0x01ce:
            if (r0 != r15) goto L_0x01da
            net.sourceforge.jtds.jdbc.DateTime r0 = new net.sourceforge.jtds.jdbc.DateTime
            int r1 = r19.readInt()
            r0.<init>(r4, r1)
            return r0
        L_0x01da:
            r1.skip(r0)
            goto L_0x047f
        L_0x01df:
            java.lang.Long r0 = new java.lang.Long
            long r1 = r19.readLong()
            r0.<init>(r1)
            return r0
        L_0x01e9:
            int r0 = r19.read()
            if (r0 <= 0) goto L_0x047f
            int r3 = r19.read()
            int r0 = r0 + r10
            byte[] r4 = new byte[r0]
            int r5 = r19.getServerType()
            if (r5 != r13) goto L_0x0211
        L_0x01fc:
            if (r12 >= r0) goto L_0x0208
            int r5 = r19.read()
            byte r5 = (byte) r5
            r4[r12] = r5
            int r12 = r12 + 1
            goto L_0x01fc
        L_0x0208:
            java.math.BigInteger r0 = new java.math.BigInteger
            if (r3 != 0) goto L_0x020d
            r10 = 1
        L_0x020d:
            r0.<init>(r10, r4)
            goto L_0x0227
        L_0x0211:
            int r5 = r0 + -1
            if (r0 <= 0) goto L_0x021e
            int r0 = r19.read()
            byte r0 = (byte) r0
            r4[r5] = r0
            r0 = r5
            goto L_0x0211
        L_0x021e:
            java.math.BigInteger r0 = new java.math.BigInteger
            if (r3 != 0) goto L_0x0223
            goto L_0x0224
        L_0x0223:
            r10 = 1
        L_0x0224:
            r0.<init>(r10, r4)
        L_0x0227:
            java.math.BigDecimal r1 = new java.math.BigDecimal
            int r2 = r2.scale
            r1.<init>(r0, r2)
            return r1
        L_0x022f:
            java.lang.Integer r0 = new java.lang.Integer
            int r1 = r19.readInt()
            r0.<init>(r1)
            return r0
        L_0x0239:
            int r3 = r19.read()
            if (r3 <= 0) goto L_0x047f
            net.sourceforge.jtds.jdbc.CharsetInfo r4 = r2.charsetInfo
            if (r4 != 0) goto L_0x0248
            net.sourceforge.jtds.jdbc.CharsetInfo r0 = r18.getCharsetInfo()
            goto L_0x024a
        L_0x0248:
            net.sourceforge.jtds.jdbc.CharsetInfo r0 = r2.charsetInfo
        L_0x024a:
            java.lang.String r0 = r1.readNonUnicodeString(r3, r0)
            if (r3 != r11) goto L_0x0265
            int r2 = r2.tdsType
            r3 = 39
            if (r2 != r3) goto L_0x0265
            int r1 = r19.getTdsVersion()
            if (r1 >= r14) goto L_0x0265
            boolean r1 = r6.equals(r0)
            if (r1 == 0) goto L_0x0263
            goto L_0x0264
        L_0x0263:
            r7 = r0
        L_0x0264:
            return r7
        L_0x0265:
            return r0
        L_0x0266:
            int r0 = r19.read()
            if (r0 == r11) goto L_0x0292
            if (r0 == r13) goto L_0x0288
            if (r0 == r15) goto L_0x027e
            if (r0 == r9) goto L_0x0274
            goto L_0x047f
        L_0x0274:
            java.lang.Long r0 = new java.lang.Long
            long r1 = r19.readLong()
            r0.<init>(r1)
            return r0
        L_0x027e:
            java.lang.Integer r0 = new java.lang.Integer
            int r1 = r19.readInt()
            r0.<init>(r1)
            return r0
        L_0x0288:
            java.lang.Integer r0 = new java.lang.Integer
            short r1 = r19.readShort()
            r0.<init>(r1)
            return r0
        L_0x0292:
            java.lang.Integer r0 = new java.lang.Integer
            int r1 = r19.read()
            r1 = r1 & 255(0xff, float:3.57E-43)
            r0.<init>(r1)
            return r0
        L_0x029e:
            int r0 = r19.read()
            if (r0 <= 0) goto L_0x047f
            byte[] r0 = new byte[r0]
            r1.read(r0)
            return r0
        L_0x02aa:
            int r0 = r19.read()
            if (r0 <= 0) goto L_0x047f
            byte[] r0 = new byte[r0]
            r1.read(r0)
            net.sourceforge.jtds.jdbc.UniqueIdentifier r1 = new net.sourceforge.jtds.jdbc.UniqueIdentifier
            r1.<init>(r0)
            return r1
        L_0x02bb:
            int r3 = r19.read()
            if (r3 <= 0) goto L_0x047f
            net.sourceforge.jtds.jdbc.CharsetInfo r3 = r2.charsetInfo
            if (r3 == 0) goto L_0x02cc
            net.sourceforge.jtds.jdbc.CharsetInfo r2 = r2.charsetInfo
            java.lang.String r2 = r2.getCharset()
            goto L_0x02d0
        L_0x02cc:
            java.lang.String r2 = r18.getCharset()
        L_0x02d0:
            r1.skip(r8)
            int r3 = r19.readInt()
            if (r3 != 0) goto L_0x02e1
            int r4 = r19.getTdsVersion()
            if (r4 > r13) goto L_0x02e1
            goto L_0x047f
        L_0x02e1:
            net.sourceforge.jtds.jdbc.ClobImpl r4 = new net.sourceforge.jtds.jdbc.ClobImpl
            r4.<init>(r0)
            net.sourceforge.jtds.util.BlobBuffer r6 = r4.getBlobBuffer()
            long r7 = (long) r3
            long r9 = r18.getLobBuffer()
            int r0 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r0 > 0) goto L_0x0338
            java.io.BufferedReader r0 = new java.io.BufferedReader
            java.io.InputStreamReader r7 = new java.io.InputStreamReader
            java.io.InputStream r8 = r1.getInputStream(r3)
            r7.<init>(r8, r2)
            r2 = 1024(0x400, float:1.435E-42)
            r0.<init>(r7, r2)
            int r3 = r3 * 2
            byte[] r2 = new byte[r3]
            r3 = 0
        L_0x0308:
            int r7 = r0.read()
            if (r7 < 0) goto L_0x031b
            int r8 = r3 + 1
            byte r9 = (byte) r7
            r2[r3] = r9
            int r3 = r8 + 1
            int r7 = r7 >> 8
            byte r7 = (byte) r7
            r2[r8] = r7
            goto L_0x0308
        L_0x031b:
            r0.close()
            r6.setBuffer(r2, r12)
            if (r3 != r13) goto L_0x0332
            byte r0 = r2[r12]
            if (r0 != r5) goto L_0x0332
            byte r0 = r2[r11]
            if (r0 != 0) goto L_0x0332
            int r0 = r19.getTdsVersion()
            if (r0 >= r14) goto L_0x0332
            goto L_0x0333
        L_0x0332:
            r12 = r3
        L_0x0333:
            long r0 = (long) r12
            r6.setLength(r0)
            goto L_0x0363
        L_0x0338:
            java.io.BufferedReader r0 = new java.io.BufferedReader
            java.io.InputStreamReader r5 = new java.io.InputStreamReader
            java.io.InputStream r1 = r1.getInputStream(r3)
            r5.<init>(r1, r2)
            r1 = 1024(0x400, float:1.435E-42)
            r0.<init>(r5, r1)
            r1 = 1
            java.io.OutputStream r1 = r6.setBinaryStream(r1, r12)     // Catch:{ SQLException -> 0x0364 }
        L_0x034e:
            int r2 = r0.read()     // Catch:{ SQLException -> 0x0364 }
            if (r2 < 0) goto L_0x035d
            r1.write(r2)     // Catch:{ SQLException -> 0x0364 }
            int r2 = r2 >> 8
            r1.write(r2)     // Catch:{ SQLException -> 0x0364 }
            goto L_0x034e
        L_0x035d:
            r1.close()     // Catch:{ SQLException -> 0x0364 }
            r0.close()     // Catch:{ SQLException -> 0x0364 }
        L_0x0363:
            return r4
        L_0x0364:
            r0 = move-exception
            java.io.IOException r1 = new java.io.IOException
            java.lang.String r0 = r0.getMessage()
            r1.<init>(r0)
            throw r1
        L_0x036f:
            int r2 = r19.read()
            if (r2 <= 0) goto L_0x047f
            r1.skip(r8)
            int r2 = r19.readInt()
            if (r2 != 0) goto L_0x0386
            int r3 = r19.getTdsVersion()
            if (r3 > r13) goto L_0x0386
            goto L_0x047f
        L_0x0386:
            long r3 = (long) r2
            long r5 = r18.getLobBuffer()
            int r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r7 > 0) goto L_0x039a
            byte[] r2 = new byte[r2]
            r1.read(r2)
            net.sourceforge.jtds.jdbc.BlobImpl r1 = new net.sourceforge.jtds.jdbc.BlobImpl
            r1.<init>(r0, r2)
            goto L_0x03c0
        L_0x039a:
            net.sourceforge.jtds.jdbc.BlobImpl r3 = new net.sourceforge.jtds.jdbc.BlobImpl     // Catch:{ SQLException -> 0x03c1 }
            r3.<init>(r0)     // Catch:{ SQLException -> 0x03c1 }
            r4 = 1
            java.io.OutputStream r0 = r3.setBinaryStream(r4)     // Catch:{ SQLException -> 0x03c1 }
            r4 = 1024(0x400, float:1.435E-42)
            byte[] r5 = new byte[r4]     // Catch:{ SQLException -> 0x03c1 }
        L_0x03a9:
            int r6 = java.lang.Math.min(r2, r4)     // Catch:{ SQLException -> 0x03c1 }
            int r4 = r1.read(r5, r12, r6)     // Catch:{ SQLException -> 0x03c1 }
            if (r4 == r10) goto L_0x03bc
            if (r2 == 0) goto L_0x03bc
            r0.write(r5, r12, r4)     // Catch:{ SQLException -> 0x03c1 }
            int r2 = r2 - r4
            r4 = 1024(0x400, float:1.435E-42)
            goto L_0x03a9
        L_0x03bc:
            r0.close()     // Catch:{ SQLException -> 0x03c1 }
            r1 = r3
        L_0x03c0:
            return r1
        L_0x03c1:
            r0 = move-exception
            java.io.IOException r1 = new java.io.IOException
            java.lang.String r0 = r0.getMessage()
            r1.<init>(r0)
            throw r1
        L_0x03cc:
            int r0 = r2.tdsType
            r2 = 123(0x7b, float:1.72E-43)
            if (r0 != r2) goto L_0x03d7
            int r0 = r19.read()
            goto L_0x03d8
        L_0x03d7:
            r0 = 4
        L_0x03d8:
            if (r0 != r15) goto L_0x03e4
            net.sourceforge.jtds.jdbc.DateTime r0 = new net.sourceforge.jtds.jdbc.DateTime
            int r1 = r19.readInt()
            r0.<init>(r1, r4)
            return r0
        L_0x03e4:
            r1.skip(r0)
            goto L_0x047f
        L_0x03e9:
            int r0 = r2.tdsType
            java.lang.Object r0 = getMoneyValue(r1, r0)
            return r0
        L_0x03f0:
            int r0 = r19.read()
            if (r0 <= 0) goto L_0x047f
            int r0 = r19.read()
            if (r0 == 0) goto L_0x03ff
            java.lang.Boolean r0 = java.lang.Boolean.TRUE
            goto L_0x0401
        L_0x03ff:
            java.lang.Boolean r0 = java.lang.Boolean.FALSE
        L_0x0401:
            return r0
        L_0x0402:
            int r0 = r19.read()
            if (r0 <= 0) goto L_0x047f
            int r0 = r0 / r13
            java.lang.String r0 = r1.readUnicodeString(r0)
            return r0
        L_0x040e:
            int r2 = r19.read()
            if (r2 <= 0) goto L_0x047f
            r1.skip(r8)
            int r2 = r19.readInt()
            if (r2 != 0) goto L_0x0424
            int r3 = r19.getTdsVersion()
            if (r3 > r13) goto L_0x0424
            goto L_0x047f
        L_0x0424:
            net.sourceforge.jtds.jdbc.ClobImpl r3 = new net.sourceforge.jtds.jdbc.ClobImpl
            r3.<init>(r0)
            net.sourceforge.jtds.util.BlobBuffer r4 = r3.getBlobBuffer()
            long r6 = (long) r2
            long r8 = r18.getLobBuffer()
            int r0 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r0 > 0) goto L_0x0455
            byte[] r0 = new byte[r2]
            r1.read(r0)
            r4.setBuffer(r0, r12)
            if (r2 != r13) goto L_0x044f
            byte r6 = r0[r12]
            if (r6 != r5) goto L_0x044f
            byte r0 = r0[r11]
            if (r0 != 0) goto L_0x044f
            int r0 = r19.getTdsVersion()
            if (r0 != r13) goto L_0x044f
            goto L_0x0450
        L_0x044f:
            r12 = r2
        L_0x0450:
            long r0 = (long) r12
            r4.setLength(r0)
            goto L_0x0473
        L_0x0455:
            r5 = 1
            java.io.OutputStream r0 = r4.setBinaryStream(r5, r12)     // Catch:{ SQLException -> 0x0474 }
            r4 = 1024(0x400, float:1.435E-42)
            byte[] r5 = new byte[r4]     // Catch:{ SQLException -> 0x0474 }
        L_0x045f:
            int r6 = java.lang.Math.min(r2, r4)     // Catch:{ SQLException -> 0x0474 }
            int r6 = r1.read(r5, r12, r6)     // Catch:{ SQLException -> 0x0474 }
            if (r6 == r10) goto L_0x0470
            if (r2 == 0) goto L_0x0470
            r0.write(r5, r12, r6)     // Catch:{ SQLException -> 0x0474 }
            int r2 = r2 - r6
            goto L_0x045f
        L_0x0470:
            r0.close()     // Catch:{ SQLException -> 0x0474 }
        L_0x0473:
            return r3
        L_0x0474:
            r0 = move-exception
            java.io.IOException r1 = new java.io.IOException
            java.lang.String r0 = r0.getMessage()
            r1.<init>(r0)
            throw r1
        L_0x047f:
            r0 = 0
            return r0
        L_0x0481:
            java.lang.Object r0 = getVariant(r18, r19)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: net.sourceforge.jtds.jdbc.TdsData.readData(net.sourceforge.jtds.jdbc.JtdsConnection, net.sourceforge.jtds.jdbc.ResponseStream, net.sourceforge.jtds.jdbc.ColInfo):java.lang.Object");
    }

    static boolean isSigned(ColInfo colInfo) {
        int i = colInfo.tdsType;
        if (i < 0 || i > 255 || types[i] == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("TDS data type ");
            sb.append(i);
            sb.append(" invalid");
            throw new IllegalArgumentException(sb.toString());
        }
        if (i == 38 && colInfo.bufferSize == 1) {
            i = 48;
        }
        return types[i].isSigned;
    }

    static boolean isCollation(ColInfo colInfo) {
        int i = colInfo.tdsType;
        if (i >= 0 && i <= 255) {
            TypeInfo[] typeInfoArr = types;
            if (typeInfoArr[i] != null) {
                return typeInfoArr[i].isCollation;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("TDS data type ");
        sb.append(i);
        sb.append(" invalid");
        throw new IllegalArgumentException(sb.toString());
    }

    static boolean isCurrency(ColInfo colInfo) {
        int i = colInfo.tdsType;
        if (i >= 0 && i <= 255 && types[i] != null) {
            return i == 60 || i == 122 || i == 110;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("TDS data type ");
        sb.append(i);
        sb.append(" invalid");
        throw new IllegalArgumentException(sb.toString());
    }

    static boolean isSearchable(ColInfo colInfo) {
        int i = colInfo.tdsType;
        if (i >= 0 && i <= 255) {
            TypeInfo[] typeInfoArr = types;
            if (typeInfoArr[i] != null) {
                return typeInfoArr[i].size != -4;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("TDS data type ");
        sb.append(i);
        sb.append(" invalid");
        throw new IllegalArgumentException(sb.toString());
    }

    static boolean isUnicode(ColInfo colInfo) {
        int i = colInfo.tdsType;
        if (i >= 0 && i <= 255 && types[i] != null) {
            return i == 98 || i == 99 || i == 103 || i == XSYBCHAR || i == XSYBNVARCHAR || i == XSYBNCHAR;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("TDS data type ");
        sb.append(i);
        sb.append(" invalid");
        throw new IllegalArgumentException(sb.toString());
    }

    static void fillInType(ColInfo colInfo) throws SQLException {
        int i = colInfo.jdbcType;
        if (i == -7) {
            colInfo.tdsType = 50;
            colInfo.bufferSize = 1;
            colInfo.displaySize = 1;
            colInfo.precision = 1;
        } else if (i == 12) {
            colInfo.tdsType = 39;
            colInfo.bufferSize = MS_LONGVAR_MAX;
            colInfo.displaySize = MS_LONGVAR_MAX;
            colInfo.precision = MS_LONGVAR_MAX;
        } else if (i == 4) {
            colInfo.tdsType = 56;
            colInfo.bufferSize = 4;
            colInfo.displaySize = 11;
            colInfo.precision = 10;
        } else if (i == 5) {
            colInfo.tdsType = 52;
            colInfo.bufferSize = 2;
            colInfo.displaySize = 6;
            colInfo.precision = 5;
        } else {
            throw new SQLException(Messages.get("error.baddatatype", (Object) Integer.toString(colInfo.jdbcType)), "HY000");
        }
        colInfo.sqlType = types[colInfo.tdsType].sqlType;
        colInfo.scale = 0;
    }

    static void getNativeType(JtdsConnection jtdsConnection, ParamInfo paramInfo) throws SQLException {
        int i;
        JtdsConnection jtdsConnection2 = jtdsConnection;
        ParamInfo paramInfo2 = paramInfo;
        int i2 = paramInfo2.jdbcType;
        if (i2 == 1111) {
            i2 = Support.getJdbcType(paramInfo2.value);
        }
        String str = "error.textoutparam";
        int i3 = 0;
        String str2 = "HY000";
        if (i2 != 12) {
            if (i2 != 16) {
                if (i2 != 1111) {
                    String str3 = "image";
                    if (i2 != 2009) {
                        if (i2 != 2004) {
                            if (i2 != 2005) {
                                switch (i2) {
                                    case -7:
                                        break;
                                    case -6:
                                    case 4:
                                    case 5:
                                        paramInfo2.tdsType = 38;
                                        paramInfo2.sqlType = "int";
                                        return;
                                    case -5:
                                        if (jtdsConnection.getTdsVersion() >= 4 || jtdsConnection2.getSybaseInfo(64)) {
                                            paramInfo2.tdsType = 38;
                                            paramInfo2.sqlType = "bigint";
                                            return;
                                        }
                                        paramInfo2.tdsType = 106;
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("decimal(");
                                        sb.append(jtdsConnection.getMaxPrecision());
                                        sb.append(')');
                                        paramInfo2.sqlType = sb.toString();
                                        paramInfo2.scale = 0;
                                        return;
                                    case FontRequestCallback.FAIL_REASON_SECURITY_VIOLATION /*-4*/:
                                    case FontRequestCallback.FAIL_REASON_FONT_LOAD_ERROR /*-3*/:
                                    case -2:
                                        break;
                                    case -1:
                                    case 1:
                                        break;
                                    case 0:
                                        break;
                                    case 2:
                                    case 3:
                                        paramInfo2.tdsType = 106;
                                        int maxPrecision = jtdsConnection.getMaxPrecision();
                                        int i4 = 10;
                                        if (paramInfo2.value instanceof BigDecimal) {
                                            i4 = ((BigDecimal) paramInfo2.value).scale();
                                        } else if (paramInfo2.scale >= 0 && paramInfo2.scale <= maxPrecision) {
                                            i4 = paramInfo2.scale;
                                        }
                                        StringBuilder sb2 = new StringBuilder();
                                        sb2.append("decimal(");
                                        sb2.append(maxPrecision);
                                        sb2.append(',');
                                        sb2.append(i4);
                                        sb2.append(')');
                                        paramInfo2.sqlType = sb2.toString();
                                        return;
                                    case 6:
                                    case 8:
                                        paramInfo2.tdsType = 109;
                                        paramInfo2.sqlType = "float";
                                        return;
                                    case 7:
                                        paramInfo2.tdsType = 109;
                                        paramInfo2.sqlType = "real";
                                        return;
                                    default:
                                        String str4 = "datetime";
                                        switch (i2) {
                                            case 91:
                                                if (jtdsConnection2.getSybaseInfo(2)) {
                                                    paramInfo2.tdsType = 123;
                                                    paramInfo2.sqlType = "date";
                                                    return;
                                                }
                                                paramInfo2.tdsType = 111;
                                                paramInfo2.sqlType = str4;
                                                return;
                                            case 92:
                                                if (jtdsConnection2.getSybaseInfo(2)) {
                                                    paramInfo2.tdsType = SYBTIMEN;
                                                    paramInfo2.sqlType = "time";
                                                    return;
                                                }
                                                paramInfo2.tdsType = 111;
                                                paramInfo2.sqlType = str4;
                                                return;
                                            case 93:
                                                paramInfo2.tdsType = 111;
                                                paramInfo2.sqlType = str4;
                                                return;
                                            default:
                                                throw new SQLException(Messages.get("error.baddatatype", (Object) Integer.toString(paramInfo2.jdbcType)), str2);
                                        }
                                }
                            }
                        }
                        if (paramInfo2.value != null) {
                            i3 = paramInfo2.length;
                        }
                        if (jtdsConnection.getTdsVersion() < 3) {
                            if (i3 <= 255) {
                                paramInfo2.tdsType = 37;
                                paramInfo2.sqlType = "varbinary(255)";
                                return;
                            } else if (!jtdsConnection2.getSybaseInfo(1)) {
                                paramInfo2.tdsType = 34;
                                paramInfo2.sqlType = str3;
                                return;
                            } else if (i3 > 16384) {
                                paramInfo2.tdsType = 36;
                                paramInfo2.sqlType = str3;
                                return;
                            } else {
                                paramInfo2.tdsType = SYBLONGBINARY;
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append("varbinary(");
                                sb3.append(i3);
                                sb3.append(")");
                                paramInfo2.sqlType = sb3.toString();
                                return;
                            }
                        } else if (i3 <= MS_LONGVAR_MAX) {
                            paramInfo2.tdsType = XSYBVARBINARY;
                            paramInfo2.sqlType = "varbinary(8000)";
                            return;
                        } else if (!paramInfo2.isOutput) {
                            paramInfo2.tdsType = 34;
                            if (isMSSQL2005Plus(jtdsConnection)) {
                                str3 = "varbinary(max)";
                            }
                            paramInfo2.sqlType = str3;
                            return;
                        } else {
                            throw new SQLException(Messages.get(str), str2);
                        }
                    } else {
                        if (paramInfo2.value != null) {
                            i3 = paramInfo2.length;
                        }
                        if (jtdsConnection.getTdsVersion() >= 4) {
                            paramInfo2.tdsType = XML;
                            paramInfo2.sqlType = "xml";
                            return;
                        } else if (jtdsConnection.getTdsVersion() < 3) {
                            if (i3 <= 255) {
                                paramInfo2.tdsType = 37;
                                paramInfo2.sqlType = "varbinary(255)";
                                return;
                            } else if (!jtdsConnection2.getSybaseInfo(1)) {
                                paramInfo2.tdsType = 34;
                                paramInfo2.sqlType = str3;
                                return;
                            } else if (i3 > 16384) {
                                paramInfo2.tdsType = 36;
                                paramInfo2.sqlType = str3;
                                return;
                            } else {
                                paramInfo2.tdsType = SYBLONGBINARY;
                                StringBuilder sb4 = new StringBuilder();
                                sb4.append("varbinary(");
                                sb4.append(i3);
                                sb4.append(')');
                                paramInfo2.sqlType = sb4.toString();
                                return;
                            }
                        } else if (i3 <= MS_LONGVAR_MAX) {
                            paramInfo2.tdsType = XSYBVARBINARY;
                            paramInfo2.sqlType = "varbinary(8000)";
                            return;
                        } else if (!paramInfo2.isOutput) {
                            paramInfo2.tdsType = 34;
                            if (isMSSQL2005Plus(jtdsConnection)) {
                                str3 = "varbinary(max)";
                            }
                            paramInfo2.sqlType = str3;
                            return;
                        } else {
                            throw new SQLException(Messages.get(str), str2);
                        }
                    }
                }
                paramInfo2.tdsType = 39;
                paramInfo2.sqlType = "varchar(255)";
                return;
            }
            if (jtdsConnection.getTdsVersion() >= 3 || jtdsConnection2.getSybaseInfo(4)) {
                paramInfo2.tdsType = 104;
            } else {
                paramInfo2.tdsType = 50;
            }
            paramInfo2.sqlType = "bit";
            return;
        }
        if (paramInfo2.value == null) {
            i = 0;
        } else {
            i = paramInfo2.length;
        }
        String str5 = "error.generic.ioerror";
        String str6 = "text";
        if (jtdsConnection.getTdsVersion() < 3) {
            String charset = jtdsConnection.getCharset();
            if (i > 0 && ((i <= 8192 || jtdsConnection2.getSybaseInfo(32)) && jtdsConnection2.getSybaseInfo(16) && jtdsConnection.getUseUnicode() && !Key.STRING_CHARSET_NAME.equals(charset))) {
                try {
                    String string = paramInfo2.getString(charset);
                    if (!canEncode(string, charset)) {
                        paramInfo2.length = string.length();
                        if (paramInfo2.length > 8192) {
                            paramInfo2.sqlType = "unitext";
                            paramInfo2.tdsType = 36;
                            return;
                        }
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("univarchar(");
                        sb5.append(paramInfo2.length);
                        sb5.append(')');
                        paramInfo2.sqlType = sb5.toString();
                        paramInfo2.tdsType = SYBLONGBINARY;
                        return;
                    }
                } catch (IOException e) {
                    throw new SQLException(Messages.get(str5, (Object) e.getMessage()), str2);
                }
            }
            if (jtdsConnection.isWideChar() && i <= 16384) {
                try {
                    byte[] bytes = paramInfo2.getBytes(charset);
                    if (bytes == null) {
                        i = 0;
                    } else {
                        i = bytes.length;
                    }
                } catch (IOException e2) {
                    throw new SQLException(Messages.get(str5, (Object) e2.getMessage()), str2);
                }
            }
            if (i <= 255) {
                paramInfo2.tdsType = 39;
                paramInfo2.sqlType = "varchar(255)";
            } else if (!jtdsConnection2.getSybaseInfo(1)) {
                paramInfo2.tdsType = 35;
                paramInfo2.sqlType = str6;
            } else if (i > 16384) {
                paramInfo2.tdsType = 36;
                paramInfo2.sqlType = str6;
            } else {
                paramInfo2.tdsType = XSYBCHAR;
                StringBuilder sb6 = new StringBuilder();
                sb6.append("varchar(");
                sb6.append(i);
                sb6.append(')');
                paramInfo2.sqlType = sb6.toString();
            }
        } else if (paramInfo2.isUnicode && i <= 4000) {
            paramInfo2.tdsType = XSYBNVARCHAR;
            paramInfo2.sqlType = "nvarchar(4000)";
        } else if (!paramInfo2.isUnicode && i <= MS_LONGVAR_MAX) {
            CharsetInfo charsetInfo = jtdsConnection.getCharsetInfo();
            if (i > 0) {
                try {
                    if (charsetInfo.isWideChars() && paramInfo2.getBytes(charsetInfo.getCharset()).length > MS_LONGVAR_MAX) {
                        paramInfo2.tdsType = 35;
                        paramInfo2.sqlType = str6;
                        return;
                    }
                } catch (IOException e3) {
                    throw new SQLException(Messages.get(str5, (Object) e3.getMessage()), str2);
                }
            }
            paramInfo2.tdsType = XSYBVARCHAR;
            paramInfo2.sqlType = "varchar(8000)";
        } else if (paramInfo2.isOutput) {
            throw new SQLException(Messages.get(str), str2);
        } else if (paramInfo2.isUnicode) {
            paramInfo2.tdsType = 99;
            paramInfo2.sqlType = "ntext";
        } else {
            paramInfo2.tdsType = 35;
            paramInfo2.sqlType = str6;
        }
    }

    static int getTds5ParamSize(String str, boolean z, ParamInfo paramInfo, boolean z2) {
        int i;
        int i2 = 8;
        if (paramInfo.name != null && z2) {
            if (z) {
                i = Support.encodeString(str, paramInfo.name).length;
            } else {
                i = paramInfo.name.length();
            }
            i2 = 8 + i;
        }
        int i3 = paramInfo.tdsType;
        if (i3 == 50) {
            return i2;
        }
        if (i3 != 106) {
            if (!(i3 == 109 || i3 == 111 || i3 == 123 || i3 == SYBTIMEN)) {
                if (i3 == XSYBCHAR || i3 == SYBLONGBINARY) {
                    return i2 + 4;
                }
                switch (i3) {
                    case 36:
                        break;
                    case 37:
                    case 38:
                    case 39:
                        break;
                    default:
                        StringBuilder sb = new StringBuilder();
                        sb.append("Unsupported output TDS type 0x");
                        sb.append(Integer.toHexString(paramInfo.tdsType));
                        throw new IllegalStateException(sb.toString());
                }
            }
            return i2 + 1;
        }
        return i2 + 3;
    }

    static void writeTds5ParamFmt(RequestStream requestStream, String str, boolean z, ParamInfo paramInfo, boolean z2) throws IOException {
        if (paramInfo.name == null || !z2) {
            requestStream.write(0);
        } else if (z) {
            byte[] encodeString = Support.encodeString(str, paramInfo.name);
            requestStream.write((byte) encodeString.length);
            requestStream.write(encodeString);
        } else {
            requestStream.write((byte) paramInfo.name.length());
            requestStream.write(paramInfo.name);
        }
        requestStream.write(paramInfo.isOutput ? (byte) 1 : 0);
        if (paramInfo.sqlType.startsWith("univarchar")) {
            requestStream.write(35);
        } else {
            if ("unitext".equals(paramInfo.sqlType)) {
                requestStream.write(36);
            } else {
                requestStream.write(0);
            }
        }
        requestStream.write((byte) paramInfo.tdsType);
        int i = paramInfo.tdsType;
        if (i != 50) {
            if (i != 106) {
                byte b = 8;
                byte b2 = 4;
                if (i != 109) {
                    if (i == 111) {
                        requestStream.write(8);
                    } else if (i == 123 || i == SYBTIMEN) {
                        requestStream.write(4);
                    } else if (i == XSYBCHAR) {
                        requestStream.write((int) ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
                    } else if (i != SYBLONGBINARY) {
                        switch (i) {
                            case 36:
                                if ("text".equals(paramInfo.sqlType)) {
                                    b2 = 3;
                                }
                                requestStream.write(b2);
                                requestStream.write(0);
                                requestStream.write(0);
                                break;
                            case 37:
                            case 39:
                                requestStream.write(-1);
                                break;
                            case 38:
                                if (!"bigint".equals(paramInfo.sqlType)) {
                                    b = 4;
                                }
                                requestStream.write(b);
                                break;
                            default:
                                StringBuilder sb = new StringBuilder();
                                sb.append("Unsupported output TDS type ");
                                sb.append(Integer.toHexString(paramInfo.tdsType));
                                throw new IllegalStateException(sb.toString());
                        }
                    } else {
                        requestStream.write((int) ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
                    }
                } else if (paramInfo.value instanceof Float) {
                    requestStream.write(4);
                } else {
                    requestStream.write(8);
                }
            } else {
                requestStream.write((byte) TdsCore.NTLMAUTH_PKT);
                requestStream.write(38);
                if (paramInfo.jdbcType == -5) {
                    requestStream.write(0);
                } else if (paramInfo.value instanceof BigDecimal) {
                    requestStream.write((byte) ((BigDecimal) paramInfo.value).scale());
                } else if (paramInfo.scale < 0 || paramInfo.scale > 38) {
                    requestStream.write(10);
                } else {
                    requestStream.write((byte) paramInfo.scale);
                }
            }
        }
        requestStream.write(0);
    }

    static void writeTds5Param(RequestStream requestStream, CharsetInfo charsetInfo, ParamInfo paramInfo) throws IOException, SQLException {
        if (paramInfo.charsetInfo == null) {
            paramInfo.charsetInfo = charsetInfo;
        }
        int i = paramInfo.tdsType;
        if (i != 50) {
            if (i != 106) {
                if (i == 111) {
                    putDateTimeValue(requestStream, (DateTime) paramInfo.value);
                    return;
                } else if (i != 123) {
                    if (i != SYBTIMEN) {
                        if (i != XSYBCHAR) {
                            if (i != SYBLONGBINARY) {
                                if (i != 108) {
                                    if (i != 109) {
                                        switch (i) {
                                            case 36:
                                                requestStream.write(0);
                                                requestStream.write(0);
                                                requestStream.write(0);
                                                if (paramInfo.value instanceof InputStream) {
                                                    byte[] bArr = new byte[8192];
                                                    int read = ((InputStream) paramInfo.value).read(bArr);
                                                    while (read > 0) {
                                                        requestStream.write((byte) read);
                                                        requestStream.write((byte) (read >> 8));
                                                        requestStream.write((byte) (read >> 16));
                                                        requestStream.write((byte) ((read >> 24) | 128));
                                                        requestStream.write(bArr, 0, read);
                                                        read = ((InputStream) paramInfo.value).read(bArr);
                                                    }
                                                } else if ((paramInfo.value instanceof Reader) && !paramInfo.charsetInfo.isWideChars()) {
                                                    char[] cArr = new char[8192];
                                                    int read2 = ((Reader) paramInfo.value).read(cArr);
                                                    while (read2 > 0) {
                                                        requestStream.write((byte) read2);
                                                        requestStream.write((byte) (read2 >> 8));
                                                        requestStream.write((byte) (read2 >> 16));
                                                        requestStream.write((byte) ((read2 >> 24) | 128));
                                                        requestStream.write(Support.encodeString(paramInfo.charsetInfo.getCharset(), new String(cArr, 0, read2)));
                                                        read2 = ((Reader) paramInfo.value).read(cArr);
                                                    }
                                                } else if (paramInfo.value != null) {
                                                    if ("unitext".equals(paramInfo.sqlType)) {
                                                        String string = paramInfo.getString(paramInfo.charsetInfo.getCharset());
                                                        int i2 = 0;
                                                        while (i2 < string.length()) {
                                                            int i3 = 4096;
                                                            if (string.length() - i2 < 4096) {
                                                                i3 = string.length() - i2;
                                                            }
                                                            int i4 = i3 * 2;
                                                            requestStream.write((byte) i4);
                                                            requestStream.write((byte) (i4 >> 8));
                                                            requestStream.write((byte) (i4 >> 16));
                                                            requestStream.write((byte) ((i4 >> 24) | 128));
                                                            int i5 = i2 + i3;
                                                            requestStream.write(string.substring(i2, i5).toCharArray(), 0, i3);
                                                            i2 = i5;
                                                        }
                                                    } else {
                                                        byte[] bytes = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                                                        int i6 = 0;
                                                        while (i6 < bytes.length) {
                                                            int length = bytes.length - i6 >= 8192 ? 8192 : bytes.length - i6;
                                                            requestStream.write((byte) length);
                                                            requestStream.write((byte) (length >> 8));
                                                            requestStream.write((byte) (length >> 16));
                                                            requestStream.write((byte) ((length >> 24) | 128));
                                                            int i7 = 0;
                                                            while (i7 < length) {
                                                                int i8 = i6 + 1;
                                                                requestStream.write(bytes[i6]);
                                                                i7++;
                                                                i6 = i8;
                                                            }
                                                        }
                                                    }
                                                }
                                                requestStream.write(0);
                                                return;
                                            case 37:
                                                if (paramInfo.value == null) {
                                                    requestStream.write(0);
                                                    return;
                                                }
                                                byte[] bytes2 = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                                                if (requestStream.getTdsVersion() >= 3 || bytes2.length != 0) {
                                                    requestStream.write((byte) bytes2.length);
                                                    requestStream.write(bytes2);
                                                    return;
                                                }
                                                requestStream.write(1);
                                                requestStream.write(0);
                                                return;
                                            case 38:
                                                if (paramInfo.value == null) {
                                                    requestStream.write(0);
                                                    return;
                                                }
                                                if ("bigint".equals(paramInfo.sqlType)) {
                                                    requestStream.write(8);
                                                    requestStream.write(((Number) paramInfo.value).longValue());
                                                    return;
                                                }
                                                requestStream.write(4);
                                                requestStream.write(((Number) paramInfo.value).intValue());
                                                return;
                                            case 39:
                                                if (paramInfo.value == null) {
                                                    requestStream.write(0);
                                                    return;
                                                }
                                                byte[] bytes3 = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                                                if (bytes3.length == 0) {
                                                    bytes3 = new byte[]{32};
                                                }
                                                if (bytes3.length <= 255) {
                                                    requestStream.write((byte) bytes3.length);
                                                    requestStream.write(bytes3);
                                                    return;
                                                }
                                                throw new SQLException(Messages.get("error.generic.truncmbcs"), "HY000");
                                            default:
                                                StringBuilder sb = new StringBuilder();
                                                sb.append("Unsupported output TDS type ");
                                                sb.append(Integer.toHexString(paramInfo.tdsType));
                                                throw new IllegalStateException(sb.toString());
                                        }
                                    } else if (paramInfo.value == null) {
                                        requestStream.write(0);
                                        return;
                                    } else if (paramInfo.value instanceof Float) {
                                        requestStream.write(4);
                                        requestStream.write(((Number) paramInfo.value).floatValue());
                                        return;
                                    } else {
                                        requestStream.write(8);
                                        requestStream.write(((Number) paramInfo.value).doubleValue());
                                        return;
                                    }
                                }
                            } else if (paramInfo.value == null) {
                                requestStream.write(0);
                                return;
                            } else if (paramInfo.sqlType.startsWith("univarchar")) {
                                String string2 = paramInfo.getString(paramInfo.charsetInfo.getCharset());
                                if (string2.length() == 0) {
                                    string2 = " ";
                                }
                                requestStream.write(string2.length() * 2);
                                requestStream.write(string2.toCharArray(), 0, string2.length());
                                return;
                            } else {
                                byte[] bytes4 = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                                if (bytes4.length > 0) {
                                    requestStream.write(bytes4.length);
                                    requestStream.write(bytes4);
                                    return;
                                }
                                requestStream.write(1);
                                requestStream.write(0);
                                return;
                            }
                        } else if (paramInfo.value == null) {
                            requestStream.write(0);
                            return;
                        } else {
                            byte[] bytes5 = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                            if (bytes5.length == 0) {
                                bytes5 = new byte[]{32};
                            }
                            requestStream.write(bytes5.length);
                            requestStream.write(bytes5);
                            return;
                        }
                    } else if (paramInfo.value == null) {
                        requestStream.write(0);
                        return;
                    } else {
                        requestStream.write(4);
                        requestStream.write(((DateTime) paramInfo.value).getTime());
                        return;
                    }
                } else if (paramInfo.value == null) {
                    requestStream.write(0);
                    return;
                } else {
                    requestStream.write(4);
                    requestStream.write(((DateTime) paramInfo.value).getDate());
                    return;
                }
            }
            BigDecimal bigDecimal = null;
            if (paramInfo.value != null) {
                if (paramInfo.value instanceof Long) {
                    bigDecimal = new BigDecimal(paramInfo.value.toString());
                } else {
                    bigDecimal = (BigDecimal) paramInfo.value;
                }
            }
            requestStream.write(bigDecimal);
        } else if (paramInfo.value == null) {
            requestStream.write(0);
        } else {
            requestStream.write(((Boolean) paramInfo.value).booleanValue() ? (byte) 1 : 0);
        }
    }

    static void putCollation(RequestStream requestStream, ParamInfo paramInfo) throws IOException {
        if (!types[paramInfo.tdsType].isCollation) {
            return;
        }
        if (paramInfo.collation != null) {
            requestStream.write(paramInfo.collation);
        } else {
            requestStream.write(new byte[]{0, 0, 0, 0, 0});
        }
    }

    static void writeParam(RequestStream requestStream, CharsetInfo charsetInfo, byte[] bArr, ParamInfo paramInfo) throws IOException {
        int i;
        byte b = 4;
        int i2 = 1;
        int i3 = 0;
        boolean z = requestStream.getTdsVersion() >= 4;
        if (z && paramInfo.collation == null) {
            paramInfo.collation = bArr;
        }
        if (paramInfo.charsetInfo == null) {
            paramInfo.charsetInfo = charsetInfo;
        }
        int i4 = paramInfo.tdsType;
        if (i4 == 34) {
            if (paramInfo.value == null) {
                i = 0;
            } else {
                i = paramInfo.length;
            }
            requestStream.write((byte) paramInfo.tdsType);
            if (i > 0) {
                if (paramInfo.value instanceof InputStream) {
                    requestStream.write(i);
                    requestStream.write(i);
                    requestStream.writeStreamBytes((InputStream) paramInfo.value, i);
                    return;
                }
                byte[] bytes = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                requestStream.write(bytes.length);
                requestStream.write(bytes.length);
                requestStream.write(bytes);
            } else if (requestStream.getTdsVersion() < 3) {
                requestStream.write(1);
                requestStream.write(1);
                requestStream.write(0);
            } else {
                requestStream.write(i);
                requestStream.write(i);
            }
        } else if (i4 == 35) {
            if (paramInfo.value == null) {
                i2 = 0;
            } else {
                int i5 = paramInfo.length;
                if (i5 != 0 || requestStream.getTdsVersion() >= 3) {
                    i2 = i5;
                } else {
                    paramInfo.value = " ";
                }
            }
            requestStream.write((byte) paramInfo.tdsType);
            if (i2 <= 0) {
                requestStream.write(i2);
                if (z) {
                    putCollation(requestStream, paramInfo);
                }
                requestStream.write(i2);
            } else if (paramInfo.value instanceof InputStream) {
                requestStream.write(i2);
                if (z) {
                    putCollation(requestStream, paramInfo);
                }
                requestStream.write(i2);
                requestStream.writeStreamBytes((InputStream) paramInfo.value, i2);
            } else if (!(paramInfo.value instanceof Reader) || paramInfo.charsetInfo.isWideChars()) {
                byte[] bytes2 = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                requestStream.write(bytes2.length);
                if (z) {
                    putCollation(requestStream, paramInfo);
                }
                requestStream.write(bytes2.length);
                requestStream.write(bytes2);
            } else {
                requestStream.write(i2);
                if (z) {
                    putCollation(requestStream, paramInfo);
                }
                requestStream.write(i2);
                requestStream.writeReaderBytes((Reader) paramInfo.value, i2);
            }
        } else if (i4 == 50) {
            requestStream.write((byte) paramInfo.tdsType);
            if (paramInfo.value == null) {
                requestStream.write(0);
            } else {
                requestStream.write(((Boolean) paramInfo.value).booleanValue() ? (byte) 1 : 0);
            }
        } else if (i4 == 99) {
            if (paramInfo.value != null) {
                i3 = paramInfo.length;
            }
            requestStream.write((byte) paramInfo.tdsType);
            if (i3 <= 0) {
                requestStream.write(i3);
                if (z) {
                    putCollation(requestStream, paramInfo);
                }
                requestStream.write(i3);
            } else if (paramInfo.value instanceof Reader) {
                requestStream.write(i3);
                if (z) {
                    putCollation(requestStream, paramInfo);
                }
                requestStream.write(i3 * 2);
                requestStream.writeReaderChars((Reader) paramInfo.value, i3);
            } else if (!(paramInfo.value instanceof InputStream) || paramInfo.charsetInfo.isWideChars()) {
                String string = paramInfo.getString(paramInfo.charsetInfo.getCharset());
                int length = string.length();
                requestStream.write(length);
                if (z) {
                    putCollation(requestStream, paramInfo);
                }
                requestStream.write(length * 2);
                requestStream.write(string);
            } else {
                requestStream.write(i3);
                if (z) {
                    putCollation(requestStream, paramInfo);
                }
                requestStream.write(i3 * 2);
                requestStream.writeReaderChars(new InputStreamReader((InputStream) paramInfo.value, paramInfo.charsetInfo.getCharset()), i3);
            }
        } else if (i4 != 104) {
            if (i4 != 106) {
                if (i4 == 111) {
                    requestStream.write(111);
                    requestStream.write(8);
                    putDateTimeValue(requestStream, (DateTime) paramInfo.value);
                    return;
                } else if (i4 == XSYBVARBINARY) {
                    requestStream.write((byte) paramInfo.tdsType);
                    requestStream.write(8000);
                    if (paramInfo.value == null) {
                        requestStream.write(-1);
                        return;
                    }
                    byte[] bytes3 = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                    requestStream.write((short) bytes3.length);
                    requestStream.write(bytes3);
                    return;
                } else if (i4 != XSYBVARCHAR) {
                    if (i4 == XSYBNVARCHAR) {
                        requestStream.write((byte) paramInfo.tdsType);
                        requestStream.write(8000);
                        if (z) {
                            putCollation(requestStream, paramInfo);
                        }
                        if (paramInfo.value == null) {
                            requestStream.write(-1);
                            return;
                        }
                        String string2 = paramInfo.getString(paramInfo.charsetInfo.getCharset());
                        requestStream.write((short) (string2.length() * 2));
                        requestStream.write(string2);
                        return;
                    } else if (i4 == XML) {
                        int i6 = paramInfo.length;
                        requestStream.write((byte) paramInfo.tdsType);
                        requestStream.write(0);
                        if (paramInfo.value == null) {
                            requestStream.write(-1);
                            return;
                        }
                        requestStream.write((long) i6);
                        requestStream.write(i6);
                        if (paramInfo.value instanceof byte[]) {
                            requestStream.write((byte[]) paramInfo.value);
                        } else if (paramInfo.value instanceof InputStream) {
                            byte[] bArr2 = new byte[1024];
                            while (i6 > 0) {
                                int read = ((InputStream) paramInfo.value).read(bArr2);
                                if (read >= 0) {
                                    requestStream.write(bArr2, 0, read);
                                    i6 -= read;
                                } else {
                                    throw new IOException(Messages.get("error.io.outofdata"));
                                }
                            }
                        }
                        requestStream.write(0);
                        return;
                    } else if (i4 != 108) {
                        if (i4 != 109) {
                            switch (i4) {
                                case 37:
                                    requestStream.write((byte) paramInfo.tdsType);
                                    requestStream.write(-1);
                                    if (paramInfo.value == null) {
                                        requestStream.write(0);
                                        return;
                                    }
                                    byte[] bytes4 = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                                    if (requestStream.getTdsVersion() >= 3 || bytes4.length != 0) {
                                        requestStream.write((byte) bytes4.length);
                                        requestStream.write(bytes4);
                                        return;
                                    }
                                    requestStream.write(1);
                                    requestStream.write(0);
                                    return;
                                case 38:
                                    requestStream.write((byte) paramInfo.tdsType);
                                    String str = "bigint";
                                    if (paramInfo.value == null) {
                                        if (str.equals(paramInfo.sqlType)) {
                                            b = 8;
                                        }
                                        requestStream.write(b);
                                        requestStream.write(0);
                                        return;
                                    } else if (str.equals(paramInfo.sqlType)) {
                                        requestStream.write(8);
                                        requestStream.write(8);
                                        requestStream.write(((Number) paramInfo.value).longValue());
                                        return;
                                    } else {
                                        requestStream.write(4);
                                        requestStream.write(4);
                                        requestStream.write(((Number) paramInfo.value).intValue());
                                        return;
                                    }
                                case 39:
                                    if (paramInfo.value == null) {
                                        requestStream.write((byte) paramInfo.tdsType);
                                        requestStream.write(-1);
                                        requestStream.write(0);
                                        return;
                                    }
                                    byte[] bytes5 = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                                    if (bytes5.length <= 255) {
                                        if (bytes5.length == 0) {
                                            bytes5 = new byte[]{32};
                                        }
                                        requestStream.write((byte) paramInfo.tdsType);
                                        requestStream.write(-1);
                                        requestStream.write((byte) bytes5.length);
                                        requestStream.write(bytes5);
                                        return;
                                    } else if (bytes5.length > MS_LONGVAR_MAX || requestStream.getTdsVersion() < 3) {
                                        requestStream.write(35);
                                        requestStream.write(bytes5.length);
                                        if (z) {
                                            putCollation(requestStream, paramInfo);
                                        }
                                        requestStream.write(bytes5.length);
                                        requestStream.write(bytes5);
                                        return;
                                    } else {
                                        requestStream.write(-89);
                                        requestStream.write(8000);
                                        if (z) {
                                            putCollation(requestStream, paramInfo);
                                        }
                                        requestStream.write((short) bytes5.length);
                                        requestStream.write(bytes5);
                                        return;
                                    }
                                default:
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("Unsupported output TDS type ");
                                    sb.append(Integer.toHexString(paramInfo.tdsType));
                                    throw new IllegalStateException(sb.toString());
                            }
                        } else {
                            requestStream.write((byte) paramInfo.tdsType);
                            if (paramInfo.value instanceof Float) {
                                requestStream.write(4);
                                requestStream.write(4);
                                requestStream.write(((Number) paramInfo.value).floatValue());
                                return;
                            }
                            requestStream.write(8);
                            if (paramInfo.value == null) {
                                requestStream.write(0);
                                return;
                            }
                            requestStream.write(8);
                            requestStream.write(((Number) paramInfo.value).doubleValue());
                            return;
                        }
                    }
                } else if (paramInfo.value == null) {
                    requestStream.write((byte) paramInfo.tdsType);
                    requestStream.write(8000);
                    if (z) {
                        putCollation(requestStream, paramInfo);
                    }
                    requestStream.write(-1);
                    return;
                } else {
                    byte[] bytes6 = paramInfo.getBytes(paramInfo.charsetInfo.getCharset());
                    if (bytes6.length > MS_LONGVAR_MAX) {
                        requestStream.write(35);
                        requestStream.write(bytes6.length);
                        if (z) {
                            putCollation(requestStream, paramInfo);
                        }
                        requestStream.write(bytes6.length);
                        requestStream.write(bytes6);
                        return;
                    }
                    requestStream.write((byte) paramInfo.tdsType);
                    requestStream.write(8000);
                    if (z) {
                        putCollation(requestStream, paramInfo);
                    }
                    requestStream.write((short) bytes6.length);
                    requestStream.write(bytes6);
                    return;
                }
            }
            requestStream.write((byte) paramInfo.tdsType);
            BigDecimal bigDecimal = null;
            int maxPrecision = requestStream.getMaxPrecision();
            if (paramInfo.value == null) {
                if (paramInfo.jdbcType != -5) {
                    i3 = (paramInfo.scale < 0 || paramInfo.scale > maxPrecision) ? 10 : paramInfo.scale;
                }
            } else if (paramInfo.value instanceof Long) {
                bigDecimal = new BigDecimal(((Long) paramInfo.value).toString());
            } else {
                bigDecimal = (BigDecimal) paramInfo.value;
                i3 = bigDecimal.scale();
            }
            requestStream.write(requestStream.getMaxDecimalBytes());
            requestStream.write((byte) maxPrecision);
            requestStream.write((byte) i3);
            requestStream.write(bigDecimal);
        } else {
            requestStream.write(104);
            requestStream.write(1);
            if (paramInfo.value == null) {
                requestStream.write(0);
                return;
            }
            requestStream.write(1);
            requestStream.write(((Boolean) paramInfo.value).booleanValue() ? (byte) 1 : 0);
        }
    }

    private TdsData() {
    }

    private static Object getDatetimeValue(ResponseStream responseStream, int i) throws IOException, ProtocolException {
        int i2 = i == 111 ? responseStream.read() : i == 58 ? 4 : 8;
        if (i2 == 0) {
            return null;
        }
        if (i2 == 4) {
            return new DateTime((short) (responseStream.readShort() & 65535), (short) responseStream.readShort());
        } else if (i2 == 8) {
            return new DateTime(responseStream.readInt(), responseStream.readInt());
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid DATETIME value with size of ");
            sb.append(i2);
            sb.append(" bytes.");
            throw new ProtocolException(sb.toString());
        }
    }

    private static void putDateTimeValue(RequestStream requestStream, DateTime dateTime) throws IOException {
        if (dateTime == null) {
            requestStream.write(0);
            return;
        }
        requestStream.write(8);
        requestStream.write(dateTime.getDate());
        requestStream.write(dateTime.getTime());
    }

    private static Object getMoneyValue(ResponseStream responseStream, int i) throws IOException, ProtocolException {
        BigInteger bigInteger;
        int i2 = i == 60 ? 8 : i == 110 ? responseStream.read() : 4;
        if (i2 == 4) {
            bigInteger = BigInteger.valueOf((long) responseStream.readInt());
        } else if (i2 == 8) {
            bigInteger = BigInteger.valueOf(((long) (((byte) responseStream.read()) & 255)) + (((long) (((byte) responseStream.read()) & 255)) << 8) + (((long) (((byte) responseStream.read()) & 255)) << 16) + (((long) (((byte) responseStream.read()) & 255)) << 24) + (((long) (((byte) responseStream.read()) & 255)) << 32) + (((long) (((byte) responseStream.read()) & 255)) << 40) + (((long) (((byte) responseStream.read()) & 255)) << 48) + (((long) (((byte) responseStream.read()) & 255)) << 56));
        } else if (i2 == 0) {
            bigInteger = null;
        } else {
            throw new ProtocolException("Invalid money value.");
        }
        if (bigInteger == null) {
            return null;
        }
        return new BigDecimal(bigInteger, 4);
    }

    private static Object getVariant(JtdsConnection jtdsConnection, ResponseStream responseStream) throws IOException, ProtocolException {
        int readInt = responseStream.readInt();
        if (readInt == 0) {
            return null;
        }
        ColInfo colInfo = new ColInfo();
        int i = readInt - 2;
        colInfo.tdsType = responseStream.read();
        int read = i - responseStream.read();
        int i2 = colInfo.tdsType;
        switch (i2) {
            case 36:
                byte[] bArr = new byte[read];
                responseStream.read(bArr);
                return new UniqueIdentifier(bArr);
            case 48:
                return new Integer(responseStream.read() & 255);
            case 50:
                return responseStream.read() != 0 ? Boolean.TRUE : Boolean.FALSE;
            case 52:
                return new Integer(responseStream.readShort());
            case 56:
                return new Integer(responseStream.readInt());
            case 106:
            case 108:
                colInfo.precision = responseStream.read();
                colInfo.scale = responseStream.read();
                int read2 = responseStream.read();
                int i3 = -1;
                int i4 = read - 1;
                byte[] bArr2 = new byte[i4];
                while (true) {
                    int i5 = i4 - 1;
                    if (i4 > 0) {
                        bArr2[i5] = (byte) responseStream.read();
                        i4 = i5;
                    } else {
                        if (read2 != 0) {
                            i3 = 1;
                        }
                        return new BigDecimal(new BigInteger(i3, bArr2), colInfo.scale);
                    }
                }
            case 122:
                break;
            case SYBINT8 /*127*/:
                return new Long(responseStream.readLong());
            case XSYBVARBINARY /*165*/:
            case XSYBBINARY /*173*/:
                responseStream.skip(2);
                byte[] bArr3 = new byte[read];
                responseStream.read(bArr3);
                return bArr3;
            case XSYBVARCHAR /*167*/:
            case XSYBCHAR /*175*/:
                getCollation(responseStream, colInfo);
                try {
                    setColumnCharset(colInfo, jtdsConnection);
                    responseStream.skip(2);
                    return responseStream.readNonUnicodeString(read);
                } catch (SQLException e) {
                    responseStream.skip(read + 2);
                    StringBuilder sb = new StringBuilder();
                    sb.append(e.toString());
                    sb.append(" [SQLState: ");
                    sb.append(e.getSQLState());
                    sb.append(']');
                    throw new ProtocolException(sb.toString());
                }
            case XSYBNVARCHAR /*231*/:
            case XSYBNCHAR /*239*/:
                responseStream.skip(7);
                return responseStream.readUnicodeString(read / 2);
            default:
                switch (i2) {
                    case 58:
                    case 61:
                        return getDatetimeValue(responseStream, colInfo.tdsType);
                    case 59:
                        return new Float(Float.intBitsToFloat(responseStream.readInt()));
                    case 60:
                        break;
                    case 62:
                        return new Double(Double.longBitsToDouble(responseStream.readLong()));
                    default:
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Unsupported TDS data type 0x");
                        sb2.append(Integer.toHexString(colInfo.tdsType));
                        sb2.append(" in sql_variant");
                        throw new ProtocolException(sb2.toString());
                }
        }
        return getMoneyValue(responseStream, colInfo.tdsType);
    }

    public static String getMSTypeName(String str, int i) {
        if (str.equalsIgnoreCase("text") && i != 35) {
            return "varchar";
        }
        if (str.equalsIgnoreCase("ntext") && i != 35) {
            return "nvarchar";
        }
        if (str.equalsIgnoreCase("image") && i != 34) {
            str = "varbinary";
        }
        return str;
    }

    private static boolean canEncode(String str, String str2) {
        if (str == null || Key.STRING_CHARSET_NAME.equals(str2)) {
            return true;
        }
        if ("ISO-8859-1".equals(str2)) {
            for (int length = str.length() - 1; length >= 0; length--) {
                if (str.charAt(length) > 255) {
                    return false;
                }
            }
            return true;
        } else if ("ISO-8859-15".equals(str2) || "Cp1252".equals(str2)) {
            for (int length2 = str.length() - 1; length2 >= 0; length2--) {
                char charAt = str.charAt(length2);
                if (charAt > 255 && charAt != 8364) {
                    return false;
                }
            }
            return true;
        } else if ("US-ASCII".equals(str2)) {
            for (int length3 = str.length() - 1; length3 >= 0; length3--) {
                if (str.charAt(length3) > SYBINT8) {
                    return false;
                }
            }
            return true;
        } else {
            try {
                return new String(str.getBytes(str2), str2).equals(str);
            } catch (UnsupportedEncodingException unused) {
                return false;
            }
        }
    }

    static boolean isMSSQL2005Plus(JtdsConnection jtdsConnection) {
        return jtdsConnection.getServerType() == 1 && jtdsConnection.getDatabaseMajorVersion() > 8;
    }
}
