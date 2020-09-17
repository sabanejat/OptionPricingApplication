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
                    List<Double> results = calculate(sharePrice, strikePrice, vol / 100, riskFree / 100, time, nSims);
                    callOptionPrice = results.get(0);
                    showToast(String.valueOf(callOptionPrice));

                    try {
                        String data = readFromFile(getActivity()) + "2,"
                                +"Monte Carlo , share price: " + sharePrice + ", strike price: " + strikePrice
                                + ", risk free interest rate: " + riskFree  + ", time: " + time + ", volatility: " + vol
                                + ", number of sims: "+nSims +" Call option price: "
                                + calculate( sharePrice, strikePrice, vol / 100, riskFree/100, time, nSims)+ "-";
                        writeToFile(data, getActivity());



                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    List<Double> results = calculate(sharePrice, strikePrice, vol / 100, riskFree / 100, time, nSims);
                    callOptionPrice = results.get(1);
                    showToast(String.valueOf(callOptionPrice));

                    try {
                        String data = readFromFile(getActivity()) + "2,"
                                +"Monte Carlo , share price: " + sharePrice + ", strike price: " + strikePrice
                                + ", risk free interest rate: " + riskFree  + ", time: " + time + ", volatility: " + vol
                                + ", number of sims: "+nSims +" Put option price: "
                                + calculate( sharePrice, strikePrice, vol / 100, riskFree/100, time, nSims)+ "-";
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
                                  double riskFree, double time, int numSims) {
        List<Double> results = new ArrayList<>();


        double disFactor = Math.exp(-riskFree * time);
        double sumCallPayoffs = 0.0;
        double sumPutPayoffs = 0.0;
        Random random = new Random();

        for (int i = 0; i <= numSims; i++) {
            double nextGaussian = random.nextGaussian();
            double St = share * Math.exp(((riskFree - 0.5 * Math.pow(vol, 2)) * time) +
                    (vol * Math.sqrt(time)) * nextGaussian);
            sumCallPayoffs += callPayOff(St, strike);
            sumPutPayoffs += putPayOff(St, strike);
        }

        double callOptionPrices = disFactor * sumCallPayoffs / numSims;
        double putOptionPrices = disFactor * sumPutPayoffs / numSims;


        results.add(callOptionPrices);
        results.add(putOptionPrices);
        return results;
    }

    private double putPayOff(double share, double strike) {
        return Math.max(strike - share, 0);
    }

    private double callPayOff(double share, double strike) {
        return Math.max(share - strike, 0);
    }

}

