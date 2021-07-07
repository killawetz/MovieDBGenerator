package Generator;

import Util.DBConnector;
import Util.DBUtil;

import java.sql.*;

public class GenerateAssociativeEntities {

    PreparedStatement insertStatement;
    PreparedStatement checkStatement;
    private ResultSet randomFirstEntity;
    private ResultSet randomSecondEntity;
    private ResultSet randomThirdEntity;
    private Statement selectRandomRawFromTable;

    private int inputtedNumberOfRows;
    private int maxPossibleRows;
    private int currentCount;
    private int insertedRowsCounter;


    private final static String FILM_SELECT_QUERY = "SELECT id FROM film ORDER BY random() LIMIT 1";
    private final static String COUNTRY_SELECT_QUERY = "SELECT id FROM country ORDER BY random() LIMIT 1";
    private final static String GENRE_SELECT_QUERY = "SELECT id FROM genre ORDER BY random() LIMIT 1";
    private final static String PERSON_SELECT_QUERY = "SELECT id FROM person ORDER BY random() LIMIT 1";
    private final static String AWARD_SELECT_QUERY = "SELECT id FROM award ORDER BY random() LIMIT 1";
    private final static String ROLE_SELECT_QUERY = "SELECT id FROM role ORDER BY random() LIMIT 1";
    private final static String STUDIO_SELECT_QUERY = "SELECT id FROM studio ORDER BY random() LIMIT 1";
    private final static String PERSON_IN_FILM_SELECT_QUERY = "SELECT id FROM person_in_film ORDER BY random() LIMIT 1";

    private final static String FILM_SELECT_COUNT = "SELECT count(id) FROM film";


    public void generateGenreFilm(int numberOfRows) {
        DBConnector.getDBConnection();
        inputtedNumberOfRows = numberOfRows;

        final String selectCountGenres = "SELECT count(id) FROM genre";
        final String selectCurrentCount = "SELECT count(id) FROM genre_film";

        maxPossibleRows = DBUtil.getMaxPossibleRows(selectCountGenres, FILM_SELECT_COUNT);
        currentCount = DBUtil.getCountOfRows(selectCurrentCount);
        if (!DBUtil.isItPossibleToInsert(currentCount, maxPossibleRows) ||
                numberOfRows < 1) {
            return;
        }
        try {
            insertStatement = DBConnector.connection.prepareStatement("INSERT INTO " +
                    "public.genre_film(genre_id, film_id) " +
                    "VALUES (?, ?)");
            checkStatement = DBConnector.connection.prepareStatement("SELECT EXISTS(" +
                    "SELECT genre_id, film_id FROM genre_film WHERE genre_id = ? and film_id = ?)");

            while (inputtedNumberOfRows > 0 && insertedRowsCounter < maxPossibleRows - currentCount) {
                randomFirstEntity = getRandomID(GENRE_SELECT_QUERY);
                randomSecondEntity = getRandomID(FILM_SELECT_QUERY);

                insertIntoFilmAndEntity(insertStatement, checkStatement);

            }
            closeAllConnections();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public void generateCountryFilm(int numberOfRows) {
        DBConnector.getDBConnection();
        inputtedNumberOfRows = numberOfRows;


        final String selectCountCountry = "SELECT count(id) FROM country";
        final String selectCurrentCount = "SELECT count(id) FROM country_film";

        maxPossibleRows = DBUtil.getMaxPossibleRows(selectCountCountry, FILM_SELECT_COUNT);
        currentCount = DBUtil.getCountOfRows(selectCurrentCount);
        if (!DBUtil.isItPossibleToInsert(currentCount, maxPossibleRows) ||
                numberOfRows < 1) {
            return;
        }
        try {
            while (inputtedNumberOfRows > 0 && insertedRowsCounter < maxPossibleRows - currentCount) {
                randomFirstEntity = getRandomID(COUNTRY_SELECT_QUERY);
                randomSecondEntity = getRandomID(FILM_SELECT_QUERY);
                insertStatement = DBConnector.connection.prepareStatement("INSERT INTO " +
                        "public.country_film(country_id, film_id) " +
                        "VALUES (?, ?)");
                checkStatement = DBConnector.connection.prepareStatement("SELECT EXISTS(" +
                        "SELECT country_id, film_id FROM country_film WHERE country_id = ? and film_id = ?)");

                insertIntoFilmAndEntity(insertStatement, checkStatement);
            }
            closeAllConnections();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void generateStudioFilm(int numberOfRows) {
        DBConnector.getDBConnection();
        inputtedNumberOfRows = numberOfRows;

        final String selectCountStudio = "SELECT count(id) FROM studio";
        final String selectCurrentCount = "SELECT count(id) FROM studio_film";

        maxPossibleRows = DBUtil.getMaxPossibleRows(selectCountStudio, FILM_SELECT_COUNT);
        currentCount = DBUtil.getCountOfRows(selectCurrentCount);
        if (!DBUtil.isItPossibleToInsert(currentCount, maxPossibleRows) ||
                numberOfRows < 1) {
            return;
        }
        try {
            insertStatement = DBConnector.connection.prepareStatement(
                    "INSERT INTO public.studio_film(studio_id, film_id) " +
                            "VALUES (?, ?)");
            checkStatement = DBConnector.connection.prepareStatement("SELECT EXISTS(" +
                    "SELECT studio_id, film_id FROM studio_film WHERE studio_id = ? and film_id = ?)");

            while (inputtedNumberOfRows > 0 && insertedRowsCounter < maxPossibleRows - currentCount) {
                randomFirstEntity = getRandomID(STUDIO_SELECT_QUERY);
                randomSecondEntity = getRandomID(FILM_SELECT_QUERY);
                insertIntoFilmAndEntity(insertStatement, checkStatement);
            }
            closeAllConnections();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void generatePersonInFilm(int numberOfRows) {
        DBConnector.getDBConnection();
        inputtedNumberOfRows = numberOfRows;

        final String selectCountPerson = "SELECT count(id) FROM person";
        final String selectCountRole = "SELECT count(id) FROM role";
        final String selectCurrentCount = "SELECT count(id) FROM person_in_film";

        maxPossibleRows = DBUtil.getMaxPossibleRows(selectCountPerson,
                selectCountRole,
                FILM_SELECT_COUNT);
        currentCount = DBUtil.getCountOfRows(selectCurrentCount);
        if (!DBUtil.isItPossibleToInsert(currentCount, maxPossibleRows) ||
                numberOfRows < 1) {
            return;
        }
        try {
            insertStatement = DBConnector.connection.prepareStatement(
                    "INSERT INTO public.person_in_film(person_id, film_id, role_id) " +
                            "VALUES (?, ?, ?)");
            checkStatement = DBConnector.connection.prepareStatement("SELECT EXISTS(" +
                    "SELECT person_id, film_id, role_id FROM person_in_film " +
                    "WHERE person_id = ? and film_id = ? and role_id = ?)");

            while (inputtedNumberOfRows > 0 && insertedRowsCounter < maxPossibleRows - currentCount) {
                try {
                    randomFirstEntity = getRandomID(PERSON_SELECT_QUERY);
                    randomSecondEntity = getRandomID(FILM_SELECT_QUERY);
                    randomThirdEntity = getRandomID(ROLE_SELECT_QUERY);
                    insertIntoFilmAndEntity(insertStatement, checkStatement);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            closeAllConnections();
            randomThirdEntity.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    public void generatePersonAward(int numberOfRows) {
        DBConnector.getDBConnection();
        inputtedNumberOfRows = numberOfRows;

        final String selectCountPersonInFilm = "SELECT count(id) FROM person_in_film";
        final String selectCountAward = "SELECT count(id) FROM award";
        final String selectCurrentCount = "SELECT count(id) FROM person_award";

        maxPossibleRows = DBUtil.getMaxPossibleRows(selectCountPersonInFilm, selectCountAward);
        currentCount = DBUtil.getCountOfRows(selectCurrentCount);
        if (!DBUtil.isItPossibleToInsert(currentCount, maxPossibleRows) ||
                numberOfRows < 1) {
            return;
        }
        try {
            insertStatement = DBConnector.connection.prepareStatement(
                    "INSERT INTO public.person_award(person_in_film_id, award_id, is_received) " +
                            "VALUES (?, ?, random() > 0.5)");
            checkStatement = DBConnector.connection.prepareStatement("SELECT EXISTS(" +
                    "SELECT person_in_film_id, award_id FROM person_award " +
                    "WHERE person_in_film_id = ? and award_id = ?)");

            while (inputtedNumberOfRows > 0 && insertedRowsCounter < maxPossibleRows - currentCount) {
                randomFirstEntity = getRandomID(PERSON_IN_FILM_SELECT_QUERY);
                randomSecondEntity = getRandomID(AWARD_SELECT_QUERY);
                insertIntoFilmAndEntity(insertStatement, checkStatement);
            }
            closeAllConnections();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private ResultSet getRandomID(String entityQuery) throws SQLException {
        selectRandomRawFromTable = DBConnector.connection.createStatement();
        return selectRandomRawFromTable.executeQuery(entityQuery);

    }

    private void insertIntoFilmAndEntity(PreparedStatement insertQuery, PreparedStatement checkQuery) {
        try {
            int parameterCount = insertQuery.getParameterMetaData().getParameterCount();
            if (parameterCount == 2) {
                if (randomFirstEntity.next() && randomSecondEntity.next()) {
                    if (!checkExist(checkQuery, parameterCount)) {
                        insertQuery.setInt(1, randomFirstEntity.getInt("id"));
                        insertQuery.setInt(2, randomSecondEntity.getInt("id"));
                        insertQuery.executeUpdate();
                    } else {
                        return;
                    }
                }
            } else {
                if (randomFirstEntity.next() && randomSecondEntity.next() && randomThirdEntity.next()) {
                    if (!checkExist(checkQuery, parameterCount)) {
                        insertQuery.setInt(1, randomFirstEntity.getInt("id"));
                        insertQuery.setInt(2, randomSecondEntity.getInt("id"));
                        insertQuery.setInt(3, randomThirdEntity.getInt("id"));
                        insertQuery.executeUpdate();
                    } else {
                        return;
                    }
                }
            }
            inputtedNumberOfRows--;
            insertedRowsCounter++;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private boolean checkExist(PreparedStatement query, int parameterCount) {
        boolean isExist = false;
        try {
            switch (parameterCount) {
                case 2:
                    isExist = DBUtil.isRowExist(query,
                            randomFirstEntity.getInt("id"),
                            randomSecondEntity.getInt("id"));
                    break;
                case 3:
                    isExist = DBUtil.isRowExist(query,
                            randomFirstEntity.getInt("id"),
                            randomSecondEntity.getInt("id"),
                            randomThirdEntity.getInt("id"));
                    break;
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return isExist;
    }

    private void closeAllConnections() throws SQLException {
        selectRandomRawFromTable.close();
        randomFirstEntity.close();
        randomSecondEntity.close();
        DBConnector.connection.close();
        DBUtil.closeUtilConnection();
    }
}
