import Generator.*;

public class Main {

    public static void main(String[] args) {
        StudioGenerator studioGenerator = new StudioGenerator();
        FilmGenerator filmGenerator = new FilmGenerator();
        PersonGenerator personGenerator = new PersonGenerator();
        GenreGenerator genreGenerator = new GenreGenerator();
        CountryGenerator countryGenerator = new CountryGenerator();
        RoleGenerator roleGenerator = new RoleGenerator();
        AwardGenerator awardGenerator = new AwardGenerator();
        ScreenshotGenerator screenshotGenerator = new ScreenshotGenerator();
        GenerateAssociativeEntities generateAssociativeEntities = new GenerateAssociativeEntities();


        filmGenerator.generate(400);
        studioGenerator.generate(40);
        genreGenerator.generate(20);
        countryGenerator.generate(150);
        personGenerator.generate(800);
        awardGenerator.generate(15);
        roleGenerator.generate(12);
        screenshotGenerator.generate(250);

        generateAssociativeEntities.generateGenreFilm(290);
        generateAssociativeEntities.generateCountryFilm(320);
        generateAssociativeEntities.generateStudioFilm(250);
        generateAssociativeEntities.generatePersonInFilm(500);
        generateAssociativeEntities.generatePersonAward(80);

    }
}
