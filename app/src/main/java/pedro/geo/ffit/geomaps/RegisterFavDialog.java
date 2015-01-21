package pedro.geo.ffit.geomaps;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import pedro.geo.ffit.model.Favorite;

/**
 * Created by pedro on 21/01/15.
 */
public class RegisterFavDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_register_fav, container, false);
        getDialog().setTitle("Adicionar Favorito");

        final EditText edTitle = (EditText) view.findViewById(R.id.favorite_title);
        final EditText edDescription = (EditText) view.findViewById(R.id.favorite_description);

        // Não sei uma maneira eficaz de passar um objeto para essa classe
        final Boolean update = getArguments().getBoolean("update");
        final int position = getArguments().getInt("position");
        final String id = getArguments().getString("fav_id");
        final String address = getArguments().getString("address");
        final String snippet = getArguments().getString("snippet");

        // Facilita a vida do usuário setando o nome e o cep da localidade direto no EditText
        edTitle.setText(address);
        edDescription.setText(snippet);

        // Ao clicar no botão save é analizado se o booleano update está setado como true para
        // efetuar a atualização, caso contrário é efetuado o registro de um novo favorito
        Button btSave = (Button) view.findViewById(R.id.button_favorite_save);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity mapsActivity = (MapsActivity) getActivity();

                if (update) {

                    Favorite favorite = new Favorite();
                    favorite.setId(id);
                    favorite.setTitle(edTitle.getText().toString());
                    favorite.setDescription(edDescription.getText().toString());

                    mapsActivity.onClickUpdateFav(position, favorite);

                } else {

                    mapsActivity.onClickSaveFav(edTitle.getText().toString(), edDescription.getText().toString());
                }

                getDialog().dismiss();
            }
        });

        // Botão simples para fechamento do dialog
        Button btLeave = (Button) view.findViewById(R.id.button_favorite_leave);
        btLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }
}
