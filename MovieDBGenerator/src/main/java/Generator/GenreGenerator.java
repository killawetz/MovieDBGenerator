package Generator;

import Model.GenreModel;
import Util.DBConnector;
import Util.DBUtil;
import Util.MyAPICall;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class GenreGenerator extends RetrofitGenerator {

    private PreparedStatement queryInsertIntoGenre;
    private PreparedStatement checkQuery;
    private int insertedMovieCounter = 0;

    private static final String GET_COUNT_OF_ROWS_QUERY = "SELECT count(id) FROM genre";


    @Override
    public void generate(int countOfRows) {
        DBConnector.getDBConnection();
        final MyAPICall myAPICall = getAPI();
        Response<GenreModel> response;
        Call<GenreModel> call;

        List<GenreModel.Genre> genreList;

        call = myAPICall.getGenreData();
        int i = 0;

        try {
            response = call.execute();

            if (response.code() == 200) {
                genreList = response.body().getGenreList();
                int maxNumberOfGenre = genreList.size();
                int numberOfLines = checkNumberOfLines(countOfRows, maxNumberOfGenre);
                if(!DBUtil.isItPossibleToInsert(maxNumberOfGenre, GET_COUNT_OF_ROWS_QUERY) ||
                        countOfRows < 1) {
                    return;
                }

                while (insertedMovieCounter < numberOfLines && i < maxNumberOfGenre) {
                    insertIntoGenreTable(genreList.get(i).getName());
                    i++;
                }
            }
            closeConnections();
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public void closeConnections() throws SQLException {
        DBConnector.connection.close();
        queryInsertIntoGenre.close();
        checkQuery.close();
    }


    private void insertIntoGenreTable(String genreName) {
        try {
            checkQuery = DBConnector.connection.prepareStatement(
                    "SELECT EXISTS(SELECT genre_name FROM genre WHERE genre_name = ?)");
            if (DBUtil.isRowExist(checkQuery, genreName)) {
                return;
            }

            queryInsertIntoGenre = DBConnector.connection.prepareStatement(
                    "INSERT INTO public.genre(genre_name)" +
                            "VALUES(?)");

            queryInsertIntoGenre.setString(1, genreName);
            queryInsertIntoGenre.executeUpdate();

            insertedMovieCounter++;

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
