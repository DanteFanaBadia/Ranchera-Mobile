package ado.edu.pucmm.rancherasystem.ui.activity;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ado.edu.pucmm.rancherasystem.R;
import ado.edu.pucmm.rancherasystem.adapters.ClientSearchAdapter;
import ado.edu.pucmm.rancherasystem.db.RanchDatabaseRepo;
import ado.edu.pucmm.rancherasystem.entity.Bill;
import ado.edu.pucmm.rancherasystem.entity.Client;
import ado.edu.pucmm.rancherasystem.viewmodel.BillViewModel;

public class SelectClientActivity extends AppCompatActivity
        implements BillViewModel.Listener{

    private List<Client> clients;
    private BillViewModel billViewModel;
    private Client client;
    private Bill bill;
    private RanchDatabaseRepo ranchDatabaseRepo;
    private ImageView billIcon;
    private TextView billAmount;
    private  List<Bill> bills;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_clientes);

        ranchDatabaseRepo = new RanchDatabaseRepo(getBaseContext());
        clients = new ArrayList<Client>();
        bills = new ArrayList<Bill>();
        client = null;
                //payedAmount = repo.getBillAmount(this,bill.getId());

        billViewModel = ViewModelProviders.of(this).get(BillViewModel.class);
        this.billViewModel.setListener(this);

        billIcon = findViewById(R.id.bill_icon);
        billAmount = findViewById(R.id.bill_clientes_text);

        billIcon.setVisibility(View.INVISIBLE);
        billAmount.setVisibility(View.INVISIBLE);

        AutoCompleteTextView clientAutoComplete = findViewById(R.id.search_cliente);
        ClientSearchAdapter adapter = new ClientSearchAdapter(this, R.layout.client_search_dropdown, clients);
        clientAutoComplete.setAdapter(adapter);
        clientAutoComplete.setOnItemClickListener(onItemClickListener);
    }

    private AdapterView.OnItemClickListener onItemClickListener =
            (adapterView, view, i, l) -> {
                client = (Client) adapterView.getItemAtPosition(i);
                bills = ranchDatabaseRepo.getDoneBills(this, client.getId());
                int cnt = 0;
                float payedAmount;
                float total;
                float owed;
                for(Bill bill : bills){
                    payedAmount = ranchDatabaseRepo.getBillAmount(this, bill.getId());
                    total = bill.getTotal();
                    owed = total - payedAmount;
                    if(owed != 0) cnt++;
                }

                String billMessage = String.valueOf(cnt);

                if(cnt == 1) {
                    billMessage = billMessage + " factura vencida";
                }

                else if(cnt > 1){
                    billMessage = billMessage + " facturas vencidas";
                }

                else{
                    billAmount.setTextColor(Color.BLACK);
                    billMessage = " No tiene facturas vencidas";
                }

                billIcon.setVisibility(View.VISIBLE);
                billAmount.setVisibility(View.VISIBLE);
                setText(R.id.name_clientes_text, client.getName());
                setText(R.id.phone_clientes_text, client.getPhoneNumber());
                setText(R.id.email_clientes_text, client.getEmail());
                setText(R.id.address_clientes_text, client.getAddress());
                setText(R.id.bill_clientes_text, billMessage);
                hideKeyboard();
            };

    public void cancelOrder(View view) {
        startActivity(new Intent(this, MenuActivity.class));
    }

    public void toProductSelection(View view) {
        if(client!= null) {
            bill = new Bill(client.getId(),"Pending");
            billViewModel.insert(bill);
        } else {
            Toast.makeText(SelectClientActivity.this,
                    "Seleccione un cliente",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFinish(Bill bill) {
        if(client != null) {
            Intent intent = new Intent(this, SelectProductsActivity.class);
            intent.putExtra("billId", bill.getId());
            startActivity(intent);
        }
    }

    private void setText(int resourceId, String text){
        ((TextView)findViewById(resourceId)).setText(text);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }
}