package net.controllerListview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.groupe_efrei.projecttranmertz.R;

import java.util.List;

/**
 * Cette classe gère l'affichage dynamique de la ListView de la liste d'un utilisateur
 * Created by Benoît on 10/04/2016.
 */
public class AnimeListAdapter extends ArrayAdapter<AnimeListView> {
    public AnimeListAdapter(Context context, List<AnimeListView> animeList) {
        super(context, 0, animeList);//Ressource vaudra systématiquement 0
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.anime_ligne,parent, false);
        }

        AnimeListViewHolder viewHolder = (AnimeListViewHolder) convertView.getTag();//on récupère le tag de la convertView pour pouvoir la modifier
        if(viewHolder==null)//on instancie les champs du viewHolder, s'il existe bien
        {
            viewHolder = new AnimeListViewHolder();
            viewHolder.nomAnime = (TextView) convertView.findViewById(R.id.nomAnime);
            viewHolder.nbEpisode = (TextView) convertView.findViewById(R.id.nbEpisode);
            viewHolder.scoreUser = (TextView) convertView.findViewById(R.id.scoreUser);
            viewHolder.imageAnime = (ImageView) convertView.findViewById(R.id.imageAnime);
            viewHolder.userStatus = (TextView) convertView.findViewById(R.id.userStatus);
        }

        //récupère l'index/position de la liste List<AnimeListView> animeList
        AnimeListView animelistview = getItem(position);

        //on remplit la vue
        viewHolder.nomAnime.setText(animelistview.getNomAnime());
        viewHolder.nbEpisode.setText("Episode(s) : "+ animelistview.getNbEpisode());
        viewHolder.scoreUser.setText("Score : "+animelistview.getScore());
        Picasso.with(getContext()).load(animelistview.getImage()).into(viewHolder.imageAnime);
        viewHolder.userStatus.setText(animelistview.getUserStatus());


        return convertView;
    }

}
