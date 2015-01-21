package pedro.geo.ffit.geomaps;

import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pedro.geo.ffit.db.DAO;
import pedro.geo.ffit.model.Favorite;


public class MapsActivity extends ActionBarActivity {

    // Id de identificação do dialog de registro do favorito
    private final int DIALOG_REGISTER_FAV = 1;

    // Mapa da google aonde através dele será efetuado as operações de manipulação do mapa
    private GoogleMap googleMap;

    // Lista de marcadores para assimilar qual maker pertence a posição da lista de favoritos
    // através do makers.get(int position);
    private List<Marker> markers;

    // atributos utilizados na função de callback dos dialogs
    private double latitude;
    private double longitude;

    // Necessário ser global para chamar o método dimiss quando os dialogs invocarem algum método de callback
    private ListFavDialog listFavDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Corrige a incompatibilidade com o navigation drawer
        GoogleMapOptions googleMapOptions = new GoogleMapOptions();
        googleMapOptions.zOrderOnTop(true);

        // Instancia MapFragment para ser utilizado no layout
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        googleMap = mapFragment.getMap();

        // Inicia a instancia inicial dos mapas
        initMap();

        // Instancia uma lista de markers para facilitar a remoção do maker através de getItem(position);
        markers = new ArrayList<>();

        // Carrega os makers armazenando-os no banco de dados, para o mapa
        List<Favorite> favorites = DAO.open(this).getListFavorites();
        for (int i = 0; i < favorites.size(); i++) {
            Favorite favorite = favorites.get(i);
            addMarker(new LatLng(favorite.getLatitude(), favorite.getLongitude()), favorite.getTitle(), favorite.getDescription());
        }

        // Botão que invocará o dialog que contém a lista de favoritos
        ImageButton buttonFav = (ImageButton) findViewById(R.id.button_fav);
        buttonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                listFavDialog = new ListFavDialog();
                listFavDialog.show(fragmentManager, "list_fav_dialog");
            }
        });

        // Solicita o serviço de GPS do android para atualizar a primeira posição do mapa como a posição atual do gps
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateWithNewLocation(location);
    }

    // Função que cria um círculo e seta a posição do mapa para indicar a posição atual do usuário
    private void updateWithNewLocation(Location location) {

        String addressString = "Endereço Desconhecido";
        String snippetString = "";

        if (location != null) {

            // Recupera a latitude e longitude, após isso seta a visão do mapa na sua localização atual
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            // Adiciona um círculo que irá se referir a sua localização atual
            googleMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(10)
                    .strokeColor(Color.RED)
                    .fillColor(Color.RED));

            // Utilizei dois modos de chamar o método animateCamera
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(16).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

            googleMap.animateCamera(cameraUpdate, 3000, null);
        }
    }

    private void initMap() {

        // Seta o tipo de mapa padrão da google
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Quando clicar no mapa será invocado um dialog passando para ele todas as informações importantes do ponto
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                latitude = latLng.latitude;
                longitude = latLng.longitude;

                // Geolocalização do endereço
                Geocoder gc = new Geocoder(MapsActivity.this, Locale.getDefault());

                Bundle bundle = new Bundle(2);
                try {
                    List<Address> addresses = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addresses.size() == 1) {
                        Address address = addresses.get(0);
                        bundle.putString("address", address.getAddressLine(0));
                        bundle.putString("snippet", address.getPostalCode());
                    }
                } catch (IOException ioe) {
                    Log.e("Geocoder IOException exception: ", ioe.getMessage());
                }

                // Modo de invocar um Dialog
                FragmentManager fragmentManager = getFragmentManager();
                RegisterFavDialog registerFavDialog = new RegisterFavDialog();

                registerFavDialog.setArguments(bundle);
                registerFavDialog.show(fragmentManager, "register_fav_dialog");
            }
        });
    }

    // Função chamada ao clicar no botão salva do dialog de registro do favorito
    public void onClickSaveFav(String title, String description) {

        Favorite favorite = new Favorite();
        favorite.setTitle(title);
        favorite.setDescription(description);
        favorite.setLatitude(latitude);
        favorite.setLongitude(longitude);

        DAO.open(this).insert(favorite);

        addMarker(new LatLng(latitude, longitude), title, description);
    }

    // Função chamada ao clicar no botão delete da lista de favoritos
    public void onClickDeleteFav(int position) {
        listFavDialog.dismiss();
        Marker marker = markers.get(position);
        marker.remove();
        markers.remove(position);
    }

    // Método que efetua a alteração dos dados no marker e no banco de dados
    public void onClickUpdateFav(int position, Favorite favorite) {
        listFavDialog.dismiss();

        markers.get(position).setTitle(favorite.getTitle());
        markers.get(position).setSnippet(favorite.getDescription());

        favorite.setLatitude(markers.get(position).getPosition().latitude);
        favorite.setLongitude(markers.get(position).getPosition().longitude);

        DAO.open(this).update(favorite);
    }

    // Adiciona um marcador ao mapa, e em seguida esse marcador é adicionado a uma lista para sua melhor manipulação
    public void addMarker(LatLng latLng, String title, String snippet) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).title(title).snippet(snippet).draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.flag));

        Marker marker = googleMap.addMarker(markerOptions);


        markers.add(marker);
    }

    // Método que move a janela do mapa de forma animada para a posição do marker definido no parâmetro
    public void moveToMarkerLocation(int position) {
        listFavDialog.dismiss();
        Marker marker = markers.get(position);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16), 3000, null);
    }

    // Método que cria a barra de menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_maps, menu);
        return true;
    }

    // Método que verifica qual item do menu foi selecionado
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_exit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
