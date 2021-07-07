package Generator;

import Model.RoleModel;
import Util.DBConnector;
import Util.DBUtil;
import Util.MyAPICall;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class RoleGenerator extends RetrofitGenerator {

    private PreparedStatement queryInsertIntoRole;
    private PreparedStatement checkQuery;
    private Call<List<RoleModel>> call;

    private String[] roleArray;
    private int insertedMovieCounter = 0;

    private static final String GET_COUNT_OF_ROWS_QUERY = "SELECT count(id) FROM role";

    @Override
    public void generate(int countOfRows) {
        DBConnector.getDBConnection();

        fillRoleArray();

        int maxNumberOfRole = roleArray.length;
        if(!DBUtil.isItPossibleToInsert(maxNumberOfRole, GET_COUNT_OF_ROWS_QUERY) ||
                countOfRows < 1) {
            return;
        }

        int i = 0;
        int numberOfLines = checkNumberOfLines(countOfRows, maxNumberOfRole);

        while (insertedMovieCounter < numberOfLines && i < maxNumberOfRole) {
            insertIntoRoleTable(roleArray[i]);
            i++;
        }

        try {
            closeConnections();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public void closeConnections() throws SQLException {
        DBConnector.connection.close();
        queryInsertIntoRole.close();
        checkQuery.close();
    }

    private void fillRoleArray() {
        final MyAPICall myAPICall = getAPI();
        Response<List<RoleModel>> response;
        call = myAPICall.getRoleData();
        List<RoleModel> roleModelList;

        try {
            response = call.execute();
            if (response.code() == 200) {
                roleModelList = response.body();

                int sizeList = roleModelList.size();
                roleArray = new String[sizeList];
                for (int i = 0; i < sizeList; i++) {
                    roleArray[i] = roleModelList.get(i).getDepartment();
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }



    private void insertIntoRoleTable(String roleName) {
        try {
            checkQuery = DBConnector.connection.prepareStatement(
                    "SELECT EXISTS(SELECT position FROM role WHERE position = ?)");
            if (DBUtil.isRowExist(checkQuery, roleName)) {
                return;
            }

            queryInsertIntoRole = DBConnector.connection.prepareStatement(
                    "INSERT INTO public.role(position)" +
                            "VALUES(?)");

            queryInsertIntoRole.setString(1, roleName);
            queryInsertIntoRole.executeUpdate();

            insertedMovieCounter++;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
