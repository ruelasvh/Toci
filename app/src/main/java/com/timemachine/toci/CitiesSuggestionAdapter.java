package com.timemachine.toci;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victor Ruelas on 8/7/17.
 */

public class CitiesSuggestionAdapter extends ArrayAdapter<String>{

    Context context;
    int resource;
    List<String> items, tempItems, suggestions;

    public CitiesSuggestionAdapter(Context context, int resource,  List<String> items) {
        super(context, resource, items);
        this.context = context;
        this.resource = resource;
        this.items = items;
        tempItems = new ArrayList<>(items); // this makes the difference.
        suggestions = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.autocomplete_item, parent, false);
        }
        String city = items.get(position);
        if (city != null) {
            TextView lblCity= (TextView) view.findViewById(R.id.lbl_city);
            if (lblCity != null)
                lblCity.setText(city);
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return cityFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter cityFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((String) resultValue);
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (String city : tempItems) {
                    String cityOnly = city.split(",")[0]; // Match cities only by city, exclude state i.e. Brooklyn, NY
                    if (cityOnly.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(city);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<String> filterList = (ArrayList<String>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (String String : filterList) {
                    add(String);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
