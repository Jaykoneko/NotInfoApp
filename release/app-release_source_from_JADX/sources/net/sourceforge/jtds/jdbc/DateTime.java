package net.sourceforge.jtds.jdbc;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

public class DateTime {
    static final int DATE_NOT_USED = Integer.MIN_VALUE;
    static final int TIME_NOT_USED = Integer.MIN_VALUE;
    private int date;
    private Date dateValue;
    private short day;
    private short hour;
    private short millis;
    private short minute;
    private short month;
    private short second;
    private String stringValue;
    private int time;
    private Time timeValue;
    private Timestamp tsValue;
    private boolean unpacked;
    private short year;

    DateTime(int i, int i2) {
        this.date = i;
        this.time = i2;
    }

    DateTime(short s, short s2) {
        this.date = s & 65535;
        this.time = s2 * 60 * 300;
    }

    DateTime(Timestamp timestamp) throws SQLException {
        this.tsValue = timestamp;
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(timestamp);
        if (gregorianCalendar.get(0) == 1) {
            this.year = (short) gregorianCalendar.get(1);
            this.month = (short) (gregorianCalendar.get(2) + 1);
            this.day = (short) gregorianCalendar.get(5);
            this.hour = (short) gregorianCalendar.get(11);
            this.minute = (short) gregorianCalendar.get(12);
            this.second = (short) gregorianCalendar.get(13);
            this.millis = (short) gregorianCalendar.get(14);
            packDate();
            packTime();
            this.unpacked = true;
            return;
        }
        throw new SQLException(Messages.get("error.datetime.range.era"), "22007");
    }

    DateTime(Time time2) throws SQLException {
        this.timeValue = time2;
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(time2);
        if (gregorianCalendar.get(0) == 1) {
            this.date = Integer.MIN_VALUE;
            this.year = 1900;
            this.month = 1;
            this.day = 1;
            this.hour = (short) gregorianCalendar.get(11);
            this.minute = (short) gregorianCalendar.get(12);
            this.second = (short) gregorianCalendar.get(13);
            this.millis = (short) gregorianCalendar.get(14);
            packTime();
            this.year = 1970;
            this.month = 1;
            this.day = 1;
            this.unpacked = true;
            return;
        }
        throw new SQLException(Messages.get("error.datetime.range.era"), "22007");
    }

    DateTime(Date date2) throws SQLException {
        this.dateValue = date2;
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date2);
        if (gregorianCalendar.get(0) == 1) {
            this.year = (short) gregorianCalendar.get(1);
            this.month = (short) (gregorianCalendar.get(2) + 1);
            this.day = (short) gregorianCalendar.get(5);
            this.hour = 0;
            this.minute = 0;
            this.second = 0;
            this.millis = 0;
            packDate();
            this.time = Integer.MIN_VALUE;
            this.unpacked = true;
            return;
        }
        throw new SQLException(Messages.get("error.datetime.range.era"), "22007");
    }

    /* access modifiers changed from: 0000 */
    public int getDate() {
        int i = this.date;
        if (i == Integer.MIN_VALUE) {
            return 0;
        }
        return i;
    }

    /* access modifiers changed from: 0000 */
    public int getTime() {
        int i = this.time;
        if (i == Integer.MIN_VALUE) {
            return 0;
        }
        return i;
    }

    private void unpackDateTime() {
        int i = this.date;
        if (i == Integer.MIN_VALUE) {
            this.year = 1970;
            this.month = 1;
            this.day = 1;
        } else if (i == 0) {
            this.year = 1900;
            this.month = 1;
            this.day = 1;
        } else {
            int i2 = i + 68569 + 2415021;
            int i3 = (i2 * 4) / 146097;
            int i4 = i2 - (((146097 * i3) + 3) / 4);
            int i5 = ((i4 + 1) * 4000) / 1461001;
            int i6 = (i4 - ((i5 * 1461) / 4)) + 31;
            int i7 = (i6 * 80) / 2447;
            int i8 = i6 - ((i7 * 2447) / 80);
            int i9 = i7 / 11;
            int i10 = (i7 + 2) - (i9 * 12);
            this.year = (short) (((i3 - 49) * 100) + i5 + i9);
            this.month = (short) i10;
            this.day = (short) i8;
        }
        int i11 = this.time;
        if (i11 == Integer.MIN_VALUE) {
            this.hour = 0;
            this.minute = 0;
            this.second = 0;
        } else {
            int i12 = i11 / 1080000;
            int i13 = i11 - (1080000 * i12);
            this.time = i13;
            int i14 = i13 / 18000;
            int i15 = i13 - (i14 * 18000);
            this.time = i15;
            int i16 = i15 / 300;
            int i17 = i15 - (i16 * 300);
            this.time = i17;
            int round = Math.round(((float) (i17 * 1000)) / 300.0f);
            this.time = round;
            this.hour = (short) i12;
            this.minute = (short) i14;
            this.second = (short) i16;
            this.millis = (short) round;
        }
        this.unpacked = true;
    }

    public void packDate() throws SQLException {
        short s = this.year;
        if (s < 1753 || s > 9999) {
            throw new SQLException(Messages.get("error.datetime.range"), "22003");
        }
        int i = this.day - 32075;
        int i2 = s + 4800;
        short s2 = this.month;
        this.date = (((i + (((i2 + ((s2 - 14) / 12)) * 1461) / 4)) + ((((s2 - 2) - (((s2 - 14) / 12) * 12)) * 367) / 12)) - (((((s + 4900) + ((s2 - 14) / 12)) / 100) * 3) / 4)) - 2415021;
    }

    public void packTime() {
        int i = this.hour * 1080000;
        this.time = i;
        int i2 = i + (this.minute * 18000);
        this.time = i2;
        int i3 = i2 + (this.second * 300);
        this.time = i3;
        int round = i3 + Math.round((((float) this.millis) * 300.0f) / 1000.0f);
        this.time = round;
        if (round > 25919999) {
            this.time = 0;
            this.hour = 0;
            this.minute = 0;
            this.second = 0;
            this.millis = 0;
            if (this.date != Integer.MIN_VALUE) {
                GregorianCalendar gregorianCalendar = new GregorianCalendar();
                gregorianCalendar.set(1, this.year);
                gregorianCalendar.set(2, this.month - 1);
                gregorianCalendar.set(5, this.day);
                gregorianCalendar.add(5, 1);
                this.year = (short) gregorianCalendar.get(1);
                this.month = (short) (gregorianCalendar.get(2) + 1);
                this.day = (short) gregorianCalendar.get(5);
                this.date++;
            }
        }
    }

    public Timestamp toTimestamp() {
        if (this.tsValue == null) {
            if (!this.unpacked) {
                unpackDateTime();
            }
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.set(1, this.year);
            gregorianCalendar.set(2, this.month - 1);
            gregorianCalendar.set(5, this.day);
            gregorianCalendar.set(11, this.hour);
            gregorianCalendar.set(12, this.minute);
            gregorianCalendar.set(13, this.second);
            gregorianCalendar.set(14, this.millis);
            this.tsValue = new Timestamp(gregorianCalendar.getTime().getTime());
        }
        return this.tsValue;
    }

    public Date toDate() {
        if (this.dateValue == null) {
            if (!this.unpacked) {
                unpackDateTime();
            }
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.set(1, this.year);
            gregorianCalendar.set(2, this.month - 1);
            gregorianCalendar.set(5, this.day);
            gregorianCalendar.set(11, 0);
            gregorianCalendar.set(12, 0);
            gregorianCalendar.set(13, 0);
            gregorianCalendar.set(14, 0);
            this.dateValue = new Date(gregorianCalendar.getTime().getTime());
        }
        return this.dateValue;
    }

    public Time toTime() {
        if (this.timeValue == null) {
            if (!this.unpacked) {
                unpackDateTime();
            }
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.set(1, 1970);
            gregorianCalendar.set(2, 0);
            gregorianCalendar.set(5, 1);
            gregorianCalendar.set(11, this.hour);
            gregorianCalendar.set(12, this.minute);
            gregorianCalendar.set(13, this.second);
            gregorianCalendar.set(14, this.millis);
            this.timeValue = new Time(gregorianCalendar.getTime().getTime());
        }
        return this.timeValue;
    }

    public Object toObject() {
        if (this.date == Integer.MIN_VALUE) {
            return toTime();
        }
        if (this.time == Integer.MIN_VALUE) {
            return toDate();
        }
        return toTimestamp();
    }

    public String toString() {
        int i;
        if (this.stringValue == null) {
            if (!this.unpacked) {
                unpackDateTime();
            }
            short s = this.day;
            short s2 = this.month;
            short s3 = this.year;
            short s4 = this.millis;
            short s5 = this.second;
            short s6 = this.minute;
            short s7 = this.hour;
            char[] cArr = new char[23];
            if (this.date != Integer.MIN_VALUE) {
                cArr[9] = (char) ((s % 10) + 48);
                cArr[8] = (char) (((s / 10) % 10) + 48);
                cArr[7] = '-';
                cArr[6] = (char) ((s2 % 10) + 48);
                cArr[5] = (char) (((s2 / 10) % 10) + 48);
                cArr[4] = '-';
                cArr[3] = (char) ((s3 % 10) + 48);
                int i2 = s3 / 10;
                cArr[2] = (char) ((i2 % 10) + 48);
                int i3 = i2 / 10;
                cArr[1] = (char) ((i3 % 10) + 48);
                cArr[0] = (char) (((i3 / 10) % 10) + 48);
                if (this.time != Integer.MIN_VALUE) {
                    i = 11;
                    cArr[10] = ' ';
                } else {
                    i = 10;
                }
            } else {
                i = 0;
            }
            if (this.time != Integer.MIN_VALUE) {
                int i4 = (i + 12) - 1;
                cArr[i4] = (char) ((s4 % 10) + 48);
                int i5 = s4 / 10;
                int i6 = i4 - 1;
                cArr[i6] = (char) ((i5 % 10) + 48);
                int i7 = i6 - 1;
                cArr[i7] = (char) (((i5 / 10) % 10) + 48);
                int i8 = i7 - 1;
                cArr[i8] = '.';
                int i9 = i8 - 1;
                cArr[i9] = (char) ((s5 % 10) + 48);
                int i10 = i9 - 1;
                cArr[i10] = (char) (((s5 / 10) % 10) + 48);
                int i11 = i10 - 1;
                cArr[i11] = ':';
                int i12 = i11 - 1;
                cArr[i12] = (char) ((s6 % 10) + 48);
                int i13 = i12 - 1;
                cArr[i13] = (char) (((s6 / 10) % 10) + 48);
                int i14 = i13 - 1;
                cArr[i14] = ':';
                int i15 = i14 - 1;
                cArr[i15] = (char) ((s7 % 10) + 48);
                int i16 = i15 - 1;
                cArr[i16] = (char) (((s7 / 10) % 10) + 48);
                int i17 = i16 + 12;
                if (cArr[i17 - 1] == '0') {
                    i17--;
                }
                if (cArr[i - 1] == '0') {
                    i--;
                }
            }
            this.stringValue = String.valueOf(cArr, 0, i);
        }
        return this.stringValue;
    }
}
