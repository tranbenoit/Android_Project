package net.groupe_efrei.projecttranmertz;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import net.connection.DownloadFilesTask;
import net.controllerListview.BookmarkListAdapter;
import net.controllerListview.BookmarkListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benoît on 14/04/2016.
 */
public class Bookmark extends AppCompatActivity {

    private ListView mListView = null;
    private static final String bookmark = "bookmark.txt";
    private ArrayList<String> bookmarkUsers = new ArrayList<String>();//contiendra tous les utilisateurs favoris
    private String urlAPI = "http://myanimelist.net/malappinfo.php?u=";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liste_user);
    }

    @Override
    protected void onStart(){
        super.onStart();
        this.bookmarkUsers = readBookmark();
        mListView = (ListView) findViewById(R.id.listViewUser);
        List<BookmarkListView> bookmarks = genererBookmarkListView();
        BookmarkListAdapter adapter = new BookmarkListAdapter(Bookmark.this,bookmarks);


        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//lorsque l'on clique sur un des utilisateurs affichés, on connecte à sa liste d'Animes
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Log.i("JFL", "Bouton bookmark: " + bookmarkUsers.get(position));
                trytoconnect(urlAPI+bookmarkUsers.get(position),bookmarkUsers.get(position));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {//évite que ça bug avec des version android inférieures à 3.0
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public ArrayList<BookmarkListView> genererBookmarkListView()
    {
        ArrayList<BookmarkListView> bookmarks = new ArrayList<BookmarkListView>();
        for(int i =0; i<this.bookmarkUsers.size();i++)
        {
            bookmarks.add(new BookmarkListView(this.bookmarkUsers.get(i)));
        }
        return bookmarks;
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

    public void trytoconnect(String adresse, String getUser) {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Log.i("JFL", "Before test");
        if (networkInfo != null && networkInfo.isConnected()) {//on vérifie qu'on est bien connecté à internet
            Log.i("JFL", "Asynctask creation !");
            AsyncTask<String, Void, String> as = new DownloadFilesTask(getApplicationContext(),getUser);
            Log.i("JFL", "Asynctask created");
            as.execute(adresse);
            Log.i("JFL", "Asynctask launched in parallel");
        }
    }
}
