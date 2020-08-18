package sabanejat.com;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Second_Frag extends Fragment {


    double sharePrice;
    double strikePrice;
    double riskFree;
    double vol;
    double time;
    int N;



    EditText optionPrice;
    EditText optionStrikePrice;
    EditText riskFreeInterestRate;
    EditText volatility;
    EditText optionDate;
    EditText numOfSteps;

    Button calculate;

    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_first_, container, false);


        optionPrice = view.findViewById(R.id.optionPrice);
        optionStrikePrice = view.findViewById(R.id.optionStrikePrice);
        riskFreeInterestRate = view.findViewById(R.id.riskFreeInterestRate);
        volatility = view.findViewById(R.id.volatility);
        optionDate = view.findViewById(R.id.optionDate);
        numOfSteps = view.findViewById(R.id.numOfSteps);


        calculate = view.findViewById(R.id.calculate);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePrice = Double.parseDouble(optionPrice.getText().toString());
                strikePrice = Double.parseDouble(optionStrikePrice.getText().toString());
                riskFree = Double.parseDouble(riskFreeInterestRate.getText().toString());
                vol = Double.parseDouble(volatility.getText().toString());
                time = Double.parseDouble(optionDate.getText().toString());
                N = Integer.parseInt(numOfSteps.getText().toString());

                List<Double> results = estimatePrice(sharePrice, strikePrice, vol / 100, riskFree / 100, time, N);
                Double callOptionPrice = results.get(0);


                showToast(String.valueOf(callOptionPrice));


            }
        });
        return view;
    }


    public void showToast(String calculatedNum) {
        Toast.makeText(getActivity(), calculatedNum, Toast.LENGTH_LONG).show();

    }



    /**
     * Estimate European Option price based on Cox, Ross and Rubinstein model
     *
     * @param asset      Current Asset Price (Option price)
     * @param strike     Exercise Price (Option strike price)
     * @param volatility Annual volatility (volatility)
     * @param intRate    Annual interest rate (risk free interest rate)
     * @param expiry:    Time to maturity (in terms of year)
     * @param steps      : Number of steps
     * @return Put and call price of european options based on Cox, Ross and Rubinstein model
     */
    public List<Double> estimatePrice(double asset,
                                      double strike,
                                      double volatility,
                                      double intRate,
                                      double expiry,
                                      int steps) {
        List<Double> results = new ArrayList<>();
        List<Double> stockPrices = new ArrayList<>();
        List<Double> callOptionPrices = new ArrayList<>();
        List<Double> putOptionPrices = new ArrayList<>();

        double time_step = (expiry) / steps;
        double R = Math.exp(intRate * time_step);
        double dF = 1 / R; // discount Factor

        double u = Math.exp(volatility * Math.sqrt(time_step)); // up boundary
        double d = 1 / u; // down boundary (Cox, Ross and Rubinstein constraint)
        // at leaf node, price difference factor between each node
        double uu = u * u; // (u*d)
        double p_up = (R - d) / (u - d); // up probability
        double p_down = 1 - p_up; // down probability

        // initiliaze stock prices
        for (int i = 0; i <= steps; i++) {
            stockPrices.add(i, 0.0d);
        }

        double sDown = asset * Math.pow(d, steps);
        stockPrices.set(0, sDown);

        // Estimate stock prices in leaf nodes
        for (int i = 1; i <= steps; i++) {
            double sD = uu * stockPrices.get(i - 1);
            stockPrices.set(i, sD);
        }

        // estimate option's intrinsic values at leaf nodes
        for (int i = 0; i <= steps; i++) {
            double callOptionPrice = callPayOff(stockPrices.get(i), strike);
            callOptionPrices.add(i, callOptionPrice);
            double putOptionPrice = putPayOff(stockPrices.get(i), strike);
            putOptionPrices.add(i, putOptionPrice);
        }

        // and lets estimate option prices
        for (int i = steps; i > 0; i--) {
            for (int j = 0; j <= i - 1; j++) {
                double callV = dF * (p_up * callOptionPrices.get(j + 1) +
                        p_down * callOptionPrices.get(j));
                callOptionPrices.set(j, callV);
                double putV = dF * (p_up * putOptionPrices.get(j + 1) +
                        p_down * putOptionPrices.get(j));
                putOptionPrices.set(j, putV);
            }
        }

        // first elements holds option's price
        results.add(callOptionPrices.get(0));
        results.add(putOptionPrices.get(0));
        return results;
    }

    // Pay off method for put options
    private double putPayOff(double stockPrice, double strike) {
        return Math.max(strike - stockPrice, 0);
    }

    // Pay off method for call options
    private double callPayOff(double stockPrice, double strike) {
        return Math.max(stockPrice - strike, 0);
    }


}
