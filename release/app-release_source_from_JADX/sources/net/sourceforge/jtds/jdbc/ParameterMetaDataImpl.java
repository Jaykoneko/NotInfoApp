package net.sourceforge.jtds.jdbc;

import java.sql.ParameterMetaData;
import java.sql.SQLException;

public class ParameterMetaDataImpl implements ParameterMetaData {
    private final int maxPrecision;
    private final ParamInfo[] parameterList;
    private final boolean useLOBs;

    public int isNullable(int i) throws SQLException {
        return 2;
    }

    public ParameterMetaDataImpl(ParamInfo[] paramInfoArr, JtdsConnection jtdsConnection) {
        if (paramInfoArr == null) {
            paramInfoArr = new ParamInfo[0];
        }
        this.parameterList = paramInfoArr;
        this.maxPrecision = jtdsConnection.getMaxPrecision();
        this.useLOBs = jtdsConnection.getUseLOBs();
    }

    public int getParameterCount() throws SQLException {
        return this.parameterList.length;
    }

    public int getParameterType(int i) throws SQLException {
        if (this.useLOBs) {
            return getParameter(i).jdbcType;
        }
        return Support.convertLOBType(getParameter(i).jdbcType);
    }

    public int getScale(int i) throws SQLException {
        ParamInfo parameter = getParameter(i);
        if (parameter.scale >= 0) {
            return parameter.scale;
        }
        return 0;
    }

    public boolean isSigned(int i) throws SQLException {
        int i2 = getParameter(i).jdbcType;
        if (i2 != -5) {
            switch (i2) {
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public int getPrecision(int i) throws SQLException {
        ParamInfo parameter = getParameter(i);
        return parameter.precision >= 0 ? parameter.precision : this.maxPrecision;
    }

    public String getParameterTypeName(int i) throws SQLException {
        return getParameter(i).sqlType;
    }

    public String getParameterClassName(int i) throws SQLException {
        return Support.getClassName(getParameterType(i));
    }

    public int getParameterMode(int i) throws SQLException {
        ParamInfo parameter = getParameter(i);
        if (!parameter.isOutput) {
            return parameter.isSet ? 1 : 0;
        }
        return parameter.isSet ? 2 : 4;
    }

    private ParamInfo getParameter(int i) throws SQLException {
        if (i >= 1) {
            ParamInfo[] paramInfoArr = this.parameterList;
            if (i <= paramInfoArr.length) {
                return paramInfoArr[i - 1];
            }
        }
        throw new SQLException(Messages.get("error.prepare.paramindex", (Object) Integer.toString(i)), "07009");
    }

    public boolean isWrapperFor(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }

    public Object unwrap(Class cls) throws SQLException {
        throw new AbstractMethodError();
    }
}
