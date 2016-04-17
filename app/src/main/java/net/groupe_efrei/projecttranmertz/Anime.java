package net.groupe_efrei.projecttranmertz;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Classe contenant les données d'une série
 * Created by Benoît on 09/04/2016.
 */
public class Anime implements Comparable<Anime>, Parcelable {
    private String id;//anime id dans la base de données de MyAnimeList
    private String title;//nom de l'Anime
    private String synonyms;//autre noms (le plus souvent dans d'autre langues)
    private String type;//le genre de diffusion : en série, en film...
    private String episode;//nombre d'épisodes
    private String series_status;//si la série est terminée, en cours ou en production
    private String series_start;//date de sortie
    private String series_end;///date de fin de diffusion
    private String series_image; //adresse url de l'image de la base de données
    private String my_id;//id de la série contenue dans la liste de l'utilisateur
    private String watched_episode;//nb d'épisodes regardés
    private String start_date;//début du visionnage
    private String finish_date;//fin du visionnage
    private String score;//note de l'utilisateur
    private String my_status; //statut de la série dans notre liste (en cours, fini...)
    private String rewatching;//nombre de revisionnage
    private String rewatching_ep;//nombre d'épisodes revus
    private String last_update;//date de dernière modification
    private String my_tags;//tags créés par l'utilisateur

    public Anime(Anime anime) {
        this.id = anime.getId();
        this.title = anime.getTitle();
        this.synonyms = anime.getSynonyms();
        this.type = anime.getType();
        this.episode = anime.getEpisode();
        this.series_status = anime.getSeries_status();
        this.series_start = anime.getSeries_start();
        this.series_end = anime.getSeries_end();
        this.series_image = anime.getSeries_image();
        this.my_id = anime.getMy_id();
        this.watched_episode = anime.getWatched_episode();
        this.start_date = anime.getStart_date();
        this.finish_date = anime.getFinish_date();
        this.score = anime.getScore();
        this.my_status = anime.getMy_status();
        this.rewatching = anime.getRewatching();
        this.rewatching_ep = anime.getRewatching_ep();
        this.last_update = anime.getLast_update();
        this.my_tags = anime.getMy_tags();
    }

    public Anime(String id, String title, String synonyms, String type, String episode, String series_status,
                 String series_start, String series_end, String series_image, String my_id, String watched_episode,
                 String start_date, String finish_date, String score, String my_status, String rewatching,
                 String rewatching_ep, String last_update, String my_tags) {
        this.id = id;
        this.title = title;
        this.synonyms = synonyms;
        this.type = type;
        this.episode = episode;
        this.series_status = series_status;
        this.series_start = series_start;
        this.series_end = series_end;
        this.series_image = series_image;
        this.my_id = my_id;
        this.watched_episode = watched_episode;
        this.start_date = start_date;
        this.finish_date = finish_date;
        if(score.equals("0"))
            this.score="-";
        else
            this.score = score;
        this.my_status = my_status;
        this.rewatching = rewatching;
        this.rewatching_ep = rewatching_ep;
        this.last_update = last_update;
        this.my_tags = my_tags;
    }


    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.id);
        out.writeString(this.title);
        out.writeString(this.synonyms);
        out.writeString(this.type);
        out.writeString(this.episode);
        out.writeString(this.series_status);
        out.writeString(this.series_start);
        out.writeString(this.series_end);
        out.writeString(this.series_image);
        out.writeString(this.my_id);
        out.writeString(this.watched_episode);
        out.writeString(this.start_date);
        out.writeString(this.finish_date);
        out.writeString(this.score);
        out.writeString(this.my_status);
        out.writeString(this.rewatching);
        out.writeString(this.rewatching_ep);
        out.writeString(this.last_update);
        out.writeString(this.my_tags);

    }

    public static final Parcelable.Creator<Anime> CREATOR
            = new Parcelable.Creator<Anime>() {
        public Anime createFromParcel(Parcel in) {
            return new Anime(in);
        }

        public Anime[] newArray(int size) {
            return new Anime[size];
        }
    };

    private Anime(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.synonyms = in.readString();
        this.type = in.readString();
        this.episode = in.readString();
        this.series_status = in.readString();
        this.series_start = in.readString();
        this.series_end = in.readString();
        this.series_image = in.readString();
        this.my_id = in.readString();
        this.watched_episode = in.readString();
        this.start_date = in.readString();
        this.finish_date = in.readString();
        this.score = in.readString();
        this.my_status = in.readString();
        this.rewatching = in.readString();
        this.rewatching_ep = in.readString();
        this.last_update = in.readString();
        this.my_tags = in.readString();
    }


    public String toString()
    {
        String result = new String();
        result = this.id +"\n" +
                this.title+"\n"+
        this.synonyms+"\n" +
        this.type+"\n" +
        this.episode+"\n" +
        this.series_status+"\n" +
        this.series_start+"\n" +
        this.series_end+"\n" +
        this.series_image+"\n" +
        this.my_id+"\n" +
        this.watched_episode+"\n" +
        this.start_date+"\n" +
        this.finish_date+"\n" +
        this.score+"\n" +
        this.my_status+"\n" +
        this.rewatching+"\n" +
        this.rewatching_ep+"\n" +
        this.last_update+"\n" +
        this.my_tags+"\n";
        return result;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSynonyms() {
        return synonyms;
    }

    public String getType() {return type;}

    public String getEpisode() {
        return episode;
    }

    public String getSeries_status() {
        return series_status;
    }

    public String getSeries_start() {
        return series_start;
    }

    public String getSeries_end() {
        return series_end;
    }

    public String getSeries_image() {
        return series_image;
    }

    public String getMy_id() {
        return my_id;
    }

    public String getWatched_episode() {
        return watched_episode;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getFinish_date() {
        return finish_date;
    }

    public String getScore() {
        return score;
    }

    public String getMy_status() {
        return my_status;
    }

    public String getRewatching() {
        return rewatching;
    }

    public String getRewatching_ep() {
        return rewatching_ep;
    }

    public String getLast_update() {
        return last_update;
    }

    public String getMy_tags() {
        return my_tags;
    }

    /**
     * Représente un chiffre
     * 1: TV
     * 2: OVA (Original Video Anime)
     * 3: Film
     * 4: Episode spécial
     * 5: ONA (Original Net Anime
     * @return
     */
    public String getTypeAnime(){
        if(this.type.equals("1")){
            return "TV";
        }
        if(this.type.equals("2")){
            return "OVA";
        }
        if(this.type.equals("3")){
            return "Film";
        }
        if(this.type.equals("4")){
            return "Special";
        }
        if(this.type.equals("5")){
            return "ONA";
        }
        return "";
    }

    /**
     * Représente un chiffre
     * 1: Currently airing
     * 2: Finished airing
     * 3: Not yet aired
     * @return la chaîne de caractère décrivant si l'anime est terminé ou non
     */
    public String getStatusAnime()
    {
        if(this.series_status.equals("1")){
            return "Currently airing";
        }
        if(this.series_status.equals("2")){
            return "Finished airing";
        }
        if(this.series_status.equals("3")){
            return "Not yet aired";
        }
        return "";
    }

    /**
     * Représente un chiffre
     * 1: Currently Watching
     * 2: Completed
     * 3: On Hold
     * 6: Plan to watch
     * @return la chaîne de caractère décrivant où en est l'utilisateur sur le visionnage
     */
    public String getMyStatusAnime()
    {
        if(this.my_status.equals("1")){
            return "Currently Watching";
        }
        if(this.my_status.equals("2")){
            return "Completed";
        }
        if(this.my_status.equals("3")){
            return "On Hold";
        }
        if(this.my_status.equals("6")){
            return "Plan to watch";
        }
        return null;
    }

    /**
     * Affichage exemple: Jan 01, 1990
     * @param date
     * @return
     */
    public String getDate(String date)
    {
        String result = "?";
        //Log.d("JFL", "Date : " + date+ "\n");

        //Log.d("JFL", "Date : " + date.substring(5,7) + "\n");
        if(!date.substring(5,7).equals("00"))//mois
            result=getMonth(date.substring(5, 7)) + " ";

        //Log.d("JFL", "Date : " + date.substring(9,10) + "\n");
        if(!date.substring(8,10).equals("00"))//jour
            result+=date.substring(8,10) + ", ";

        //Log.d("JFL", "Date : " + date.substring(0,4) + "\n");
        if(!date.substring(0,4).equals("0000"))//année
            result+=date.substring(0,4);

        return result;
    }

    public String getMonth(String month)
    {
        if(month.equals("01"))
            return "Jan";
        if(month.equals("02"))
            return "Feb";
        if(month.equals("03"))
            return "Mar";
        if(month.equals("04"))
            return "Apr";
        if(month.equals("05"))
            return "May";
        if(month.equals("06"))
            return "Jun";
        if(month.equals("07"))
            return "Jul";
        if(month.equals("08"))
            return "Aug";
        if(month.equals("09"))
            return "Sep";
        if(month.equals("10"))
            return "Oct";
        if(month.equals("11"))
            return "Nov";
        if(month.equals("12"))
            return "Dec";
        return "";
    }

    public int compareTo(Anime a)
    {
        int lastCmp = this.title.compareTo(a.getTitle());
        return (lastCmp != 0 ? lastCmp : this.id.compareTo(a.getId()) );
    }
}
