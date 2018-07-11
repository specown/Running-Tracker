package example.com.runningtracker;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import example.com.runningtracker.entities.RunEntry;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements OnClickListener {

    DatabaseHelper db;
    ListView historyList;
    Button clearHistoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        clearHistoryBtn = (Button) findViewById(R.id.clearHistoryBtn);
        clearHistoryBtn.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        historyList = (ListView) findViewById(R.id.historyList);

        //Call LoadHistory method
        LoadHistory();

    }

    private void LoadHistory() {
        //Get results from database and put into a list
        ArrayList result = db.getAllData();

        ArrayAdapter adapter = new ArrayAdapter<RunEntry>(this,
                android.R.layout.simple_list_item_1, result);
        historyList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        //Check if back button is pressed - return
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //If Clear History is pressed - delete everything from Database and re-load empty list in the history
            case R.id.clearHistoryBtn:
                db.deleteAll();
                LoadHistory();
                break;

            default:
                break;
        }
    }
}
