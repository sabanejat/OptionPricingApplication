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


import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static sabanejat.com.Constant.readFromFile;
import static sabanejat.com.Constant.writeToFile;

public class First_Frag extends Fragment {

    double sharePrice;
    double strikePrice;
    double riskFree;
    double vol;
    double time;


    EditText optionPrice;
    EditText optionStrikePrice;
    EditText riskFreeInterestRate;
    EditText volatility;
    EditText optionDate;

    Switch aSwitch;
    Button calculate;

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_first_, container, false);


        optionPrice = view.findViewById(R.id.optionPrice);
        optionStrikePrice = view.findViewById(R.id.optionStrikePrice);
        riskFreeInterestRate = view.findViewById(R.id.riskFreeInterestRate);
        volatility = view.findViewById(R.id.volatility);
        optionDate = view.findViewById(R.id.optionDate);

        aSwitch = view.findViewById(R.id.callSwitch);

        calculate = view.findViewById(R.id.calculate);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePrice = Double.parseDouble(optionPrice.getText().toString());
                strikePrice = Double.parseDouble(optionStrikePrice.getText().toString());
                riskFree = Double.parseDouble(riskFreeInterestRate.getText().toString());
                vol = Double.parseDouble(volatility.getText().toString());
                time = Double.parseDouble(optionDate.getText().toString());


                if (aSwitch.isChecked()) {
                    double callOptionPrice = calculate(true, sharePrice, strikePrice, riskFree / 100,  time, vol/100);
                    showToast(String.valueOf(callOptionPrice));

                    try {
                        String data = readFromFile(getActivity()) + "0,"
                                +"Black Scholes , share price: " + sharePrice + ", strike price: " + strikePrice
                                + ", risk free interest rate: " + riskFree  + ", time: " + time + ", volatility: " + vol + " Call option price: "
                                + calculate(true, sharePrice, strikePrice, riskFree / 100,  time, vol/100)+ "-";
                        writeToFile(data, getActivity());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    double putOptionPrice = calculate(false, sharePrice, strikePrice, riskFree / 100,  time, vol/100);
                    showToast(String.valueOf(putOptionPrice));

                    try {
                        String data = readFromFile(getActivity()) + "0,"
                                +"Black Scholes , share price: " + sharePrice + ", strike price: " + strikePrice
                                + ", risk free interest rate: " + riskFree  + ", time: " + time + ", volatility: " + vol + " Put option price: "
                                + calculate(false, sharePrice, strikePrice, riskFree / 100,  time, vol/100)+ "-";
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



    public static double calculate(boolean callOption, double share, double strike,
                                   double riskFree, double time, double volatility) {
        if (callOption) {
            double cumDistribution1 = cumDistribution(d1(share, strike, riskFree, time, volatility));
            double cumDistribution2 = cumDistribution(d2(share, strike, riskFree, time, volatility));
            return share * cumDistribution1 - strike * Math.exp(-riskFree * time) * cumDistribution2;

        } else {
            double cumDistribution1 = cumDistribution(-d1(share, strike, riskFree, time, volatility));
            double cumDistribution2 = cumDistribution(-d2(share, strike, riskFree, time, volatility));
            return strike * Math.exp(-riskFree * time) * cumDistribution2 - share * cumDistribution1;

        }
    }

    private static double d1(double share, double strike, double riskFree,
                             double time, double volatility) {
        return (log(share / strike) + (riskFree + Math.pow(volatility, 2) / 2) * time)
                / (volatility * Math.sqrt(time));
    }

    private static double d2(double share, double strike, double riskFree,
                             double time, double volatility) {
        return d1(share, strike, riskFree, time, volatility) - volatility * Math.sqrt(time);

    }


    private static double erf(double x)
    {

        double a1 = 0.254829592;
        double a2 = -0.284496736;
        double a3 = 1.421413741;
        double a4 = -1.453152027;
        double a5 = 1.061405429;
        double p = 0.3275911;
        double t = 1 / (1 + p * abs(x));

        return 1 - ((((((a5 * t + a4) * t) + a3) * t + a2) * t) + a1) * t * Math.exp(-1 * x * x);
    }
    public static double cumDistribution(double z)
    {
        double sign = 1;
        if (z < 0) sign = -1;

        return 0.5 * (1.0 + sign * erf(Math.abs(z)/Math.sqrt(2)));
    }



}


