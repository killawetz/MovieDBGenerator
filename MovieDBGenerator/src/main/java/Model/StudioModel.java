package Model;

import com.google.gson.annotations.SerializedName;

public class StudioModel {
    @SerializedName("name")
    private String name;

    public String getName() {
        return name;
    }
}
