package Generator;

import Model.FilmModel;
import Util.DBConnector;
import Util.DBUtil;
import Util.MyAPICall;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.sql.*;

public class FilmGenerator extends RetrofitGenerator {

    private PreparedStatement queryInsertIntoFilm;
    private PreparedStatement checkQuery;
    private int insertedMovieCounter;

    private static final String GET_COUNT_OF_ROWS_QUERY = "SELECT count(id) FROM film";
    /* max number of movies in themoviedb is 800.000
    опционально значение MAX_NUMBER_OF_MOVIE может быть измененно */
    public static final int MAX_NUMBER_OF_MOVIE = 1000;

    @Override
    public void generate(int countOfRows) {
        DBConnector.getDBConnection();

        if(!DBUtil.isItPossibleToInsert(MAX_NUMBER_OF_MOVIE, GET_COUNT_OF_ROWS_QUERY) ||
                countOfRows < 1) {
            return;
        }

        final MyAPICall myAPICall = getAPI();
        Response<FilmModel> response;
        Call<FilmModel> call;


        String filmName;
        String filmDescription;
        int filmBudget;
        int filmYear;
        int filmRuntime;

        int numberOfLines = checkNumberOfLines(countOfRows, MAX_NUMBER_OF_MOVIE);
        int i = DBUtil.getCountOfRows(GET_COUNT_OF_ROWS_QUERY);

        try {
            while (insertedMovieCounter < numberOfLines) {
                call = myAPICall.getMovieData(i);
                response = call.execute();
                if (response.code() == 200) {

                    filmName = response.body().getTitle();
                    filmDescription = response.body().getDescription();
                    filmBudget = response.body().getBudget();
                    filmYear = response.body().getReleaseDate();
                    filmRuntime = response.body().getRuntime();

                    insertIntoFilmTable(filmName, filmDescription, filmBudget, filmYear, filmRuntime);
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
        checkQuery.close();
        queryInsertIntoFilm.close();
    }


    private void insertIntoFilmTable(String name, String description, int budget, int year, int runtime) {
        try {
            checkQuery = DBConnector.connection.prepareStatement(
                    "SELECT EXISTS(SELECT name, year FROM film WHERE name = ? and year = ?)");

            if(DBUtil.isRowExist(checkQuery, name, year)) { return; }

            queryInsertIntoFilm = DBConnector.connection.prepareStatement(
                    "INSERT INTO public.film(name, description, budget, year, runtime)" +
                            " VALUES(?, ?, ?, ?, ?)");

            queryInsertIntoFilm.setString(1, name);
            queryInsertIntoFilm.setString(2, description);
            queryInsertIntoFilm.setInt(3, budget);
            queryInsertIntoFilm.setInt(4, year);
            queryInsertIntoFilm.setInt(5, runtime);

            queryInsertIntoFilm.executeUpdate();

            insertedMovieCounter++;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
