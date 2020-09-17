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

import static sabanejat.com.Constant.readFromFile;
import static sabanejat.com.Constant.writeToFile;

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

    Switch aSwitch;
    Button calculate;

    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        view = inflater.inflate(R.layout.fragment_second_, container, false);


        optionPrice = view.findViewById(R.id.optionPrice);
        optionStrikePrice = view.findViewById(R.id.optionStrikePrice);
        riskFreeInterestRate = view.findViewById(R.id.riskFreeInterestRate);
        volatility = view.findViewById(R.id.volatility);
        optionDate = view.findViewById(R.id.optionDate);
        numOfSteps = view.findViewById(R.id.numOfSteps);


        calculate = view.findViewById(R.id.calculate);
        aSwitch = view.findViewById(R.id.callSwitch);

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePrice = Double.parseDouble(optionPrice.getText().toString());
                strikePrice = Double.parseDouble(optionStrikePrice.getText().toString());
                riskFree = Double.parseDouble(riskFreeInterestRate.getText().toString());
                vol = Double.parseDouble(volatility.getText().toString());
                time = Double.parseDouble(optionDate.getText().toString());
                N = Integer.parseInt(numOfSteps.getText().toString());



                if (aSwitch.isChecked()) {
                    List<Double> results = calculate(sharePrice, strikePrice, vol / 100, riskFree / 100, time, N);
                    Double callOptionPrice = results.get(0);
                    showToast(String.valueOf(callOptionPrice));

                    try {

                        String data = readFromFile(getActivity()) + "1,"
                                +"Black Scholes , share price: " + sharePrice + ", strike price: " + strikePrice
                                + ", risk free interest rate: " + riskFree  + ", time: " + time + ", volatility: "
                                + vol + ", number of steps: "+N   +" Call option price: "
                                + calculate( sharePrice, strikePrice, vol / 100, riskFree/100, time, N)+ "-";
                        writeToFile(data, getActivity());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    List<Double> results = calculate(sharePrice, strikePrice, vol / 100, riskFree / 100, time, N);
                    Double putOptionPrice = results.get(1);
                    showToast(String.valueOf(putOptionPrice));

                    try {

                        String data = readFromFile(getActivity()) + "1,"
                                +"Binomial , share price: " + sharePrice + ", strike price: " + strikePrice
                                + ", risk free interest rate: " + riskFree  + ", time: " + time + ", volatility: "
                                + vol + ", number of steps: "+N   +" Put option price: "
                                + calculate( sharePrice, strikePrice, vol / 100, riskFree/100, time, N)+ "-";
                        writeToFile(data, getActivity());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }









            }
        });
        return view;
    }


    public void showToast(String calculatedNum) {
        Toast.makeText(getActivity(), calculatedNum, Toast.LENGTH_LONG).show();

    }




    public List<Double> calculate(double share, double strike, double vol,
                                  double riskFree, double time, int numSteps) {

        List<Double> results = new ArrayList<>();
        List<Double> sharePrices = new ArrayList<>();
        List<Double> callOptionPrices = new ArrayList<>();
        List<Double> putOptionPrices = new ArrayList<>();
        List<Double> americanCallOptionPrices = new ArrayList<>();
        List<Double> americanPutOptionPrices = new ArrayList<>();


        double disFactor = 1 / (Math.exp(riskFree * (time / numSteps)));

        double upBound = Math.exp(vol * Math.sqrt(time / numSteps));
        double downBound = 1 / upBound;

        double upProb = ((Math.exp(riskFree * (time / numSteps))) - downBound) / (upBound - downBound);
        double downProb = 1 - upProb;


        for (int i = 0; i <= numSteps; i++) {
            sharePrices.add(i, 0.0);
        }

        double sDown = share * Math.pow(downBound, numSteps);
        sharePrices.set(0, sDown);


        for (int i = 1; i <= numSteps; i++) {
            double sD = upBound * upBound * sharePrices.get(i - 1);
            sharePrices.set(i, sD);
        }


        for (int i = 0; i <= numSteps; i++) {
            double callOptionPrice = callPayOff(sharePrices.get(i), strike);
            callOptionPrices.add(i, callOptionPrice);
//            americanCallOptionPrices.add(i, callOptionPrice);

            double putOptionPrice = putPayOff(sharePrices.get(i), strike);
            putOptionPrices.add(i, putOptionPrice);
//            americanPutOptionPrices.add(i, putOptionPrice);
        }


        for (int i = numSteps -1; i >= 0; i--) {
            for (int j = 0; j <= i ; j++) {

                double europeanCallPrice = disFactor * (upProb * callOptionPrices.get(j + 1) +
                        downProb * callOptionPrices.get(j));
                callOptionPrices.set(j, europeanCallPrice);

                double europeanPutPrice = disFactor * (upProb * putOptionPrices.get(j + 1) +
                        downProb * putOptionPrices.get(j));
                putOptionPrices.set(j, europeanPutPrice);

//                double americanCallPrice = Math.max(sharePrices.get(i) - strike,
//                        disFactor * (upProb * putOptionPrices.get(j + 1) +
//                                downProb * putOptionPrices.get(j)));
//                americanCallOptionPrices.set(j, americanCallPrice);
//
//
//                double americanPutPrice = Math.max(strike - sharePrices.get(i),
//                        disFactor * (upProb * putOptionPrices.get(j + 1) +
//                                downProb * putOptionPrices.get(j)));
//                 americanPutOptionPrices.set(j, americanPutPrice);
//
            }
        }
        results.add(callOptionPrices.get(0));
        results.add(putOptionPrices.get(0));
//        results.add(americanCallOptionPrices.get(0));
//        results.add(americanPutOptionPrices.get(0));
        return results;

    }

    private double putPayOff(double share, double strike) {
        return Math.max(strike - share, 0);
    }

    private double callPayOff(double share, double strike) {
        return Math.max(share - strike, 0);
    }

}

