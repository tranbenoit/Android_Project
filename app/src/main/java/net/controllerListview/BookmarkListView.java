package net.controllerListview;

/**
 * Created by Benoît on 14/04/2016.
 */
public class BookmarkListView {
    private String nomUser;

    public BookmarkListView(String nomUser) {
        this.nomUser = nomUser;
    }

    public String getNomUser() {
        return nomUser;
    }

    public void setNomUser(String nomUser) {
        this.nomUser = nomUser;
    }
}
