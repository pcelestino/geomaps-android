package pedro.geo.ffit.geomaps;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import pedro.geo.ffit.db.DAO;
import pedro.geo.ffit.model.Favorite;

/**
 * Created by pedro on 20/01/15.
 */
public class ListFavDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_fav, container);
        getDialog().setTitle("Favoritos");

        final List<Favorite> listFav = DAO.open(getActivity()).getListFavorites();

        ListView listViewFav = (ListView) view.findViewById(R.id.list_fav);
        listViewFav.setAdapter(new ListFavAdapter(getActivity(), listFav));

        listViewFav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MapsActivity mapsActivity = (MapsActivity) getActivity();
                mapsActivity.moveToMarkerLocation(position);
            }
        });


        listViewFav.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle("Selecione uma das opções")
                        .setPositiveButton("Editar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Favorite favorite = listFav.get(position);
                                Bundle bundle = new Bundle(5);

                                Toast.makeText(getActivity(), favorite.getTitle(), Toast.LENGTH_SHORT).show();

                                bundle.putBoolean("update", true);
                                bundle.putInt("position", position);
                                bundle.putString("fav_id", favorite.getId());
                                bundle.putString("address", favorite.getTitle());
                                bundle.putString("snippet", favorite.getDescription());

                                FragmentManager fragmentManager = getFragmentManager();
                                RegisterFavDialog registerFavDialog = new RegisterFavDialog();

                                registerFavDialog.setArguments(bundle);
                                registerFavDialog.show(fragmentManager, "register_fav_dialog");
                            }
                        })
                        .setNegativeButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DAO.open(getActivity()).delete(listFav.get(position));

                                MapsActivity mapsActivity = (MapsActivity) getActivity();
                                mapsActivity.onClickDeleteFav(position);
                            }
                        });
                builder.show();
                return true;
            }
        });

        return view;
    }
}
