package net.connection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Benoît on 10/04/2016.
 */
public class DownloadHTML extends AsyncTask<String, Void, String> {

    public String dataHTML = null;
    private AsyncResponse callback;

    public DownloadHTML(Context contexte) {
        this.callback = (AsyncResponse) contexte;
    }

    @Override
    protected void onPostExecute(String s) {
        callback.dataHtml(dataHTML);
    }

    @Override
    protected String doInBackground(String... urls) {
        Log.i("JFL", "I should do in background " + urls[0]);
        try{
            return downloadUrl(urls[0]);
        } catch (IOException e) {
            return "Unable to retrieve web page. URL may be invalid.";
        }
    }

    /**
     * requête http get
     * @param myurl
     * @return
     * @throws IOException
     */
    public String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
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

            String result = getStringFromInputStream(is);
            dataHTML = result;
            return result;

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
        // conversion d'un inputstream en string
    private String getStringFromInputStream(InputStream is)throws IOException
    {
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }
        return total.toString();
    }

}
