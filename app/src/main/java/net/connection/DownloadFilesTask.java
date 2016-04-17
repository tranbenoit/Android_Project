package net.connection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;

import net.groupe_efrei.projecttranmertz.Anime;
import net.groupe_efrei.projecttranmertz.User;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe Java qui effectue la requête GET pour récupérer les données XML d'un utilisateur donné sur le site MyAnimeList
 * Created by groupe-efrei on 08/04/16.
 */
public class DownloadFilesTask extends AsyncTask<String, Void, String> {
    private String userAnime = null; //Membre privé qui contient le nom de l'utilisateur recherché
    private ArrayList<Anime> listeAnimes;//liste d'objets représentant la liste de séries de l'utilisateur
    private Context context;//contexte de l'activité depuis laquelle on tente d'accéder à la liste d'un utilisateur

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    public DownloadFilesTask(Context context, String getUser){//Constructeur
        this.context=context;
        this.userAnime = getUser;
    }

    /**
     * S'exécute en premier lorsque l'on exécute l'AsyncTask
     * @param urls
     * @return
     */
    @Override
    protected String doInBackground(String... urls) {
        try{
            return downloadUrl(urls[0]);//La connexion s'est bien effectuée, on télécharge
        } catch (IOException e) {//si adresse URL inexistante, on affiche une erreur
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    protected void onProgressUpdate() {
        return;
    }

    /**
     * Reçoit le String renvoyé par la méthode doInBackground.
     * Tri la liste d'Animes par ordre alphabétique
     * On envoie la liste d'objets dans un intent en tant qu'ArrayList Parcelable. Ainsi on peut envoyer une liste de n'importe quel objet
     * Si l'utilisateur a appuyé sur le bouton de recherche sans rentrer de caractère, on n'ajoute pas à l'intent le String contenant le nom d'utilisateur (qui est null)
     * Si l'utilisateur a renseigné un nom, on l'ajoute à l'intent
     * @param res
     */
    @Override
    protected void onPostExecute(String res) {
        this.userAnime = getUserName(res);//on récupère le nom de l'utilisateur stocké dans la base de données
        this.listeAnimes = new ArrayList<Anime>();
        this.listeAnimes = createAnimeList(res); //on crée un à un les objets Anime et on les stocke dans une liste depuis le flux de données reçu par la requête GET
        arrangerOrdreAlphabetique();//on les met en ordre alphabétique


        Intent intent = new Intent(context, User.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle mBundle = new Bundle();
        mBundle.putParcelableArrayList("data", this.listeAnimes);
        intent.putExtras(mBundle);
        if(this.userAnime != null)//on met le nom de l'utilisateur dans la toolbar
            intent.putExtra("userAnime",this.userAnime);
        context.startActivity(intent);

        return;
    }

    /**
     * Se connecte à l'adresse URL en requête GET
     * Récupère le flux, le parse pour récupérer ce qui nous intéresse et le retourne dans un String
     * @param myurl
     * @return
     * @throws IOException
     */
    public String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        //StringBuilder resultat = new StringBuilder(); //never used
        Log.i("JFL", "Starting download of " + myurl);

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String result = getStringFromInputStream(is);
            //Log.i("JFL" , "Result computed: " + result);

            return result;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Converti le flux en String
     * @param is
     * @return
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private String getStringFromInputStream(InputStream is)throws IOException, UnsupportedEncodingException {
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
    return total.toString();
    }

    /*
     * Parsing du username depuis le string reçu du flux
     * On récupère le nom de l'utilisateur comme il est conservé sur la bdd en ligne
     * Il est contenu entre les balises <user_name></user_name>
     */
    public String getUserName(String database){
        String result=null;
        if(database != null)
        {
            String[] test = database.split("<user_name>");
            if(test.length > 1)
            {
                test = test[1].split("</user_name>");
                result = test[0];
            }
        }
        return result;
    }

    /**
     * Parsing des séries depuis le string reçu du flux
     * Utilise les données reçues par l'inputstream pour créer une liste d'objets Anime
     * @param database
     * @return ArrayList<Anime>
     */
    public ArrayList<Anime> createAnimeList(String database)
    {
        String[] test = database.split("<anime>");
        String result = new String();
        List<String> toFilter = new ArrayList<String>();
        ArrayList<Anime> listeAnimes = new ArrayList<Anime>();
        for(int i=1; i<test.length;i++)
        {
            result+= test[i]+"\n\n";
            toFilter = parsetoAnime(test[i]);
            listeAnimes.add(creerAnime(toFilter));
        }
        return listeAnimes;
    }

    /**
     *  Contient toutes les données nécessaires à remplir les objets
     *  Effectue tout le parsing requis pour implémenter nos objets
     *  La ArrayList<String> renvoie tous les champs utiles pour implémenter la classe Anime
     * @param result
     * @return ArrayList<String>
     */
    public ArrayList<String> parsetoAnime(String result){
        ArrayList<String> toFilter = new ArrayList<String>();
        //récupération id
        String[] dataFilter = result.split("<series_animedb_id>");
        dataFilter = dataFilter[1].split("</series_animedb_id>");
        toFilter.add(dataFilter[0]);
        //récupération titre
        dataFilter = result.split("<series_title>");
        dataFilter = dataFilter[1].split("</series_title>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<series_synonyms>");
        dataFilter = dataFilter[1].split("</series_synonyms>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<series_type>");
        dataFilter = dataFilter[1].split("</series_type>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<series_episodes>");
        dataFilter = dataFilter[1].split("</series_episodes>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<series_status>");
        dataFilter = dataFilter[1].split("</series_status>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<series_start>");
        dataFilter = dataFilter[1].split("</series_start>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<series_end>");
        dataFilter = dataFilter[1].split("</series_end>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<series_image>");
        dataFilter = dataFilter[1].split("</series_image>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<my_id>");
        dataFilter = dataFilter[1].split("</my_id>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<my_watched_episodes>");
        dataFilter = dataFilter[1].split("</my_watched_episodes>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<my_start_date>");
        dataFilter = dataFilter[1].split("</my_start_date>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<my_finish_date>");
        dataFilter = dataFilter[1].split("</my_finish_date>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<my_score>");
        dataFilter = dataFilter[1].split("</my_score>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<my_status>");
        dataFilter = dataFilter[1].split("</my_status>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<my_rewatching>");
        dataFilter = dataFilter[1].split("</my_rewatching>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<my_rewatching_ep>");
        dataFilter = dataFilter[1].split("</my_rewatching_ep>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<my_last_updated>");
        dataFilter = dataFilter[1].split("</my_last_updated>");
        toFilter.add(dataFilter[0]);

        dataFilter = result.split("<my_tags>");
        dataFilter = dataFilter[1].split("</my_tags>");
        toFilter.add(dataFilter[0]);
        return toFilter;
    }

    /**
     * Renvoie un anime créé suite à une ArrayList<String>
     * @param input
     * @return Anime
     */
    public Anime creerAnime(List <String> input){
        return new Anime(input.get(0),input.get(1),input.get(2),input.get(3),input.get(4),input.get(5),input.get(6),input.get(7),input.get(8),input.get(9),
                input.get(10),input.get(11),input.get(12),input.get(13),input.get(14),input.get(15),input.get(16),input.get(17),input.get(18));
    }


    //on réarrange la liste par ordre alphabétique
    public void arrangerOrdreAlphabetique(){
        List<Anime> listeTest = new ArrayList<Anime>();
        List<Anime> nouvelleListe = new ArrayList<Anime>();
        if(this.listeAnimes.size()>0){
            listeTest.add(this.listeAnimes.get(0));//on prend le premier et on le place comme premier élément

            /* On regarde un à un les animes de listeAnimes à partir de la deuxième série
             * On vérifie avec tous les titres de la liste test avec la methode compareTo(String)
             * On cherche la valeur 1, sinon la plus petite positive pour placer l'anime après celui vérifié
             * Si on ne trouve pas de valeur positive, on cherche la plus grande valeur négative (de -1 à -infini) pour placer l'anime juste au-dessus
             */
            for(int i =1; i<this.listeAnimes.size();i++)
            {
                listeTest.add(this.listeAnimes.get(i));
            }
            trierAnime(listeTest);/*
            for(int i = 0; i<listeTest.size(); i++)
            {
                Log.i("JFL", "Origine: " + this.listeAnimes.get(i).getTitle() + " |Fin: " + listeTest.get(i).getTitle() + "\n");
            }*/
            listeAnimes.clear();
            for(int i = 0; i<listeTest.size(); i++)
            {
                this.listeAnimes.add(new Anime(listeTest.get(i)));
                //Log.i("JFL", "Origine: " + this.listeAnimes.get(i).getTitle() + " |Fin: " + listeTest.get(i).getTitle() + "\n");
            }
        }
    }

    /**
     * Réarrange par ordre alphabétique la liste de Anime
     * @param listeTest
     */
    public void trierAnime(List<Anime> listeTest){
        Collections.sort(listeTest);
    }
}
