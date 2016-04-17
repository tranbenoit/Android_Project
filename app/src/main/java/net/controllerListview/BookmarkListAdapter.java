package net.controllerListview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.groupe_efrei.projecttranmertz.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Cette classe gère l'affichage dynamique de la ListView de la liste des utilisateurs enregistrés par persistence
 * Created by Benoît on 14/04/2016.
 */
public class BookmarkListAdapter extends ArrayAdapter<BookmarkListView> {

    public BookmarkListAdapter(Context context, List<BookmarkListView> objects) {
        super(context,0, objects);//Ressource vaudra systématiquement 0
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.user_ligne,parent, false);
        }

        BookmarkListViewHolder viewHolder = (BookmarkListViewHolder) convertView.getTag(); //on récupère le tag de la convertView pour pouvoir la modifier

        if(viewHolder==null)//on instancie les champs du viewHolder, s'il existe bien
        {
            viewHolder = new BookmarkListViewHolder();
            viewHolder.nomUser = (TextView) convertView.findViewById(R.id.favoredUser);
        }
        //récupère l'index/position de la liste List<BookmarkListView> animeList
        BookmarkListView bookmarklistview = getItem(position);

        //on remplit la vue
        viewHolder.nomUser.setText(bookmarklistview.getNomUser());
        return convertView;
    }
}
