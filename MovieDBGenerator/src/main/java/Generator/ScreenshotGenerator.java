package Generator;

import Model.FilmModel;
import Util.DBConnector;
import Util.DBUtil;
import Util.MyAPICall;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.sql.*;

public class ScreenshotGenerator extends RetrofitGenerator {

    private PreparedStatement queryInsertIntoScreen;
    private PreparedStatement checkQuery;
    private Statement randomFilmQuery;
    private ResultSet randomFilmResultSet;
    private int insertedMovieCounter = 0;

    private static final String GET_COUNT_OF_ROWS_QUERY = "SELECT count(id) FROM film";


    @Override
    public void generate(int countOfRows) {
        DBConnector.getDBConnection();

        if(!DBUtil.isItPossibleToInsert(
                FilmGenerator.MAX_NUMBER_OF_MOVIE, GET_COUNT_OF_ROWS_QUERY) ||
                countOfRows < 1) {
            return;
        }

        final MyAPICall myAPICall = getAPI();
        Response<FilmModel> response;
        Call<FilmModel> call;

        String screenUrl;

        int numberOfLines = checkNumberOfLines(countOfRows, FilmGenerator.MAX_NUMBER_OF_MOVIE);
        int i = DBUtil.getCountOfRows(GET_COUNT_OF_ROWS_QUERY);

        try {
            while (insertedMovieCounter < numberOfLines) {
                call = myAPICall.getMovieData(i);
                response = call.execute();
                if (response.code() == 200) {
                    screenUrl = response.body().getScreenshot();
                    insertIntoScreenshotTable(screenUrl);
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
        queryInsertIntoScreen.close();
        checkQuery.close();
        randomFilmResultSet.close();
        randomFilmQuery.close();
    }

    private void insertIntoScreenshotTable(String url) {
        try {
            int randomFilmId = 0;
            randomFilmQuery = DBConnector.connection.createStatement();
            randomFilmResultSet = randomFilmQuery.executeQuery(
                    "SELECT id FROM film ORDER BY random() LIMIT 1");

            if (randomFilmResultSet.next()) {
                randomFilmId = randomFilmResultSet.getInt("id");
            }

            checkQuery = DBConnector.connection.prepareStatement(
                    "SELECT EXISTS(" +
                            "SELECT url FROM screenshot WHERE url = ?)");

            if (DBUtil.isRowExist(checkQuery, url)) {
                return;
            }

            queryInsertIntoScreen = DBConnector.connection.prepareStatement("INSERT INTO " +
                    "public.screenshot(url, film_id) " +
                    "VALUES (?, ?)");

            queryInsertIntoScreen.setString(1, url);
            queryInsertIntoScreen.setInt(2, randomFilmId);


            queryInsertIntoScreen.executeUpdate();

            insertedMovieCounter++;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
