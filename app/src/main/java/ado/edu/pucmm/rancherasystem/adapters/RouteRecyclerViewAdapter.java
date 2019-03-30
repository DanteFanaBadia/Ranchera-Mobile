package ado.edu.pucmm.rancherasystem.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ado.edu.pucmm.rancherasystem.R;
import ado.edu.pucmm.rancherasystem.entity.Client;
import ado.edu.pucmm.rancherasystem.entity.Route;

public class RouteRecyclerViewAdapter extends RecyclerView.Adapter<RouteRecyclerViewAdapter.RouteViewHolder> {

    class RouteViewHolder extends RecyclerView.ViewHolder {

        private final TextView clientNameView;
        private final TextView addressView;

        private RouteViewHolder(View itemView) {
            super(itemView);
            clientNameView = itemView.findViewById(R.id.text_view_name);
            addressView = itemView.findViewById(R.id.address_clientes_text);
        }
    }

    private final LayoutInflater inflater;
    private List<Route> routes;
    private List<Client> clients = new ArrayList<>();

    public RouteRecyclerViewAdapter(Context context) { inflater = LayoutInflater.from(context); }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_route, parent, false);
        return new RouteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
       if(clients != null) {
           Client current = clients.get(position);
           holder.clientNameView.setText(current.getName());
           holder.addressView.setText(current.getAddress());
       } else {
           holder.clientNameView.setText("No tiene rutas pendientes");
       }
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public void setClients(List<Client> clients){
        this.clients = clients;
        notifyDataSetChanged();
    }
}