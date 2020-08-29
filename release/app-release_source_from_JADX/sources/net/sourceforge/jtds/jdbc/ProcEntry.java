package net.sourceforge.jtds.jdbc;

public class ProcEntry {
    public static final int CURSOR = 3;
    public static final int PREPARE = 2;
    public static final int PREP_FAILED = 4;
    public static final int PROCEDURE = 1;
    private ColInfo[] colMetaData;
    private String name;
    private ParamInfo[] paramMetaData;
    private int refCount;
    private int type;

    public final String toString() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setHandle(int i) {
        this.name = Integer.toString(i);
    }

    public ColInfo[] getColMetaData() {
        return this.colMetaData;
    }

    public void setColMetaData(ColInfo[] colInfoArr) {
        this.colMetaData = colInfoArr;
    }

    public ParamInfo[] getParamMetaData() {
        return this.paramMetaData;
    }

    public void setParamMetaData(ParamInfo[] paramInfoArr) {
        this.paramMetaData = paramInfoArr;
    }

    public void setType(int i) {
        this.type = i;
    }

    public int getType() {
        return this.type;
    }

    public void appendDropSQL(StringBuilder sb) {
        int i = this.type;
        if (i == 1) {
            sb.append("DROP PROC ");
            sb.append(this.name);
            sb.append(10);
        } else if (i == 2) {
            sb.append("EXEC sp_unprepare ");
            sb.append(this.name);
            sb.append(10);
        } else if (i == 3) {
            sb.append("EXEC sp_cursorunprepare ");
            sb.append(this.name);
            sb.append(10);
        } else if (i != 4) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Invalid cached statement type ");
            sb2.append(this.type);
            throw new IllegalStateException(sb2.toString());
        }
    }

    public void addRef() {
        this.refCount++;
    }

    public void release() {
        int i = this.refCount;
        if (i > 0) {
            this.refCount = i - 1;
        }
    }

    public int getRefCount() {
        return this.refCount;
    }
}
