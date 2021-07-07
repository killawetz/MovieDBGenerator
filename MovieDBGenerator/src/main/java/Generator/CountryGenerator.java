package Generator;


import Model.CountryModel;
import Util.DBConnector;
import Util.DBUtil;
import Util.MyAPICall;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CountryGenerator extends RetrofitGenerator {

    private PreparedStatement queryInsertIntoCountry;
    private PreparedStatement checkQuery;
    private int insertedMovieCounter = 0;

    private static final String GET_COUNT_OF_ROWS_QUERY = "SELECT count(id) FROM country";


    @Override
    public void generate(int countOfRows) {
        DBConnector.getDBConnection();
        MyAPICall myAPICall = getAPI();
        Response<List<CountryModel>> response;
        Call<List<CountryModel>> call;

        List<CountryModel> countryList;

        call = myAPICall.getCountryData();
        int i = 0;

        try {
            response = call.execute();

            if (response.code() == 200) {
                countryList = response.body();
                int maxNumberOfCountry = countryList.size();
                int numberOfLines = checkNumberOfLines(countOfRows, maxNumberOfCountry);

                if (!DBUtil.isItPossibleToInsert(
                        maxNumberOfCountry - 1, GET_COUNT_OF_ROWS_QUERY) ||
                        countOfRows < 1) {
                    return;
                }

                while (insertedMovieCounter < numberOfLines && i < maxNumberOfCountry) {
                    insertIntoCountryTable(countryList.get(i).getEnglishName());
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
        queryInsertIntoCountry.close();
        checkQuery.close();
    }


    private void insertIntoCountryTable(String countryName) {
        try {
            checkQuery = DBConnector.connection.prepareStatement(
                    "SELECT EXISTS(SELECT country_name FROM country WHERE country_name = ?)");
            if (DBUtil.isRowExist(checkQuery, countryName)) {
                return;
            }

            queryInsertIntoCountry = DBConnector.connection.prepareStatement(
                    "INSERT INTO public.country(country_name)" +
                            "VALUES(?)");

            queryInsertIntoCountry.setString(1, countryName);
            queryInsertIntoCountry.executeUpdate();

            insertedMovieCounter++;
        } catch (SQLException throwables) {
            throwables.printStackTrace();

        }
    }


}
