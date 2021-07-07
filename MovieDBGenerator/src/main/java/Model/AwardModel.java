package Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AwardModel {
    @SerializedName("awards")
    private List<String> awards;

    public List<String> getAwards() {
        return awards;
    }
}
