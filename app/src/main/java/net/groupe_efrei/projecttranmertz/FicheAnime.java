package net.groupe_efrei.projecttranmertz;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.connection.AsyncResponse;
import net.connection.DownloadHTML;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Benoît on 10/04/2016.
 */
public class FicheAnime extends AppCompatActivity implements AsyncResponse {
    private ImageView imageView = null;
    private Anime anime = null;
    private TextView animeInformation = null;
    private WebView animeDescription = null;

    //c'est ici qu'on traite les affichages de texte (on prend de nouvelles informations sur une nouvelle page web
    @Override
    public void dataHtml(String result) {

        //Récupération du genre de la série
        String genre = parseHTMLgenre(result);
        //Log.i("JFL", "Information parsée : " + result);
        String affichage = animeInformation(anime) + genre + "\n<br/>";//on récupère toutes les informations concernant la série
        //Log.i("JFL", "affichage : \n" + affichage);
        this.animeInformation = (TextView) findViewById(R.id.animeInformation);//on les place dans la View
        this.animeInformation.setText(Html.fromHtml(affichage));//on mets les titres en gras avec des balises html, d'où la méthode fromHtml

        //Récupération du synopsys
        String description = parseDescriptionHTML(result);
        animeDescription = (WebView) findViewById(R.id.animeDescription);
        this.animeDescription.loadData(description.format("<html><body style=\"text-align:justify\"> %s </body></Html>",description), "text/html", "utf-8");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ficheanime);
        Intent intent = getIntent();
        anime = intent.getParcelableExtra("listeAnimes");//deserialisation
    }

    @Override
    protected void onStart(){
        super.onStart();
        ;


        imageView = (ImageView) findViewById(R.id.imageAnime);
        Picasso.with(getBaseContext()).load(anime.getSeries_image()).into(imageView);

        //Log.i("JFL", "\n" + anime.toString());

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {//on vérifie qu'on est bien connecté à internet
            //Log.i("JFL", "Asynctask creation !");
            AsyncTask<String, Void, String> as = new DownloadHTML(FicheAnime.this);
            as.execute("http://myanimelist.net/anime/"+anime.getId());
        }
    }


    public String animeInformation(Anime anime)
    {
        String result = "";
        if(anime != null)
        {
            result+= "<b>Type:</b> " + anime.getTypeAnime() + "\n<br/>";
            result+= "<b>Episodes:</b> " + anime.getEpisode() + "\n<br/>";
            result+= "<b>Status:</b>  " + anime.getStatusAnime() + "\n<br/>";
            if(anime.getDate(anime.getSeries_start()) != "?")
                result+= "<b>Aired:</b>  "+ anime.getDate(anime.getSeries_start()) + " to " + anime.getDate(anime.getSeries_end()) + "\n<br/>";
            else
                result+= "<b>Aired:</b> ?\n<br/>";
            result+= "<b>Genre:</b>  ";
        }
        return result;
    }

    public String parseHTMLgenre(String data){
        String result = "";
        ArrayList<String> toFilter = new ArrayList<String>();

        String[] dataFilter = data.split("genre");;

        for(int i =1; i<dataFilter.length;i++)
        {
            result+=dataFilter[i];
        }

        dataFilter = result.split("</td>");
        result = dataFilter[0];

        dataFilter = result.split("\">");
        result = "";
        for(int i =1; i<dataFilter.length;i++)
        {
            result+=dataFilter[i]+"\n";//on fait un saut à la ligne pour séparer la donnée qu'on veut du reste de la balise html
        }

        dataFilter = result.split("</a>");
        String[] antiespace;//on utilise ça pour retirer ignorer les lignes qui n'ont pas les données qu'on veut
        for(int i =0; i<dataFilter.length;i++)
        {
            //Log.i("JFL", "Ligne " + i + ": " + dataFilter[i]);
            if(dataFilter[i].contains("\n"))
            {
                antiespace = dataFilter[i].split("\n");
                if(antiespace.length > 1)//évite segfault, en cas de la dernière ligne qui est un espace
                    toFilter.add(antiespace[1]);
            }
            else
                toFilter.add(dataFilter[i]);
        }
        result = "";
        for(int i =0; i<toFilter.size();i++)
        {
            result+=toFilter.get(i) + ", ";
        }


        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//on traite le bouton retour
        //getMenuInflater().inflate(R.menu.menu_fiche,menu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {//évite que ça bug avec des version android inférieures à 3.0
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(this.anime.getTitle());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//ce qui se passe quand on fait les options
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String parseDescriptionHTML(String data)
    {
        String result="";

        String[] dataFilter = data.split("og:description\" content=\"");//on retire tout le texte html avant la description
        result = dataFilter[1];
        dataFilter = result.split("\">"); //on retire tout ce qui est après la description
        result = dataFilter[0];
        result = result.replaceAll("&quot;","\"");//code pour "
        result = result.replaceAll("&#039;", "\'");//code pour '
        //Log.i("JFL", "Description :\n" + result);
        return result;
    }
}
