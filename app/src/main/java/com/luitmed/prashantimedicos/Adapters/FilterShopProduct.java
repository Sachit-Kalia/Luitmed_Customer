package com.luitmed.prashantimedicos.Adapters;

import android.widget.Filter;

import com.luitmed.prashantimedicos.Product;

import java.util.ArrayList;

public class FilterShopProduct extends Filter {

    private SearchProductAdapter adapter;
    private ArrayList<Product> filterList;

    public FilterShopProduct(SearchProductAdapter adapter, ArrayList<Product> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        // validate data for search query

        if(constraint != null && constraint.length() > 0){

            // Search field not empty

            constraint = constraint.toString().toUpperCase();
            ArrayList<Product> filteredModels = new ArrayList<>();
            for(int i=0; i<filterList.size(); i++){
                if(filterList.get(i).getTitle().toUpperCase().contains(constraint) ||
                        filterList.get(i).getDescription().toUpperCase().contains(constraint)){
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }else{
            // Search field empty

            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.productList = (ArrayList<Product>) results.values;
        // refresh the adapter
        adapter.notifyDataSetChanged();;

    }
}
