package Util;

import java.sql.*;

public class DBUtil {

    private static PreparedStatement selectExist;
    private static ResultSet existResultSet;

    public static int getCountOfRows(String query) {
        int count = 0;
        try {
            Statement selectCount = DBConnector.connection.createStatement();
            ResultSet resultCount = selectCount.executeQuery(query);
            if (resultCount.next()) {
                count = resultCount.getInt("count");
            }
            selectCount.close();
            resultCount.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return count;
    }

    public static int getMaxPossibleRows(String firstTable, String secondTable) {
        return getCountOfRows(firstTable) * getCountOfRows(secondTable);
    }

    public static int getMaxPossibleRows(String firstTable,
                                         String secondTable,
                                         String thirdTable) {
        return getCountOfRows(firstTable) *
                getCountOfRows(secondTable) *
                getCountOfRows(thirdTable);
    }


    public static boolean isRowExist(PreparedStatement query, String name) {
        boolean isExist = false;
        selectExist = query;
        try {
            selectExist.setString(1, name);

            existResultSet = selectExist.executeQuery();
            existResultSet.next();
            isExist = existResultSet.getBoolean("exists");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return isExist;
    }

    public static boolean isRowExist(PreparedStatement query, String name, int intValue) {
        boolean isExist = false;
        selectExist = query;
        try {
            selectExist.setString(1, name);
            selectExist.setInt(2, intValue);

            existResultSet = selectExist.executeQuery();
            existResultSet.next();
            isExist = existResultSet.getBoolean("exists");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return isExist;
    }

    public static boolean isRowExist(PreparedStatement query, String name, Date date) {
        boolean isExist = false;
        selectExist = query;
        try {
            selectExist.setString(1, name);
            selectExist.setDate(2, date);

            existResultSet = selectExist.executeQuery();
            existResultSet.next();
            isExist = existResultSet.getBoolean("exists");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return isExist;
    }

    public static boolean isRowExist(PreparedStatement query, int randomFirst, int randomSecond) {
        boolean isExist = false;
        selectExist = query;
        try {
            selectExist.setInt(1, randomFirst);
            selectExist.setInt(2, randomSecond);

            existResultSet = selectExist.executeQuery();
            existResultSet.next();
            isExist = existResultSet.getBoolean("exists");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return isExist;
    }

    public static boolean isRowExist(PreparedStatement query,
                                      int randomFirst,
                                      int randomSecond,
                                      int randomThird) {
        boolean isExist = false;
        selectExist = query;
        try {
            selectExist.setInt(1, randomFirst);
            selectExist.setInt(2, randomSecond);
            selectExist.setInt(3, randomThird);

            existResultSet = selectExist.executeQuery();
            existResultSet.next();
            isExist = existResultSet.getBoolean("exists");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return isExist;
    }

    public static void closeUtilConnection() throws SQLException {
        existResultSet.close();
        existResultSet.close();
    }

    public static boolean isItPossibleToInsert(int maxElements, String query) {
        if(maxElements == getCountOfRows(query)) {
            try {
                DBConnector.connection.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public static boolean isItPossibleToInsert(int currentCount, int maxPossibleRow){
        if(currentCount == maxPossibleRow){
            try {
                DBConnector.connection.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            return false;
        }
        return true;
    }

}
