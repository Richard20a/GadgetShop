package com.example.gadgetshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ShoppingActivity extends AppCompatActivity {

    private static final String LOG_TAG = ShoppingActivity.class.getName();

    private RecyclerView mRecyclerView;
    private ArrayList<ShoppingItem> mItemsData;
    private ShoppingItemAdapter mAdapter;
    private int gridNumber = 1;
    private int cartItems = 0;
    private int itemLimit = 10;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private boolean viewRow = true;
    private FrameLayout redCircle;
    private TextView countTextView;
    private NotificationHandler mNotificationHandler;
    //private AlarmManager mAlarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        // Set the Layout Manager.
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        // Initialize the ArrayList that will contain the data.
        mItemsData = new ArrayList<>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new ShoppingItemAdapter(this, mItemsData);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");
        queryData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        this.registerReceiver(powerReceiver, filter);

        mNotificationHandler = new NotificationHandler(this);
    }

    BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();

            if (intentAction == null)
                return;

            switch (intentAction) {
                case Intent.ACTION_BATTERY_OKAY:
                    itemLimit = 10;
                    queryData();
                    break;
                case Intent.ACTION_BATTERY_LOW:
                    itemLimit = 4;
                    queryData();
                    break;
            }
        }
    };

    private void initializeData() {
        String[] itemsList = getResources().getStringArray(R.array.shopping_item_names);
        String[] itemsInfo = getResources().getStringArray(R.array.shopping_item_desc);
        String[] itemsPrice = getResources().getStringArray(R.array.shopping_item_price);
        TypedArray itemsImageResources = getResources().obtainTypedArray(R.array.shopping_item_images);
        TypedArray itemRate = getResources().obtainTypedArray(R.array.shopping_item_rates);

        mItemsData.clear();

        for (int i = 0; i < itemsList.length; i++) {
            mItems.add(new ShoppingItem(itemsList[i], itemsInfo[i], itemsPrice[i], itemRate.getFloat(i, 0),
                    itemsImageResources.getResourceId(i, 0), 0));
        }

        itemsImageResources.recycle();
    }

    private void queryData() {
        mItemsData.clear();
        mItems.orderBy("cartedCount", Query.Direction.DESCENDING).limit(itemLimit).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                ShoppingItem item = document.toObject(ShoppingItem.class);
                item.setId(document.getId());
                mItemsData.add(item);
            }

            if (mItemsData.size() == 0) {
                initializeData();
                queryData();
            }

            mAdapter.notifyDataSetChanged();
        });
    }

    public void deleteItem(ShoppingItem item) {
        DocumentReference ref = mItems.document(item._getId());
        ref.delete()
                .addOnSuccessListener(success -> {
                    Log.d(LOG_TAG, "Item is successfully deleted: " + item._getId());
                })
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Item " + item._getId() + " cannot be deleted.", Toast.LENGTH_LONG).show();
                });

        queryData();
        mNotificationHandler.cancel();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_button:
                Log.d(LOG_TAG, "Logout clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.settings_button:
                Log.d(LOG_TAG, "Setting clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.cart:
                Log.d(LOG_TAG, "Cart clicked!");
                return true;
            case R.id.view_selector:
                if (viewRow) {
                    changeSpanCount(item, R.drawable.ic_view_module, 1);
                } else {
                    changeSpanCount(item, R.drawable.ic_view_stream, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(ShoppingItem item) {
        cartItems = (cartItems + 1);
        if (0 < cartItems) {
            countTextView.setText(String.valueOf(cartItems));
        } else {
            countTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);

        mItems.document(item._getId()).update("cartedCount", item.getCartedCount() + 1)
                .addOnFailureListener(fail -> {
                    Toast.makeText(this, "Item " + item._getId() + " cannot be changed.", Toast.LENGTH_LONG).show();
                });

        mNotificationHandler.send(item.getName());
        queryData();
    }
}