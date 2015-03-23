package com.tonyjs.paperandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tonyjs on 15. 2. 10..
 */
public abstract class PaperViewAdapter<T> {
    public interface DataSetObserver {
        public void notifyDataSetChanged();

        public void notifyItemAdded();

        public void notifyItemRemoved(int position);
    }

    public PaperViewAdapter(){}

    private Context mContext;
    public PaperViewAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public Context getConext() {
        return mContext;
    }

    private LayoutInflater mInflater;
    public LayoutInflater getLayoutInflater() {
        return mInflater;
    }

    private List<T> mItems;
    public void setItems(ArrayList<T> items){
        mItems = items;
        notifyDataSetChanged();
    }

    public int getCount(){
        return mItems.size();
    }

    public int getItemId(int position) {
        return position;
    }

    public T getItem(int position) {
        return (mItems != null && mItems.size() > position) ? mItems.get(position) : null;
    }

    // Basic - Add Item On Top
    public void addItem(T item) {
        if (mItems == null) {
            mItems = new ArrayList<>();
        }
        mItems.add(0, item);
        notifyItemAdded();
    }

    public void addItems(ArrayList<T> items) {
        if (mItems == null) {
            setItems(items);
            return;
        }
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void removeItem(T item) {
        int position = getPosition(item);

        if (position < 0) {
            return;
        }

        notifyItemRemoved(position);
    }

    public int getPosition(T item) {
        return mItems == null ? 0 : mItems.indexOf(item);
    }

    public List<T> getItems() {
        return mItems;
    }

    public abstract View getView(int position, ViewGroup parent);

    private DataSetObserver mObserver;
    public void registerDataSetObserver(DataSetObserver observer) {
        mObserver = observer;
    }

    public void notifyDataSetChanged() {
        if (mObserver == null) {
            return;
        }

        mObserver.notifyDataSetChanged();
    }

    public void notifyItemAdded() {
        if (mObserver == null) {
            return;
        }

        mObserver.notifyItemAdded();
    }

    public void notifyItemRemoved(int position) {
        if (mObserver == null) {
            return;
        }

        mObserver.notifyItemRemoved(position);
    }
}
