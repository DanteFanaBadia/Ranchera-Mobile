package ado.edu.pucmm.rancherasystem.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ado.edu.pucmm.rancherasystem.R;
import ado.edu.pucmm.rancherasystem.adapters.ProductsDetailsRecyclerViewAdapter;
import ado.edu.pucmm.rancherasystem.entity.Bill;
import ado.edu.pucmm.rancherasystem.entity.Client;
import ado.edu.pucmm.rancherasystem.entity.Product;
import ado.edu.pucmm.rancherasystem.db.RanchDatabaseRepo;

public class OrderSummaryActivity extends AppCompatActivity {

    private int billId;
    private Bill bill;
    private Client client;
    private float total;
    private List<Integer> productsIds;
    private List<Integer> selectedAmounts;
    private List<Product> products;
    private ProductsDetailsRecyclerViewAdapter recyclerAdapter;
    private RanchDatabaseRepo ranchDatabaseRepo;
    private ImageView billIcon;
    private TextView billAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ranchDatabaseRepo = new RanchDatabaseRepo(getBaseContext());
        setContentView(R.layout.activity_resumen_orden);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerAdapter = new ProductsDetailsRecyclerViewAdapter(this);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        billIcon = findViewById(R.id.bill_icon3);
        billAmount = findViewById(R.id.bill_clientes_text3);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            billId = extras.getInt("billId");
        }

        total = 0;
        products = new ArrayList<>();
        selectedAmounts = new ArrayList<>();
        bill = ranchDatabaseRepo.getBill(this, billId);

        client = ranchDatabaseRepo.getSingleClient(this,bill.getClient());

        productsIds = ranchDatabaseRepo.getProductsFromDetail(this, billId);

        Integer billCount = ranchDatabaseRepo.getDoneBillCount(this, client.getId());

        String billMessage = String.valueOf(billCount);

        if(billCount == 1) {
            billMessage = billMessage + " factura vencida";
        }

        else if(billCount > 1){
            billMessage = billMessage + " facturas vencidas";
        }

        else{
            billAmount.setTextColor(Color.BLACK);
            billMessage = " No tiene facturas vencidas";
        }

        billIcon.setVisibility(View.VISIBLE);
        billAmount.setVisibility(View.VISIBLE);
        billAmount.setText(billMessage);

        for(Integer id : productsIds){
            Product current = ranchDatabaseRepo.getOrderProduct(this,id);
            Integer amount = ranchDatabaseRepo.getSelectedProductAmount(this,id, billId);
            total += current.getPrice() * amount;
            products.add(current);
            selectedAmounts.add(amount);
        }

        bill.setTotal(total);
        ranchDatabaseRepo.updateBillTotal(this, billId, total);
        String total_message = "Total: RD$" + String.valueOf(total);
        setText(R.id.name_clientes_text, client.getName());
        setText(R.id.phone_clientes_text, client.getPhoneNumber());
        setText(R.id.email_clientes_text, client.getEmail());
        setText(R.id.total_textView,total_message);

        recyclerAdapter.setProducts(products);
        recyclerAdapter.setAmounts(selectedAmounts);
    }

    private void setText(int resourceId, String text){
        ((TextView)findViewById(resourceId)).setText(text);
    }

    public void toSelectProduct(View view) {
        Intent intent = new Intent(this, SelectProductsActivity.class);
        intent.putExtra("billId", billId);
        startActivity(intent);
    }

    public void toSummary(View view) {
        Intent intent = new Intent(this, ConfirmOrderActivity.class);
        intent.putExtra("billId", billId);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SelectProductsActivity.class);
        intent.putExtra("billId", billId);
        startActivity(intent);
    }
}
