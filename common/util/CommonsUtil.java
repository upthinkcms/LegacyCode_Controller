// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.util;

import java.util.Map;
import java.sql.SQLException;
import java.sql.ResultSet;

public class CommonsUtil
{
    public static int getDBInt(final ResultSet resultSet, final String columnName, final int nullValue) throws SQLException {
        int colValue = resultSet.getInt(columnName);
        if (resultSet.wasNull()) {
            colValue = nullValue;
        }
        return colValue;
    }
    
    public static int getDBInt(final Map resultSet, final String columnName, final int nullValue) throws SQLException {
        Integer colValue = resultSet.get(columnName);
        if (colValue == null) {
            colValue = nullValue;
        }
        return colValue;
    }
    
    public static String trimWhitespace(final String stringToTrim) {
        int endIndex = stringToTrim.length();
        if (endIndex == 0) {
            return stringToTrim;
        }
        int firstIndex = -1;
        while (++firstIndex < endIndex) {
            if (!Character.isWhitespace(stringToTrim.charAt(firstIndex))) {
                if ((short)stringToTrim.charAt(firstIndex) == 160) {
                    continue;
                }
                break;
            }
        }
        while (--endIndex > firstIndex) {
            if (!Character.isWhitespace(stringToTrim.charAt(endIndex))) {
                if ((short)stringToTrim.charAt(endIndex) == 160) {
                    continue;
                }
                break;
            }
        }
        return stringToTrim.substring(firstIndex, endIndex + 1);
    }
}
