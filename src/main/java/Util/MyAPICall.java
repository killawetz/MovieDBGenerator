package Util;

import Model.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface MyAPICall {

    //https://api.themoviedb.org/ 550?api_key=a3d52b90a5fc9f0d72a39591b5ff2304&language=en-US

    @GET("movie/{movie_id}?api_key=a3d52b90a5fc9f0d72a39591b5ff2304&language=en-US")
    Call<FilmModel> getMovieData(@Path("movie_id") int id);

    @GET("person/{person_id}?api_key=a3d52b90a5fc9f0d72a39591b5ff2304&language=en-US")
    Call<PersonModel> getPersonData(@Path("person_id") int id);

    @GET("company/{company_id}?api_key=a3d52b90a5fc9f0d72a39591b5ff2304")
    Call<StudioModel> getStudioData();

    @GET("genre/movie/list?api_key=a3d52b90a5fc9f0d72a39591b5ff2304&language=en-US")
    Call<GenreModel> getGenreData();

    @GET("configuration/countries?api_key=a3d52b90a5fc9f0d72a39591b5ff2304")
    Call<List<CountryModel>> getCountryData();

    @GET("configuration/jobs?api_key=a3d52b90a5fc9f0d72a39591b5ff2304")
    Call<List<RoleModel>> getRoleData();
}


