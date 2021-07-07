package Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CountryModel {

    @SerializedName("english_name")
    private String englishName;

    public String getEnglishName() {
        return englishName;
    }
}
