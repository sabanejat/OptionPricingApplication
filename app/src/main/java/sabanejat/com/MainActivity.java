package sabanejat.com;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    double sharePrice;
    double strikePrice;
    double riskFree;
    double vol;
    double time;


    TextView optionPrice;
    TextView optionStrikePrice;
    TextView riskFreeInterestRate;
    TextView volatility;

    Button dateDay;
    Button dateMonth;
    Button dateYear;

    Button calculate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        optionPrice = (TextView) findViewById(R.id.optionPrice);
        optionStrikePrice = (TextView) findViewById(R.id.optionStrikePrice);
        riskFreeInterestRate = (TextView) findViewById(R.id.riskFreeInterestRate);
        volatility = (TextView) findViewById(R.id.volatility);

        dateDay = (Button) findViewById(R.id.dateDay);
        dateMonth = (Button) findViewById(R.id.dateMonth);
        dateYear = (Button) findViewById(R.id.dateYear);

        calculate = (Button) findViewById(R.id.calculate);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePrice = Double.valueOf(optionPrice.getText().toString());
                strikePrice = Double.valueOf(optionStrikePrice.getText().toString());
                riskFree = Double.valueOf(riskFreeInterestRate.getText().toString());
                vol = Double.valueOf(volatility.getText().toString());
           //     time = Double.valueOf(expiration.getText().toString());
            }
        });

    }



    public static double callPrice(double sharePrice, double strikePrice, double riskFree, double vol, double time) {
        double d1 = (Math.log(sharePrice / strikePrice) + (riskFree + vol * vol / 2) * time) / (vol * Math.sqrt(time));
        double d2 = d1 - vol * Math.sqrt(time);
        return sharePrice * Gaussian.cdf(d1) - strikePrice * Math.exp(-riskFree * time) * Gaussian.cdf(d2);

    }

}