package sabanejat.com;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static sabanejat.com.Constant.readFromFile;
import static sabanejat.com.Constant.writeToFile;

public class Third_Frag extends Fragment {


    double sharePrice;
    double strikePrice;
    double riskFree;
    double vol;
    double time;
    int nSims;


    EditText optionPrice;
    EditText optionStrikePrice;
    EditText riskFreeInterestRate;
    EditText volatility;
    EditText optionDate;
    EditText numOfSims;

    Button calculate;

    Switch aSwitch;

    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_third_, container, false);


        optionPrice = view.findViewById(R.id.optionPrice);
        optionStrikePrice = view.findViewById(R.id.optionStrikePrice);
        riskFreeInterestRate = view.findViewById(R.id.riskFreeInterestRate);
        volatility = view.findViewById(R.id.volatility);
        optionDate = view.findViewById(R.id.optionDate);
        numOfSims = view.findViewById(R.id.numOfSteps);


        calculate = view.findViewById(R.id.calculate);
        aSwitch = view.findViewById(R.id.callSwitch);



        calculate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sharePrice = Double.parseDouble(optionPrice.getText().toString());
                strikePrice = Double.parseDouble(optionStrikePrice.getText().toString());
                riskFree = Double.parseDouble(riskFreeInterestRate.getText().toString());
                vol = Double.parseDouble(volatility.getText().toString());
                time = Double.parseDouble(optionDate.getText().toString());
                nSims = Integer.parseInt(numOfSims.getText().toString());


                Double callOptionPrice = 0D;
                if (aSwitch.isChecked()) {
                    List<Double> results = estimatePrice(sharePrice, strikePrice, vol / 100, riskFree / 100, time, nSims);
                    callOptionPrice = results.get(0);
                    showToast(String.valueOf(callOptionPrice));

                } else {
                    List<Double> results = estimatePrice(sharePrice, strikePrice, vol / 100, riskFree / 100, time, nSims);
                    callOptionPrice = results.get(1);
                    showToast(String.valueOf(callOptionPrice));
                }

                try {

                    String data = readFromFile(getActivity()) + "2," + sharePrice + "," + strikePrice + "," + riskFree / 100 + "," + time + "," + vol / 100 + "," + nSims + "," + aSwitch.isChecked() + ","  + callOptionPrice + "-";
                    writeToFile(data, getActivity());

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        return view;

    }


    public void showToast(String calculatedNum) {
        Toast.makeText(getActivity(), calculatedNum, Toast.LENGTH_LONG).show();
    }

    /**
     * Estimate European Option price by Monte Carlo Simulation
     *
     * @param asset      Current Asset Price
     * @param strike     Exercise Price
     * @param volatility Annual volatility
     * @param intRate    Annual interest rate
     * @param time:      Time to maturity (in terms of year)
     * @param num_sim    : Number of simulations
     * @return Put and call price of european options based on
     * Monte Carlo Simulation
     */
    public List<Double> estimatePrice(double asset,
                                      double strike,
                                      double volatility,
                                      double intRate,
                                      double time,
                                      int num_sim) {
        List<Double> results = new ArrayList<>();
        double R = (intRate - 0.5 * Math.pow(volatility, 2)) * time;
        double SD = volatility * Math.sqrt(time);
        double dF = Math.exp(-intRate * time); // discount Factor
        double sumCallPayoffs = 0.0;
        double sumPutPayoffs = 0.0;
        Random random = new Random();
        for (int i = 0; i <= num_sim; i++) {
            double nextGaussian = random.nextGaussian();
            double S_T = asset * Math.exp(R + SD * nextGaussian);
            sumCallPayoffs += callPayOff(S_T, strike);
            sumPutPayoffs += putPayOff(S_T, strike);
        }
        double callOptionPrices = dF * sumCallPayoffs / num_sim;
        double putOptionPrices = dF * sumPutPayoffs / num_sim;
        results.add(callOptionPrices);
        results.add(putOptionPrices);
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

