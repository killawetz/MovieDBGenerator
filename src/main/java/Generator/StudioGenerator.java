package Generator;


import Model.StudioModel;
import Util.DBConnector;
import Util.DBUtil;
import Util.MyAPICall;
import retrofit2.Call;
import retrofit2.Response;


import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StudioGenerator extends RetrofitGenerator {

    private PreparedStatement queryInsertIntoStudio;
    private PreparedStatement checkQuery;
    private int insertedMovieCounter = 0;

    private static final String GET_COUNT_OF_ROWS_QUERY = "SELECT count(id) FROM studio";
    private static final int MAX_NUMBER_OF_STUDIOS = 1000;


    @Override
    public void generate(int countOfRows) {
        DBConnector.getDBConnection();

        if(!DBUtil.isItPossibleToInsert(MAX_NUMBER_OF_STUDIOS, GET_COUNT_OF_ROWS_QUERY) ||
                countOfRows < 1) {
            return;
        }

        final MyAPICall myAPICall = getAPI();
        Response<StudioModel> response;
        Call<StudioModel> call;

        String studioName;



        int numberOfLines = checkNumberOfLines(countOfRows, MAX_NUMBER_OF_STUDIOS);
        int i = DBUtil.getCountOfRows(GET_COUNT_OF_ROWS_QUERY);

        try {
            while (insertedMovieCounter < numberOfLines) {
                call = myAPICall.getStudioData(i);
                response = call.execute();

                if (response.code() == 200) {
                    studioName = response.body().getName();
                    insertIntoStudioTable(studioName);
                }
                i++;
            }
            closeConnections();
        } catch (IOException | SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void closeConnections() throws SQLException {
        DBConnector.connection.close();
        queryInsertIntoStudio.close();
        checkQuery.close();
    }


    private void insertIntoStudioTable(String studioName) {
        try {
            checkQuery = DBConnector.connection.prepareStatement(
                    "SELECT EXISTS(SELECT studio_name FROM studio WHERE studio_name = ?)");

            if (DBUtil.isRowExist(checkQuery, studioName)) {
                return;
            }

            queryInsertIntoStudio = DBConnector.connection.prepareStatement(
                    "INSERT INTO public.studio(studio_name)" +
                            "VALUES(?)");

            queryInsertIntoStudio.setString(1, studioName);
            queryInsertIntoStudio.executeUpdate();

            insertedMovieCounter++;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


}
