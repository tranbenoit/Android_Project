package net.groupe_efrei.projecttranmertz;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.connection.DownloadFilesTask;

import java.io.InputStream;


public class MainActivity extends AppCompatActivity {
    private String urlRoot = "http://myanimelist.net/malappinfo.php?u=";//adresse pour recevoir les informations xml de la liste d'un utilisateur
    private InputStream in = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //codage du bouton "Search anime list" de l'activité principale
        Button search_animelist = (Button) findViewById(R.id.seekanimelist);

        //codage du bouton "searchBookmark" de l'activité principale
        Button searchBookmark = (Button) findViewById(R.id.bookmark);

        //on récupère la valeur entrée dans le editextview authorlistinput
        EditText authorinput = (EditText) findViewById(R.id.authorlistinput);
        final Editable getAuthor = authorinput.getText();//voir ce qui fait défaut à ce truc, ne donne pas le texte



        search_animelist.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                //Log.i("JFL", "Edittext converted is : "+getAuthor.toString());
                Toast.makeText(getBaseContext(), "Chargement...", Toast.LENGTH_SHORT).show();

                String getUser = getAuthor.toString();
                String adresse = urlRoot + getUser;
                //Log.i("JFL", "Address is : "+adresse);

                //http request get
                trytoconnect(adresse, getUser);
            }
        });


        searchBookmark.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Chargement...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Bookmark.class);
                startActivity(intent);
            }
        });
    }


    public void trytoconnect(String adresse, String getUser) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {//on vérifie qu'on est bien connecté à internet
            AsyncTask<String, Void, String> as = new DownloadFilesTask(getApplicationContext(),getUser);
            as.execute(adresse);
        }
    }
}


