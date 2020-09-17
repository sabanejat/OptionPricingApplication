package sabanejat.com;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import static java.lang.Math.abs;
import static sabanejat.com.Constant.readFromFile;
import static sabanejat.com.Constant.writeToFile;




public class Fourth_Frag extends Fragment {


    double sharePrice;
    double strikePrice;
    double riskFree;
    double vol;
    double time;
    double div;


    EditText optionPrice;
    EditText optionStrikePrice;
    EditText optionDate;
    EditText riskFreeInterestRate;
    EditText volatility;
    EditText numOfSteps;

    Button calculate;

    Switch aSwitch;

    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fourth, container, false);


        optionPrice = view.findViewById(R.id.optionPrice);
        optionStrikePrice = view.findViewById(R.id.optionStrikePrice);
        riskFreeInterestRate = view.findViewById(R.id.riskFreeInterestRate);
        volatility = view.findViewById(R.id.volatility);
        optionDate = view.findViewById(R.id.optionDate);
        numOfSteps = view.findViewById(R.id.numOfSteps);


        calculate = view.findViewById(R.id.calculate);
        aSwitch = view.findViewById(R.id.callSwitch);


        calculate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sharePrice = Double.parseDouble(optionPrice.getText().toString());
                strikePrice = Double.parseDouble(optionStrikePrice.getText().toString());
                riskFree = Double.parseDouble(riskFreeInterestRate.getText().toString());
                vol = Double.parseDouble(volatility.getText().toString());
                time = Double.parseDouble(optionDate.getText().toString());
                div = Double.parseDouble(numOfSteps.getText().toString());

                if (aSwitch.isChecked()) {
                    double callOptionPrice = calculate(sharePrice, strikePrice, riskFree / 100, div / 100, vol / 100, time);
                    showToast(String.valueOf(callOptionPrice));

                    try {
                        String data = readFromFile(getActivity()) + "3,"
                                +"BAW , share price: " + sharePrice + ", strike price: " + strikePrice
                                + ", risk free interest rate: " + riskFree  + ", time: " + time + ", volatility: " + vol
                                + ", dividends: "+div +" Call option price: "
                                + calculate( sharePrice, strikePrice, riskFree / 100, div/100, vol/100, time)+ "-";
                        writeToFile(data, getActivity());



                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {
                    double putOptionPrice = calculatePut(sharePrice, strikePrice, riskFree / 100, div / 100, vol / 100, time);
                    showToast(String.valueOf(putOptionPrice));

                    try {
                        String data = readFromFile(getActivity()) + "3,"
                                +"BAW , share price: " + sharePrice + ", strike price: " + strikePrice
                                + ", risk free interest rate: " + riskFree  + ", time: " + time + ", volatility: " + vol
                                + ", dividends: "+div +" Put option price: "
                                + calculate( sharePrice, strikePrice, riskFree / 100, div/100, vol/100, time)+ "-";
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


    public double calculate(double share, double strike, double riskFree,
                            double dividend, double vol, double time) {

        double baw;
        double costOfCarry = riskFree - dividend;
        double volSqr = (vol * vol);


        double M = 2 * riskFree / volSqr;
        double N = 2 * dividend / volSqr;
        double K = 1 - Math.exp(-riskFree * time);
        double q2 = (-(N - 1) + Math.sqrt(((N - 1) * (N - 1)) + (4 * M / K))) * 0.5;


        double q2Inf = ((-N - 1) + Math.sqrt(((N - 1) * (N - 1)) + 4 * M))* 0.5 ;
        double sStarInf = strike / (1 - 1 / q2Inf);
        double h2 = -(costOfCarry * time + 2 * vol * Math.sqrt(time)) * (strike / (sStarInf - strike));
        double sSeed = strike + (sStarInf - strike) * (1 - Math.exp(h2));



        final double ACC = Math.pow(10, -6);

        int numIterations = 0;

        double Si = sSeed;
        double g = 1;
        double g_prime = 1.0;

        double d1;
        double d2;
        double bls;


        while ((Math.abs(g) > ACC) && (Math.abs(g_prime) > ACC)
                && (numIterations++ < 100) && (Si > 0.0) && (costOfCarry < riskFree)) {


            d1 = (Math.log(share / strike) + (costOfCarry + volSqr / 2) * time) / (vol * Math.sqrt(time));
            d2 = d1 - (vol * Math.sqrt(time));
            bls = Si * Math.exp((costOfCarry - riskFree) * time) * cumNormal(d2) - strike * Math.exp(-(riskFree * time)) * cumNormal(d2);
            d1 = (Math.log(Si / strike) + (costOfCarry + volSqr / 2) * time) / (vol * Math.sqrt(time));


            g = (1 - 1 / q2) * Si - strike - bls + (1 / q2) * Si * Math.exp((dividend - riskFree) * time) * cumNormal(d1);
            g_prime = (1 - 1 / q2) * (1 - Math.exp((costOfCarry - riskFree) * time) * cumNormal(d1)) +
                    (1 / q2) * (Math.exp((costOfCarry - riskFree) * time) * calcN(d1)) * (1 / (vol * Math.sqrt(time)));
            Si = Si - (g / g_prime);
        }


        double S_star;
        if (Math.abs(g) > ACC) {
            S_star = sSeed;
        } else {
            S_star = Si;
        }

        double C ;

        d1 = (Math.log(share / strike) + (costOfCarry + volSqr / 2) * time) / (vol * Math.sqrt(time));
        d2 = d1 - (vol * Math.sqrt(time));
        bls = share * Math.exp((costOfCarry - riskFree) * time) * cumNormal(d1) - strike * Math.exp(-(riskFree * time)) * cumNormal(d2);

        if (share >= S_star) {
            C = share - strike;
        } else {

            d1 = (strike * Math.exp(S_star) + (costOfCarry + volSqr / 2) * time) / (vol * Math.sqrt(time));



            double A2 = (1 - Math.exp((costOfCarry - riskFree) * time) * cumNormal(d1)) * (S_star / q2);
            C = bls + A2 * Math.pow((share / S_star), q2);


        }


        if (C > bls) {
            baw = C;
        }

        if (costOfCarry >= riskFree) {
            baw = bls;
        } else if (bls > C) {
            baw = bls;
        } else {
            baw = Math.max(C, bls);
        }return baw;


    }

    public double calculatePut(double share, double strike, double riskFree,
                               double dividend, double vol, double time) {
        double baw;
        double costOfCarry = riskFree-dividend;
        double volSqr = (vol*vol);

        double M = 2*riskFree/volSqr;
        double N = 2*dividend/volSqr;
        double K = 1-Math.exp(-riskFree*time);
        double q1 = (-(N-1)-Math.sqrt(((N-1)*(N-1))+(4*M/K)))*0.5;

        double q1_inf = ((-N-1)+Math.sqrt(((N-1)*(N-1))+4*M))*0.5;
        double S_star_inf = strike/(1-1/q1_inf);
        double h1 = (costOfCarry*time-2*vol*Math.sqrt(time))*(strike/(strike-S_star_inf));
        double S_seed = S_star_inf+(strike-S_star_inf)*Math.exp(h1);


        final double ACC = Math.pow(10,-6);

        int no_iterations = 0;

        double Si = S_seed;
        double g = 1;
        double g_prime = 1.0;

        double d1;
        double d2;
        double bls;

        while ((abs(g)>ACC) && (abs(g_prime)>ACC)
                && (no_iterations++<100) && (Si>0.0) && (costOfCarry<riskFree)) {

            d1 = (Math.log(share/strike)+(costOfCarry+volSqr/2)*time)/(vol*Math.sqrt(time));
            d2 = d1-(vol*Math.sqrt(time));
            bls = Si*Math.exp((costOfCarry-riskFree)*time)*calcN(d2)- strike*Math.exp(-(riskFree*time))*calcN(d2);
            d1 = (Math.log(Si/strike)+(costOfCarry+volSqr/2)*time)/(vol*Math.sqrt(time));


            g = strike-share-bls+(share/q1)*(1-Math.exp((costOfCarry-riskFree)*time));
            g_prime = ((1/q1)-1)*(1-Math.exp((costOfCarry-riskFree)*time)*calcN(d1))+
                    (1/q1)*Math.exp((costOfCarry-riskFree)*time)*(1/(vol*Math.sqrt(time)))*calcN(d1);



            Si = Si-(g/g_prime);

        };

        double S_star = 0;

        if (abs(g)>ACC) {
            S_star = S_seed;
        }
        else {
            S_star = Si;
        }

        double C = 0;

        d1 = (Math.log(share/strike)+(costOfCarry+volSqr/2)*time)/(vol*Math.sqrt(time));
        d2 = d1-(vol*Math.sqrt(time));
        bls = strike*Math.exp(-riskFree*time)*calcN(d2)-share*Math.exp((costOfCarry-riskFree)*time)*calcN(d1);


        if (share <= S_star) {
            C = strike-share;
        }
        else {
            d1 = (strike*Math.exp(S_star)+(costOfCarry+volSqr/2)*time)/(vol*Math.sqrt(time));

            double A1 = -(S_star/q1)*(1-Math.exp((costOfCarry-riskFree)*time)*calcN(d1));
            C = bls+A1*Math.pow((share/S_star),q1);
        };

        if (C > bls) {
            baw = C;
        }
        if (costOfCarry >= riskFree) {
            baw = bls;
        }
        else if (bls > C) {
            baw = bls;
        } else {
        baw = Math.max(C,bls);
          }return baw;


}

    double cumNormal(double n) {

        double result;

        double x = Math.abs(n);
        double l = 0.33267d;
        double a1 = 0.4361836d;
        double a2 = -0.1201676d;
        double a3 = 0.9372980d;
        double k = 1 / (1 + (x * l));

        double N = (1 / Math.sqrt(Math.PI * 2));
        N *= Math.exp(-(Math.pow(x, 2) / 2));
        result = N * ((a1 * k) + (a2 * Math.pow(k, 2)) + (a3 * Math.pow(k, 3)));
        result = 1 - result;
        if (n >= 0) {
            return result;
        } else {
            return (1 - result);
        }

    }

    double calcN(double x) {
        return (1 / (Math.sqrt(2 * Math.PI))) * Math.exp(-Math.pow(x, 2) / 2);
    }
}

