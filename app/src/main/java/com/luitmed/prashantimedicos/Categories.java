package com.luitmed.prashantimedicos;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.luitmed.prashantimedicos.Models.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Categories extends AppCompatActivity {

    List<Category> groupList;
    List<Category> childList;
    Map<Category, List<Category>> categories;
    private ExpandableListView expandableListView;
    private MyListAdapter adapter;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        groupList = new ArrayList<>();
        back = findViewById(R.id.catBack);
        expandableListView = findViewById(R.id.expandableList);

        createGroupList();
        createCollection();

        adapter = new MyListAdapter(this, categories, groupList);
        expandableListView.setAdapter(adapter);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            int lastExpandedPostion = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if(lastExpandedPostion != -1 && groupPosition!=lastExpandedPostion){
                    expandableListView.collapseGroup(lastExpandedPostion);
                }
                lastExpandedPostion = groupPosition;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                String selected = adapter.getChild(groupPosition, childPosition).toString();
                Toast.makeText(Categories.this, "" + selected, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void createCollection() {

        Category[] medicines = {new Category("Heart and Blood pressure", R.drawable.heart_blood_pressure),
                                new Category("Diabetes and Sugar", R.drawable.blood_glucose),
                                new Category("Thyroid", R.drawable.thairoid),
                                new Category("Cough and Cold", R.drawable.cough_cold),
                                new Category("Fever", R.drawable.fever),
                                new Category("Pain", R.drawable.pain),
                                new Category("Gastric and Liver", R.drawable.gastric),
                                new Category("Sexual", R.drawable.sex_wear),
                                new Category("Neurology", R.drawable.neurology),
                                new Category("Kidney", R.drawable.kidney),
                                new Category("Skin", R.drawable.skin),
                                new Category("Vitamins and Minerals", R.drawable.vitamin),
                                new Category("Protein Supplements", R.drawable.protien),
                                new Category("Antibiotics", R.drawable.antibiotics),
                                new Category("Cancer", R.drawable.cancer)};


        Category[] healthProducts = {new Category("Baby Pads", R.drawable.c6),
                                     new Category("Sanitary Napkins", R.drawable.c6),
                                    new Category("Vitamins & Supplements", R.drawable.vitamin),
                                    new Category("Cosmetics", R.drawable.c6),
                                    new Category("Mouth Care", R.drawable.mouthcare),
                                    new Category("Protein Supplements", R.drawable.protien),
                                    new Category("Nutritional Drinks", R.drawable.c6),
                                    new Category("Health-care Devices", R.drawable.health_monitor),
                                    new Category("Ayurveda & Herbal", R.drawable.aurved_herbal),
                                    new Category("Sanitizers & Hand-washes", R.drawable.sanitizer),
                                    new Category("Haircare", R.drawable.hair_care),
                                    new Category("Immunity Boosters", R.drawable.c4),
                                    new Category("Grooming", R.drawable.c6)};


        Category[] covid = {new Category("Masks", R.drawable.c8),
                            new Category("Sanitizers", R.drawable.sanitizer),
                            new Category("Hand-wash", R.drawable.sanitizer),
                            new Category("Immunity Boosters", R.drawable.c4),
                            new Category("Face Shield", R.drawable.c8),
                            new Category("Gloves", R.drawable.c8)};



        categories = new HashMap<>();

        for(Category group: groupList){
            if(group.getTitle().equals("Medicines")){
                loadChild(medicines, group);
            }else if(group.getTitle().equals("Health Products")){
                loadChild(healthProducts, group);
            }else{
                loadChild(covid, group);
            }
        }

    }

    private void loadChild(Category[] catgs, Category group) {

        childList = new ArrayList<>();

        for(Category model: catgs){
            childList.add(model);
        }

        categories.put(group, childList);
    }

    private void createGroupList() {

        groupList = new ArrayList<>();
        groupList.add(new Category("Medicines", R.drawable.c2));
        groupList.add(new Category("Health Products", R.drawable.c4));
        groupList.add(new Category("COVID 19", R.drawable.c7));

    }
}