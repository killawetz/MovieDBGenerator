package Generator;

import Model.PersonModel;
import Util.DBConnector;
import Util.DBUtil;
import Util.MyAPICall;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.sql.*;

public class PersonGenerator extends RetrofitGenerator {

    private Statement randomCountryQuery;
    private ResultSet randomCountryResultSet;
    private PreparedStatement queryInsertIntoPerson;

    /* max number of people in themoviedb is 2,073,352
    опционально значение MAX_NUMBER_OF_PERSON может быть измененно */
    private static final int MAX_NUMBER_OF_PERSON = 4000;
    private static final String GET_COUNT_OF_ROWS_QUERY = "SELECT count(id) FROM person";
    private PreparedStatement checkQuery;
    int insertedMovieCounter = 0;

    @Override
    public void generate(int countOfRows) {
        DBConnector.getDBConnection();

        if(!DBUtil.isItPossibleToInsert(MAX_NUMBER_OF_PERSON, GET_COUNT_OF_ROWS_QUERY) ||
                countOfRows < 1) {
            return;
        }

        final MyAPICall myAPICall = getAPI();
        Response<PersonModel> response;
        Call<PersonModel> call;

        String personName;
        Date birthday;

        int numberOfLines = checkNumberOfLines(countOfRows, MAX_NUMBER_OF_PERSON);
        int i = DBUtil.getCountOfRows(GET_COUNT_OF_ROWS_QUERY);

        try {
            while (insertedMovieCounter < numberOfLines) {
                call = myAPICall.getPersonData(i);
                response = call.execute();

                if (response.code() == 200) {
                    personName = response.body().getName();
                    birthday = response.body().getBirthday();

                    insertIntoPersonTable(personName, birthday);
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
        randomCountryQuery.close();
        randomCountryResultSet.close();
        queryInsertIntoPerson.close();
    }

    private void insertIntoPersonTable(String name, Date birthday) {
        try {
            randomCountryQuery = DBConnector.connection.createStatement();
            randomCountryResultSet = randomCountryQuery.executeQuery(
                    "SELECT id FROM country ORDER BY random() LIMIT 1");

            checkQuery = DBConnector.connection.prepareStatement(
                    "SELECT EXISTS(" +
                            "SELECT name, date_of_birth FROM person" +
                            " WHERE name = ? and date_of_birth = ?)");

            if (DBUtil.isRowExist(checkQuery, name, birthday)) {
                return;
            }
            queryInsertIntoPerson = DBConnector.connection.prepareStatement("INSERT INTO " +
                    "public.person(name, country_of_birth, date_of_birth) " +
                    "VALUES (?, ?, ?)");

            if (randomCountryResultSet.next()) {
                queryInsertIntoPerson.setString(1, name);
                queryInsertIntoPerson.setInt(2, randomCountryResultSet.getInt("id"));
                queryInsertIntoPerson.setDate(3, birthday);
            }

            queryInsertIntoPerson.executeUpdate();
            insertedMovieCounter++;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
