package net.groupe_efrei.projecttranmertz;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import net.controllerListview.AnimeListAdapter;
import net.controllerListview.AnimeListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe gère l'affichage de la liste d'Animes d'un utilisateur recherché
 * On a un ListView qui contient des RelativeLayout. Chacun contient les informations d'une série de la liste d'un utilisateur
 * Lorsque l'on clique sur un RelativeLayout, on lance l'activité FicheAnime
 */
public class User extends AppCompatActivity {

    private ArrayList<Anime> listeAnimes;
    private ListView mListView = null;
    private String userAnime = null;//utilisateur recherché
    private String database = null;
    private static final String bookmark = "bookmark.txt";
    private ArrayList<String> bookmarkUsers = new ArrayList<String>();//contiendra tous les utilisateurs favoris
    private boolean favored = true;//indicateur pour changer le bouton favori

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animelist);

        this.mListView = null;
        this.userAnime = null;

        Bundle extras = getIntent().getExtras(); //On récupère les informations de l'activité MainActivity
        this.listeAnimes = getIntent().getExtras().getParcelableArrayList("data");//on récupère la liste d'Anime
        this.userAnime = extras.getString("userAnime");//on récupère le nom de l'utilisateu
    }

    @Override
    protected void onStart(){
        super.onStart();
        this.bookmarkUsers = readBookmark();//On récupère les données de persistence du fichier bookmark.txt contenant les utilisateurs favoris
        mListView = (ListView) findViewById(R.id.listView);
        List<AnimeListView> animes = genererAnimes(this.listeAnimes);
        AnimeListAdapter adapter = new AnimeListAdapter(User.this,animes);


        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//évènement quand on clique sur un bouton de la list<view> (liste d'animes)
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                //Log.i("JFL", "ListView ID : " + position);
                Intent intent = new Intent(getApplicationContext(), FicheAnime.class);
                intent.putExtra("listeAnimes", listeAnimes.get(position));//on renvoie l'anime correspondant
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {//évite que ça bug avec des version android inférieures à 3.0
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            if(this.userAnime != null) {
                getMenuInflater().inflate(R.menu.menu_user, menu);
                actionBar.setTitle(this.userAnime);

                /**
                 * On vérifie si l'utilisateur est bien dans les favoris
                 * Si oui, on met l'étoile remplie
                 * Si non, on met l'étoile vide
                 */
                if (userIsBookmark(this.userAnime)) {
                    menu.findItem(R.id.follow).setIcon(R.drawable.followed_user);
                } else {
                    menu.findItem(R.id.follow).setIcon(R.drawable.follow_user);
                }
            }
            else
            {
                getMenuInflater().inflate(R.menu.menu_fiche, menu);//évite que l'on puisse rajouter en favoris un utilisateur null
            }
            //Si erreur de saisie (vide, utilisateur inexistant), on affiche un message dans la toolbar pour éviter une fenêtre vide
            if(listeAnimes.size() < 1)
                actionBar.setTitle("Username not in database");
        }
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Permet de gérer les boutons de flèche back, l'étoile permettant de rajouter en favoris et le menu coulissant quand cliqué
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.follow:
                //Log.i("JFL", "Cliqué sur favori !");
                if(userIsBookmark(this.userAnime))//on supprime l'utilisateur des favoris
                {
                    //Log.i("JFL", "Delete");
                    item.setChecked(false);
                    deleteFromBookmark(this.userAnime);
                }
                else//on met l'utilisateur en favori
                {
                    //Log.i("JFL", "Ajout");
                    item.setChecked(true);
                    saveUser(this.userAnime);
                }
                item.setIcon(item.isChecked() ? R.drawable.followed_user : R.drawable.follow_user);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Crée une liste qui permettra d'instancier notre adapteur pour avoir une ListView dynamique
     * @param liste
     * @return List<AnimeListView></AnimeListView>
     */
    private List<AnimeListView> genererAnimes(ArrayList<Anime> liste)
    {
        List<AnimeListView> animes = new ArrayList<AnimeListView>();
        AnimeListView toAdd = null;
        //Log.i("JFL", "Generation anime: ");
        for(int i = 0; i<liste.size(); i++)
        {
            toAdd = new AnimeListView(liste.get(i).getTitle(),liste.get(i).getWatched_episode() +"/" +liste.get(i).getEpisode(),
                    liste.get(i).getScore(),liste.get(i).getSeries_image(),liste.get(i).getMyStatusAnime());
            animes.add(toAdd);
        }
        return animes;
    }


    /**
     * On enregistre un utilisateur dans le livre de favoris
     * À
     */
    public void saveUser(String user){
        String fichier = bookmark;
        String eol = System.getProperty("line.separator");//saut à la ligne
        BufferedWriter writer = null;
        try{
            File test = new File(getApplicationContext().getFilesDir().getPath().toString()+"/" + fichier);//différent de openfileoutput, il faut le chemin précis
            if(!test.exists())
            {//on crée le fichier de telle sorte à ce qu'uniquement le programme puisse y avoir accès
                //Log.i("JFL", "File not exist");
                writer = new BufferedWriter(new OutputStreamWriter(openFileOutput(fichier, Context.MODE_PRIVATE)));
            }
            else
            {//on ajoute des lignes au fichier
                //Log.i("JFL", "File exist");
                writer = new BufferedWriter(new OutputStreamWriter(openFileOutput(fichier, Context.MODE_APPEND)));
            }
            //rajoute l'utilisateur aux favoris
            writer.write(user+eol);
            this.bookmarkUsers.add(user);
        }
        catch(Exception e){
            e.printStackTrace();
        }finally{
            if(writer != null){ //fermeture du fichier
                try{
                    writer.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * On récupère tous les utilisateurs enregistrés
     * @return
     */
    public ArrayList<String> readBookmark(){
        String fichier = bookmark;
        String data = "";
        String eol = System.getProperty("line.separator");
        ArrayList<String> result = new ArrayList<String>();
        BufferedReader input = null;
        try {
            input = new BufferedReader(new InputStreamReader(openFileInput(fichier)));
            StringBuffer buffer = new StringBuffer();
            while ((data = input.readLine()) != null) {

                result.add(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {//fermeture du fichier
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * On supprime un utilisateur des favoris
     * @param user
     */
    public void deleteFromBookmark(String user){
        this.bookmarkUsers.remove(user);
        String fichier = bookmark;
        String eol = System.getProperty("line.separator");//saut à la ligne
        BufferedWriter writer = null;
        try{
            File test = new File(getApplicationContext().getFilesDir().getPath().toString()+"/" + fichier);//différent de openfileoutput, il faut le chemin précis
            if(test.exists())
            {//on réécrie le fichier entièrement
                //Log.i("JFL", "File exist");
                writer = new BufferedWriter(new OutputStreamWriter(openFileOutput(fichier, Context.MODE_PRIVATE)));
                //rajoute l'utilisateur aux favoris
                for(int i =0; i<this.bookmarkUsers.size();i++)
                {
                    writer.write(this.bookmarkUsers.get(i)+eol);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }finally{
            if(writer != null){ //fermeture du fichier
                try{
                    writer.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Recherche si on a l'utilisateur recherché dans nos favoris
     * @param user
     * @return true si utilisateur est favori, false sinon
     */
    public boolean userIsBookmark(String user)
    {
        //Log.i("JFL","utilisateur: "+ user+" condition: "+ this.bookmarkUsers.contains(user) + " book: " + this.bookmarkUsers);
        if(this.bookmarkUsers.contains(user))
            return true;
        return false;
    }

    protected void onDestroy(){
        super.onDestroy();
    }

    protected void onPause(){
        super.onPause();
    }

    protected void onResume(){
        super.onResume();
    }

    protected void onStop(){
        super.onStop();
    }
}