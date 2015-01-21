package pedro.geo.ffit.geomaps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import pedro.geo.ffit.db.DAO;
import pedro.geo.ffit.model.Favorite;

/**
 * Created by pedro on 20/01/15.
 */
public class ListFavAdapter extends BaseAdapter {

    Context context;
    List<Favorite> listFav;

    public ListFavAdapter(Context context, List<Favorite> listFav) {
        this.context = context;
        this.listFav = listFav;
    }

    @Override
    public int getCount() {
        return listFav.size();
    }

    @Override
    public Object getItem(int position) {
        return listFav.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // Um padrão do android que permite até 70% de performance
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_favorites, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleFav.setText(listFav.get(position).getTitle());
        holder.descriptionFav.setText(listFav.get(position).getDescription());

        return convertView;
    }

    public class ViewHolder {

        private TextView titleFav;
        private TextView descriptionFav;

        public ViewHolder(View v) {
            this.titleFav = (TextView) v.findViewById(R.id.title_fav);
            this.descriptionFav = (TextView) v.findViewById(R.id.description_fav);
        }
    }
}