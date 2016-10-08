package com.stom3.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stom3.android.api.ResponseCallback;
import com.stom3.android.api.User;
import com.stom3.android.api.response.IndexValue;
import com.stom3.android.api.response.IndexesLength;
import com.stom3.android.api.response.IndexesQuality;
import com.stom3.android.api.response.IndexesWoods;
import com.stom3.android.api.response.Response;
import com.stom3.android.storage.PreferencesHelper;

import java.util.LinkedList;
import java.util.List;


public class QualityIndexesFragment extends Fragment{

    public static final String ARG_QUALITY_INDEXES = "quality_indexes";

    private IndexesQuality qualityIndexes;

    public static QualityIndexesFragment newInstance(IndexesQuality indexes) {
        QualityIndexesFragment f = new QualityIndexesFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putParcelable(ARG_QUALITY_INDEXES, indexes);
        f.setArguments(args);

        return f;
    }

    public static QualityIndexesFragment newInstance() {
        QualityIndexesFragment f = new QualityIndexesFragment();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        qualityIndexes = getArguments().getParcelable(ARG_QUALITY_INDEXES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        LinearLayout view =  (LinearLayout) inflater.inflate(R.layout.fragment_quality_indexes, container, false);

        for(IndexesLength lengthIndexes : qualityIndexes.getLengths()) {
            View cardView = inflater.inflate(R.layout.length_card, container, false);
            view.addView(cardView);

            TextView lengthName = (TextView) cardView.findViewById(R.id.length_title);
            if(!lengthIndexes.getName().equalsIgnoreCase("Не задано")) {
                lengthName.setText(lengthIndexes.getName() + " м");
            } else {
                lengthName.setVisibility(View.GONE);
            }

            LinearLayout lengthContainer = (LinearLayout) cardView.findViewById(R.id.length_container);

            boolean first = true;
            for(IndexesWoods woodIndexes : lengthIndexes.getWoods()) {


                final List<Integer> sizes = new LinkedList<>();
                final List<String> categories = new LinkedList<>();

                for(IndexValue sizeIndexes : woodIndexes.getValues()) {
                    sizes.add(sizeIndexes.getValue());
                    categories.add(sizeIndexes.getCategoryId());
                }

                if(first) {
                    first = false;
                    List<String> sizesTitles = new LinkedList<>();
                    boolean isNum = true;
                    for(IndexValue sizeIndexes : woodIndexes.getValues()) {
                        try {
                            Float.parseFloat(sizeIndexes.getSize());
                        } catch (NumberFormatException e) {
                            isNum = false;
                        }
                        sizesTitles.add(sizeIndexes.getSize());
                    }

                    ArrayAdapter<String> adapter;
                    if(isNum) {
                        adapter = new ArrayAdapter<>(getActivity(), R.layout.item_title, R.id.index_title, sizesTitles);
                    } else {
                        adapter = new ArrayAdapter<>(getActivity(), R.layout.item_title_raw, R.id.index_title, sizesTitles);
                    }
                    GridView indexesGrid = new GridView(getActivity());
                    indexesGrid.setNumColumns(woodIndexes.getValues().size());
                    indexesGrid.setAdapter(adapter);
                    lengthContainer.addView(indexesGrid);
                }

                TextView woodName = new TextView(getActivity());
                woodName.setText(woodIndexes.getName());
                lengthContainer.addView(woodName);

                ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getActivity(), R.layout.item, R.id.index_value, sizes);
                GridView indexesGrid = new GridView(getActivity());
                indexesGrid.setNumColumns(woodIndexes.getValues().size());
                indexesGrid.setAdapter(adapter);
                lengthContainer.addView(indexesGrid);
                indexesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if(PreferencesHelper.getInstance().isAuth()) {
                            User.subscribeCategory(categories.get(i), new ResponseCallback<Response>() {
                                @Override
                                public void onResponse(Response response) {
                                }

                                @Override
                                public void onError(String error) {

                                }
                            });
                        }
                    }


                });
            }
        }


        return view;
    }

}