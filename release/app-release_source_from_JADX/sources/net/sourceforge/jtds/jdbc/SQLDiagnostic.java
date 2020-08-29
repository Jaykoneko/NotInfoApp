package net.sourceforge.jtds.jdbc;

import androidx.core.view.InputDeviceCompat;
import androidx.core.view.PointerIconCompat;
import java.sql.DataTruncation;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.HashMap;

class SQLDiagnostic {
    private static final HashMap mssqlStates = new HashMap();
    private static final HashMap sybStates = new HashMap();
    SQLException exceptions;
    SQLException lastException;
    SQLWarning lastWarning;
    private final int serverType;
    SQLWarning warnings;

    static {
        String str = "42000";
        mssqlStates.put(new Integer(102), str);
        String str2 = "37000";
        mssqlStates.put(new Integer(105), str2);
        String str3 = "21S01";
        mssqlStates.put(new Integer(109), str3);
        mssqlStates.put(new Integer(110), str3);
        mssqlStates.put(new Integer(113), str);
        mssqlStates.put(new Integer(131), str2);
        String str4 = "22003";
        mssqlStates.put(new Integer(168), str4);
        mssqlStates.put(new Integer(170), str2);
        mssqlStates.put(new Integer(174), str2);
        mssqlStates.put(new Integer(195), str);
        mssqlStates.put(new Integer(201), str2);
        String str5 = "22005";
        mssqlStates.put(new Integer(206), str5);
        String str6 = "42S22";
        mssqlStates.put(new Integer(207), str6);
        mssqlStates.put(new Integer(208), "S0002");
        String str7 = "22007";
        mssqlStates.put(new Integer(210), str7);
        String str8 = "22008";
        mssqlStates.put(new Integer(211), str8);
        mssqlStates.put(new Integer(213), str);
        mssqlStates.put(new Integer(220), str4);
        mssqlStates.put(new Integer(229), str);
        mssqlStates.put(new Integer(230), str);
        mssqlStates.put(new Integer(232), str4);
        String str9 = "23000";
        mssqlStates.put(new Integer(233), str9);
        mssqlStates.put(new Integer(234), str4);
        mssqlStates.put(new Integer(235), str5);
        mssqlStates.put(new Integer(236), str4);
        mssqlStates.put(new Integer(237), str4);
        mssqlStates.put(new Integer(238), str4);
        mssqlStates.put(new Integer(241), str7);
        mssqlStates.put(new Integer(242), str8);
        mssqlStates.put(new Integer(244), str4);
        mssqlStates.put(new Integer(245), "22018");
        mssqlStates.put(new Integer(246), str4);
        mssqlStates.put(new Integer(247), str5);
        mssqlStates.put(new Integer(248), str4);
        mssqlStates.put(new Integer(249), str5);
        mssqlStates.put(new Integer(256), str5);
        mssqlStates.put(new Integer(InputDeviceCompat.SOURCE_KEYBOARD), str5);
        mssqlStates.put(new Integer(260), str);
        mssqlStates.put(new Integer(262), str);
        String str10 = "25000";
        mssqlStates.put(new Integer(266), str10);
        mssqlStates.put(new Integer(272), str9);
        mssqlStates.put(new Integer(273), str9);
        mssqlStates.put(new Integer(277), str10);
        mssqlStates.put(new Integer(295), str7);
        mssqlStates.put(new Integer(296), str8);
        mssqlStates.put(new Integer(298), str8);
        mssqlStates.put(new Integer(305), str5);
        String str11 = "42S12";
        mssqlStates.put(new Integer(307), str11);
        mssqlStates.put(new Integer(308), str11);
        mssqlStates.put(new Integer(310), "22025");
        mssqlStates.put(new Integer(409), str5);
        mssqlStates.put(new Integer(506), "22019");
        mssqlStates.put(new Integer(512), "21000");
        mssqlStates.put(new Integer(515), str9);
        mssqlStates.put(new Integer(517), str8);
        mssqlStates.put(new Integer(518), str5);
        mssqlStates.put(new Integer(519), str4);
        mssqlStates.put(new Integer(520), str4);
        mssqlStates.put(new Integer(521), str4);
        mssqlStates.put(new Integer(522), str4);
        mssqlStates.put(new Integer(523), str4);
        mssqlStates.put(new Integer(524), str4);
        mssqlStates.put(new Integer(529), str5);
        mssqlStates.put(new Integer(530), str9);
        mssqlStates.put(new Integer(532), "01001");
        mssqlStates.put(new Integer(535), str4);
        mssqlStates.put(new Integer(542), str8);
        mssqlStates.put(new Integer(544), str9);
        mssqlStates.put(new Integer(547), str9);
        mssqlStates.put(new Integer(550), "44000");
        mssqlStates.put(new Integer(611), str10);
        mssqlStates.put(new Integer(626), str10);
        mssqlStates.put(new Integer(627), str10);
        mssqlStates.put(new Integer(628), str10);
        mssqlStates.put(new Integer(911), "08004");
        mssqlStates.put(new Integer(PointerIconCompat.TYPE_CROSSHAIR), str4);
        mssqlStates.put(new Integer(PointerIconCompat.TYPE_ALIAS), "22019");
        String str12 = "40001";
        mssqlStates.put(new Integer(1205), str12);
        mssqlStates.put(new Integer(1211), str12);
        mssqlStates.put(new Integer(1505), str9);
        mssqlStates.put(new Integer(1508), str9);
        mssqlStates.put(new Integer(1774), "21S02");
        mssqlStates.put(new Integer(1911), str6);
        String str13 = "42S11";
        mssqlStates.put(new Integer(1913), str13);
        mssqlStates.put(new Integer(2526), str2);
        mssqlStates.put(new Integer(2557), str);
        mssqlStates.put(new Integer(2571), str);
        mssqlStates.put(new Integer(2601), str9);
        mssqlStates.put(new Integer(2615), str9);
        mssqlStates.put(new Integer(2625), str12);
        mssqlStates.put(new Integer(2626), str9);
        mssqlStates.put(new Integer(2627), str9);
        mssqlStates.put(new Integer(2714), "S0001");
        mssqlStates.put(new Integer(2760), str);
        mssqlStates.put(new Integer(2812), str2);
        mssqlStates.put(new Integer(3110), str);
        mssqlStates.put(new Integer(3309), str12);
        mssqlStates.put(new Integer(3604), str9);
        mssqlStates.put(new Integer(3605), str9);
        mssqlStates.put(new Integer(3606), str4);
        String str14 = "22012";
        mssqlStates.put(new Integer(3607), str14);
        mssqlStates.put(new Integer(3621), "01000");
        String str15 = "42S02";
        mssqlStates.put(new Integer(3701), str15);
        mssqlStates.put(new Integer(3704), str);
        mssqlStates.put(new Integer(3725), str9);
        mssqlStates.put(new Integer(3726), str9);
        mssqlStates.put(new Integer(3902), str10);
        mssqlStates.put(new Integer(3903), str10);
        mssqlStates.put(new Integer(3906), str10);
        mssqlStates.put(new Integer(3908), str10);
        mssqlStates.put(new Integer(3915), str10);
        mssqlStates.put(new Integer(3916), str10);
        mssqlStates.put(new Integer(3918), str10);
        mssqlStates.put(new Integer(3919), str10);
        mssqlStates.put(new Integer(3921), str10);
        mssqlStates.put(new Integer(3922), str10);
        mssqlStates.put(new Integer(3926), str10);
        mssqlStates.put(new Integer(3960), "S0005");
        mssqlStates.put(new Integer(4415), "44000");
        mssqlStates.put(new Integer(4613), str);
        mssqlStates.put(new Integer(4618), str);
        mssqlStates.put(new Integer(4712), str9);
        mssqlStates.put(new Integer(4834), str);
        mssqlStates.put(new Integer(4924), str6);
        String str16 = "42S21";
        mssqlStates.put(new Integer(4925), str16);
        String str17 = str8;
        mssqlStates.put(new Integer(4926), str6);
        mssqlStates.put(new Integer(5011), str);
        mssqlStates.put(new Integer(5116), str);
        mssqlStates.put(new Integer(5146), str4);
        mssqlStates.put(new Integer(5812), str);
        mssqlStates.put(new Integer(6004), str);
        mssqlStates.put(new Integer(6102), str);
        mssqlStates.put(new Integer(6104), str2);
        mssqlStates.put(new Integer(6401), str10);
        mssqlStates.put(new Integer(7112), str12);
        mssqlStates.put(new Integer(7956), str);
        mssqlStates.put(new Integer(7969), str10);
        mssqlStates.put(new Integer(8114), str2);
        mssqlStates.put(new Integer(8115), str4);
        mssqlStates.put(new Integer(8134), str14);
        mssqlStates.put(new Integer(8144), str2);
        mssqlStates.put(new Integer(8152), "22001");
        mssqlStates.put(new Integer(8162), str2);
        mssqlStates.put(new Integer(8153), "01003");
        mssqlStates.put(new Integer(8506), str10);
        mssqlStates.put(new Integer(10015), str4);
        mssqlStates.put(new Integer(10033), str11);
        mssqlStates.put(new Integer(10055), str9);
        mssqlStates.put(new Integer(10065), str9);
        mssqlStates.put(new Integer(10095), "01001");
        mssqlStates.put(new Integer(11010), str);
        mssqlStates.put(new Integer(11011), str9);
        mssqlStates.put(new Integer(11040), str9);
        mssqlStates.put(new Integer(11045), str);
        mssqlStates.put(new Integer(14126), str);
        mssqlStates.put(new Integer(15247), str);
        mssqlStates.put(new Integer(15323), str11);
        mssqlStates.put(new Integer(15605), str13);
        mssqlStates.put(new Integer(15622), str);
        mssqlStates.put(new Integer(15626), str10);
        mssqlStates.put(new Integer(15645), str6);
        String str18 = "24000";
        mssqlStates.put(new Integer(16905), str18);
        mssqlStates.put(new Integer(16909), str18);
        mssqlStates.put(new Integer(16911), str18);
        mssqlStates.put(new Integer(16917), str18);
        mssqlStates.put(new Integer(16934), str18);
        mssqlStates.put(new Integer(16946), str18);
        mssqlStates.put(new Integer(16950), str18);
        mssqlStates.put(new Integer(16999), str18);
        mssqlStates.put(new Integer(17308), str);
        mssqlStates.put(new Integer(17571), str);
        mssqlStates.put(new Integer(18002), str);
        mssqlStates.put(new Integer(18452), "28000");
        mssqlStates.put(new Integer(18456), "28000");
        mssqlStates.put(new Integer(18833), str11);
        mssqlStates.put(new Integer(20604), str);
        mssqlStates.put(new Integer(21049), str);
        mssqlStates.put(new Integer(21166), str6);
        mssqlStates.put(new Integer(21255), str16);
        sybStates.put(new Integer(102), str2);
        sybStates.put(new Integer(109), str3);
        sybStates.put(new Integer(110), str3);
        sybStates.put(new Integer(113), str);
        sybStates.put(new Integer(168), str4);
        sybStates.put(new Integer(201), str2);
        sybStates.put(new Integer(207), str6);
        sybStates.put(new Integer(208), str15);
        sybStates.put(new Integer(213), str3);
        sybStates.put(new Integer(220), str4);
        sybStates.put(new Integer(227), str4);
        sybStates.put(new Integer(229), str);
        sybStates.put(new Integer(230), str);
        sybStates.put(new Integer(232), str4);
        sybStates.put(new Integer(233), str9);
        sybStates.put(new Integer(245), "22018");
        sybStates.put(new Integer(247), str4);
        sybStates.put(new Integer(InputDeviceCompat.SOURCE_KEYBOARD), str2);
        sybStates.put(new Integer(262), str);
        sybStates.put(new Integer(277), str10);
        sybStates.put(new Integer(307), str11);
        sybStates.put(new Integer(512), "21000");
        String str19 = str17;
        sybStates.put(new Integer(517), str19);
        sybStates.put(new Integer(535), str19);
        sybStates.put(new Integer(542), str19);
        sybStates.put(new Integer(544), str9);
        sybStates.put(new Integer(545), str9);
        sybStates.put(new Integer(546), str9);
        sybStates.put(new Integer(547), str9);
        sybStates.put(new Integer(548), str9);
        sybStates.put(new Integer(549), str9);
        sybStates.put(new Integer(550), str9);
        sybStates.put(new Integer(558), str18);
        sybStates.put(new Integer(559), str18);
        sybStates.put(new Integer(562), str18);
        sybStates.put(new Integer(565), str18);
        sybStates.put(new Integer(583), str18);
        sybStates.put(new Integer(611), str10);
        sybStates.put(new Integer(627), str10);
        sybStates.put(new Integer(628), str10);
        sybStates.put(new Integer(641), str10);
        sybStates.put(new Integer(642), str10);
        sybStates.put(new Integer(911), "08004");
        sybStates.put(new Integer(1276), str10);
        sybStates.put(new Integer(1505), str9);
        sybStates.put(new Integer(1508), str9);
        sybStates.put(new Integer(1715), "21S02");
        sybStates.put(new Integer(1720), str6);
        sybStates.put(new Integer(1913), str13);
        sybStates.put(new Integer(1921), str16);
        sybStates.put(new Integer(2526), str2);
        sybStates.put(new Integer(2714), "42S01");
        sybStates.put(new Integer(2812), str2);
        sybStates.put(new Integer(3606), str4);
        sybStates.put(new Integer(3607), str14);
        sybStates.put(new Integer(3621), "01000");
        sybStates.put(new Integer(3701), str15);
        sybStates.put(new Integer(3902), str10);
        sybStates.put(new Integer(3903), str10);
        sybStates.put(new Integer(4602), str);
        sybStates.put(new Integer(4603), str);
        sybStates.put(new Integer(4608), str);
        sybStates.put(new Integer(4934), str6);
        sybStates.put(new Integer(6104), str2);
        sybStates.put(new Integer(6235), str18);
        sybStates.put(new Integer(6259), str18);
        sybStates.put(new Integer(6260), str18);
        sybStates.put(new Integer(7010), str11);
        sybStates.put(new Integer(7327), str2);
        sybStates.put(new Integer(9501), "01003");
        sybStates.put(new Integer(9502), "22001");
        sybStates.put(new Integer(10306), str);
        sybStates.put(new Integer(10323), str);
        sybStates.put(new Integer(10330), str);
        sybStates.put(new Integer(10331), str);
        sybStates.put(new Integer(10332), str);
        sybStates.put(new Integer(11021), str2);
        sybStates.put(new Integer(11110), str);
        sybStates.put(new Integer(11113), str);
        sybStates.put(new Integer(11118), str);
        sybStates.put(new Integer(11121), str);
        sybStates.put(new Integer(17222), str);
        sybStates.put(new Integer(17223), str);
        sybStates.put(new Integer(18091), str11);
        sybStates.put(new Integer(18117), str6);
        sybStates.put(new Integer(18350), str);
        sybStates.put(new Integer(18351), str);
    }

    /* access modifiers changed from: 0000 */
    public void addWarning(SQLWarning sQLWarning) {
        if (this.warnings == null) {
            this.warnings = sQLWarning;
        } else {
            this.lastWarning.setNextWarning(sQLWarning);
        }
        this.lastWarning = sQLWarning;
    }

    /* access modifiers changed from: 0000 */
    public void addException(SQLException sQLException) {
        if (this.exceptions == null) {
            this.exceptions = sQLException;
        } else {
            this.lastException.setNextException(sQLException);
        }
        this.lastException = sQLException;
    }

    /* access modifiers changed from: 0000 */
    public void addDiagnostic(int i, int i2, int i3, String str, String str2, String str3, int i4) {
        if (i3 > 10) {
            SQLException sQLException = new SQLException(str, getStateCode(i, this.serverType, "S1000"), i);
            if ((this.serverType == 1 && (i == 8152 || i == 8115 || i == 220)) || (this.serverType == 2 && (i == 247 || i == 9502))) {
                DataTruncation dataTruncation = new DataTruncation(-1, false, false, -1, -1);
                dataTruncation.setNextException(sQLException);
                sQLException = dataTruncation;
            }
            addException(sQLException);
        } else if (i == 0) {
            addWarning(new SQLWarning(str, null, 0));
        } else {
            addWarning(new SQLWarning(str, getStateCode(i, this.serverType, "01000"), i));
        }
    }

    /* access modifiers changed from: 0000 */
    public void clearWarnings() {
        this.warnings = null;
    }

    /* access modifiers changed from: 0000 */
    public void checkErrors() throws SQLException {
        SQLException sQLException = this.exceptions;
        if (sQLException != null) {
            this.exceptions = null;
            throw sQLException;
        }
    }

    /* access modifiers changed from: 0000 */
    public SQLWarning getWarnings() {
        return this.warnings;
    }

    SQLDiagnostic(int i) {
        this.serverType = i;
    }

    private static String getStateCode(int i, int i2, String str) {
        String str2 = (String) (i2 == 2 ? sybStates : mssqlStates).get(new Integer(i));
        return str2 != null ? str2 : str;
    }
}
