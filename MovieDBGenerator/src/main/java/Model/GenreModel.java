package Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GenreModel {


    @SerializedName("genres")
    private List<Genre> genreList;

    public List<Genre> getGenreList() {
        return genreList;
    }


    public class Genre {

        @SerializedName("id")
        private int id;
        @SerializedName("name")
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}

