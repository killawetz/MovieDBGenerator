package Model;

import com.google.gson.annotations.SerializedName;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;

public class PersonModel {

    final private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private java.util.Date utilDate;

    @SerializedName("birthday")
    private String birthday;

    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }


    public Date getBirthday() {
        if (birthday == null) {
            return getRandomDate();
        }
        return Date.valueOf(LocalDate.parse(birthday));
    }

    private Date getRandomDate() {
        long minDay = LocalDate.of(1970, 1, 1).toEpochDay();
        long maxDay = LocalDate.of(2015, 12, 31).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
        LocalDate randomDate = LocalDate.ofEpochDay(randomDay);

        return Date.valueOf(randomDate);

    }
}
