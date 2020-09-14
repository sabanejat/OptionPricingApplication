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

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        String data = Constant.readFromFile(getActivity());
        String[] dataList = data.split(" , ");


        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(dataList);
        recyclerView.setAdapter(mAdapter);

        // Inflate the layout for this fragment
        return view;
    }


    /*
     * recyclerView needs an adapter to adapt an view and my model that now my model is String[]
     * */
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private String[] mDataset;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView textView;

            public MyViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.my_text_view);
            }
        }


        /*
         * we get data from constructor and initialize mDataset
         * */
        public MyAdapter(String[] myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_view, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            /*
             * In this section, it checks the first value of each row.
             * If it was 0, the background color is marked with red. If it is 1, it is marked with blue, and if it is 2, it turns green.
             * */

            if (mDataset[position].split(",")[0].equals("0")) { // 0 -> BlackScholestitle
                holder.textView.setBackgroundColor(Color.RED);
            } else if (mDataset[position].split(",")[0].equals("1")) { // 1 -> BinomialPricing
                holder.textView.setBackgroundColor(Color.BLUE);
            } else if (mDataset[position].split(",")[0].equals("2")) { // 2 -> monte_carlo_calculator
                holder.textView.setBackgroundColor(Color.GREEN);
            }
            holder.textView.setText(mDataset[position]);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }
}