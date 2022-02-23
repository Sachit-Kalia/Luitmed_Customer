package com.luitmed.prashantimedicos;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.luitmed.prashantimedicos.Models.Category;

import java.util.List;
import java.util.Map;

public class MyListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private Map<Category, List<Category>> categoryList;
    private List<Category> groupList;

    public MyListAdapter(Context context, Map<Category, List<Category>> categoryList, List<Category> groupList) {
        this.context = context;
        this.categoryList = categoryList;
        this.groupList = groupList;
    }

    @Override
    public int getGroupCount() {
        return categoryList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return categoryList.get(groupList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition).getTitle();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return categoryList.get(groupList.get(groupPosition)).get(childPosition).getTitle();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String category = getGroup(groupPosition).toString();
        Integer imageResource = groupList.get(groupPosition).getImage();
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_item, null);
        }
        TextView item = convertView.findViewById(R.id.groupTitle);
        item.setText(category);
        ImageView image = convertView.findViewById(R.id.groupImage);
        image.setImageResource(imageResource);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String model = getChild(groupPosition, childPosition).toString();
        Integer imageResource = categoryList.get(groupList.get(groupPosition)).get(childPosition).getImage();
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        TextView item = convertView.findViewById(R.id.childTitle);
        item.setText(model);
        ImageView image = convertView.findViewById(R.id.childImage);
        image.setImageResource(imageResource);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Products.class);
                intent.putExtra("type", "subcategory");
                intent.putExtra("query", model);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
