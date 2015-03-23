package com.tonyjs.paperandroid.example;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.tonyjs.paperandroid.PaperView;
import com.tonyjs.paperandroid.PaperViewAdapter;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PaperView paperView = (PaperView) findViewById(R.id.paperview);
        PaperAdapter adapter = new PaperAdapter(this);
        ArrayList<String> colors = new ArrayList<String>(){
            {
                add("#b93485");
                add("#093875");
                add("#676811");
                add("#304999");
                add("#105021");
                add("#2156c8");
                add("#c8ff12");
                add("#11f8f0");
                add("#f011f1");
                add("#ece04c");
            }
        };
        adapter.setItems(colors);
        paperView.setAdapter(adapter);
    }

    private class PaperAdapter extends PaperViewAdapter<String> {
        private PaperAdapter(Context context) {
            super(context);
        }

        @Override
        public View getView(int position, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.paper_item, parent, false);

            CardView cardView = (CardView) view.findViewById(R.id.card_view);
            int color = Color.parseColor(getItem(position));
            cardView.setCardBackgroundColor(color);
            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
