package Generator;

import Model.AwardModel;
import Util.DBConnector;
import Util.DBUtil;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AwardGenerator extends RetrofitGenerator {

    private PreparedStatement queryInsertIntoAward;
    private PreparedStatement checkQuery;
    private int insertedMovieCounter = 0;

    private static final String GET_COUNT_OF_ROWS_QUERY = "SELECT count(id) FROM award";

    @Override
    public void generate(int countOfRows) {
        DBConnector.getDBConnection();

        Gson gson = new Gson();
        AwardModel awards;
        int i = 0;

        try {
            awards = gson.fromJson(
                    new FileReader("src\\main\\data\\awards.json"),
                    AwardModel.class);
            int maxNumberOfAwards = awards.getAwards().size();
            int numberOfLines = checkNumberOfLines(countOfRows, maxNumberOfAwards);

            if(!DBUtil.isItPossibleToInsert(maxNumberOfAwards, GET_COUNT_OF_ROWS_QUERY) ||
                    countOfRows < 1) {
                return;
            }

            while (insertedMovieCounter < numberOfLines && i < maxNumberOfAwards) {
                insertIntoAwardTable(awards.getAwards().get(i));
                i++;
            }
            closeConnections();
        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnections() throws SQLException {
        DBConnector.connection.close();
        queryInsertIntoAward.close();
        checkQuery.close();
    }


    private void insertIntoAwardTable(String awardName) {
        try {
            checkQuery = DBConnector.connection.prepareStatement(
                    "SELECT EXISTS(SELECT award_name FROM award WHERE award_name = ?)");
            if (DBUtil.isRowExist(checkQuery, awardName)) {
                return;
            }

            queryInsertIntoAward = DBConnector.connection.prepareStatement(
                    "INSERT INTO public.award(award_name)" +
                            "VALUES(?)");

            queryInsertIntoAward.setString(1, awardName);
            queryInsertIntoAward.executeUpdate();

            insertedMovieCounter++;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
