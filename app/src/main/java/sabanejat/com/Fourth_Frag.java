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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    EditText riskFreeInterestRate;
    EditText volatility;
    EditText optionDate;
    EditText dividend;

    Button calculate;

    Switch aSwitch;

    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fourth, container, false);


        optionPrice = view.findViewById(R.id.optionPrice);
        optionStrikePrice = view.findViewById(R.id.optionStrikePrice);
        riskFreeInterestRate = view.findViewById(R.id.riskFreeInterestRate);
        volatility = view.findViewById(R.id.volatility);
        optionDate = view.findViewById(R.id.optionDate);
        dividend = view.findViewById(R.id.dividends);


        calculate = view.findViewById(R.id.calculate);
        aSwitch = view.findViewById(R.id.callSwitch);


        calculate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sharePrice = Double.parseDouble(optionPrice.getText().toString());
                strikePrice = Double.parseDouble(optionStrikePrice.getText().toString());
                riskFree = Double.parseDouble(riskFreeInterestRate.getText().toString());
                vol = Double.parseDouble(volatility.getText().toString());
                time = Double.parseDouble(optionDate.getText().toString());
                div = Double.parseDouble(dividend.getText().toString());

                if (aSwitch.isChecked()) {
                    double callOptionPrice = baw_function(sharePrice,strikePrice,riskFree,div,vol,time);

                } else {
                    double callOptionPrice = baw_function(sharePrice,strikePrice,riskFree,div,vol,time);
                }


                try {

                    String data = readFromFile(getActivity()) + "2," + sharePrice + "," + strikePrice + "," + riskFree / 100 + "," + time + ","
                            + vol / 100 + "," + div + "," + aSwitch.isChecked() + "," + baw_function(sharePrice,strikePrice,riskFree,div,vol,time) + "-";
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



        public double baw_function(double s0,
                            double strike,
                            double rfr,
                            double div,
                            double sigma,
                            double tau) {

            double coc = rfr - div;
            double sigma_sqr = (sigma * sigma);

            double N = 2 * div / sigma_sqr;
            double M = 2 * rfr / sigma_sqr;
            double K = 1 - Math.exp(-rfr * tau);

            double q2 = (-(N - 1) + Math.sqrt(((N - 1) * (N - 1)) + (4 * M / K))) * 0.5;
            double q2_inf = 0.5 * ((-N - 1) + Math.sqrt(((N - 1) * (N - 1)) + 4 * M));
            double S_star_inf = strike / (1 - 1 / q2_inf);
            double h2 = -
                    (coc * tau + 2 * sigma * Math.sqrt(tau)) * (strike / (S_star_inf - strike));
            double S_seed = strike + (S_star_inf - strike) * (1 - Math.exp(h2));

            final double ACC = Math.pow(10, -6);

            int no_iterations = 0; // iterate on S to find S_star, using Newton steps
            double Si = S_seed;
            double g = 1;
            double g_prime = 1.0;

            double d1;
            double d2;
            double bls;


            while ((Math.abs(g) > ACC) && (Math.abs(g_prime) > ACC)
                    && (no_iterations++ < 100) && (Si > 0.0) && (coc < rfr)) {

                d1 =
                        (Math.log(s0 / strike) + (coc + sigma_sqr / 2) * tau) / (sigma * Math.sqrt(tau));
                d2 = d1 - (sigma * Math.sqrt(tau));
                bls = Si * Math.exp((coc - rfr) * tau) * calcN(d2) -
                        strike * Math.exp(-(rfr * tau)) * calcN(d2);
                d1 =
                        (Math.log(Si / strike) + (coc + sigma_sqr / 2) * tau) / (sigma * Math.sqrt(tau));

                g = (1 - 1 / q2) * Si - strike - bls + (1 / q2) * Si * Math.exp((div - rfr) * tau) * calcN(d1);
                g_prime = (1 - 1 / q2) * (1 - Math.exp((coc - rfr) * tau) * calcN(d1)) +
                        (1 / q2) * (Math.exp((coc - rfr) * tau) * calc_n(d1)) * (1 / (sigma * Math.sqrt(tau)));

                Si = Si - (g / g_prime);
                System.out.println("number of iterations_call : " +
                        no_iterations);
            }
            ;

            double S_star = 0;

            if (Math.abs(g) > ACC) {
                S_star = S_seed;
            } else {
                S_star = Si;
            }

            double C = 0;

            d1 =
                    (Math.log(s0 / strike) + (coc + sigma_sqr / 2) * tau) / (sigma * Math.sqrt(tau));
            d2 = d1 - (sigma * Math.sqrt(tau));
            bls = s0 * Math.exp((coc - rfr) * tau) * calcN(d1) -
                    strike * Math.exp(-(rfr * tau)) * calcN(d2);

            if (s0 >= S_star) {
                C = s0 - strike;

            } else {
                d1 =
                        (strike * Math.exp(S_star) + (coc + sigma_sqr / 2) * tau) / (sigma * Math.sqrt(tau
                        ));
                double A2 = (1 - Math.exp((coc - rfr) * tau) * calcN(d1)) * (S_star / q2);
                C = bls + A2 * Math.pow((s0 / S_star), q2);

            };
            return C;


        }

        double calcN(double n) {
            // Calculates cumulative normal probability distribution for
            // variable with mean of zero and standard deviation of one

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

        double calc_n(double x) {
            double result;
            // Calculates univariate normal density funftion n(x)
            double n = (1 / (Math.sqrt(2 * Math.PI))) * Math.exp(-
                    Math.pow(x, 2) / 2);
            return n;
        }
    }

