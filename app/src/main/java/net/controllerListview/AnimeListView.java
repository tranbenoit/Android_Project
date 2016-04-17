package net.controllerListview;

/**
 * Created by Benoît on 10/04/2016.
 */
public class AnimeListView {
    private String nomAnime;
    private String nbEpisode;
    private String score;
    private String image;
    private String userStatus;

    public AnimeListView(String nomAnime, String nbEpisode, String score, String image, String userStatus) {
        this.nomAnime = nomAnime;
        this.nbEpisode = nbEpisode;
        if(score!=null)//Si l'utilisateur a mis un score pour la série, on l'affiche avec la note sur 10
            this.score = score + "/10";
        else //s'il n'a rien mis, le champ est instancié à null et on aura la valeur par défaut du TextView (-/10)
            this.score = score;
        this.image = image;
        this.userStatus = userStatus;

    }

    public String getNomAnime() {
        return nomAnime;
    }

    public String getNbEpisode() {
        return nbEpisode;
    }

    public String getScore() {
        return score;
    }

    public String getImage() {
        return image;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public void setNomAnime(String nomAnime) {
        this.nomAnime = nomAnime;
    }

    public void setNbEpisode(String nbEpisode) {
        this.nbEpisode = nbEpisode;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
