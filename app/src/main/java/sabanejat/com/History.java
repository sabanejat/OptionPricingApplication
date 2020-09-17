package sabanejat.com;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class History extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_history_, container, false);

        recyclerView = view.findViewById(R.id.my_recycler_view);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        String data = Constant.readFromFile(getActivity());
        String[] dataList = data.split("-");



        mAdapter = new MyAdapter(dataList);
        recyclerView.setAdapter(mAdapter);


        return view;
    }



    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private String[] mDataset;

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView textView;

            public MyViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.my_text_view);
            }
        }



        public MyAdapter(String[] myDataset) {
            mDataset = myDataset;
        }



        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {


            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_view, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {




            if (mDataset[position].split(",")[0].equals("0")) { // 0 -> BlackScholestitle

                for (int i = 0; i < mDataset[position].split(",").length; i++) {
                    holder.textView.setText("");
                }
            } else if (mDataset[position].split(",")[0].equals("1")) { // 1 -> BinomialPricing
            } else if (mDataset[position].split(",")[0].equals("2")) { // 2 -> monte_carlo_calculator
            } else if (mDataset[position].split(",")[0].equals("3")) { // 2 -> baw
            }
            holder.textView.setText(mDataset[position]);

        }

        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }
}