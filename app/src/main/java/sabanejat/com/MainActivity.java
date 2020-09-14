package sabanejat.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;


import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFragment(new First_Frag());
        BottomNavigationView bottomNavigationView =  findViewById(R.id.MainActivity_BottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_calendar:
                        Fragment First = new First_Frag();
                        loadFragment(First);
                        break;
                    case R.id.action_dollar:
                        Fragment Second = new Second_Frag();
                        loadFragment(Second);
                        break;
                    case R.id.action_percent:
                        Fragment Third = new Third_Frag();
                        loadFragment(Third);
                        break;
                    case R.id.action_five:
                        Fragment Fourth = new Fourth_Frag();
                        loadFragment(Fourth);
                        break;
                    case R.id.action_history:
                        Fragment history = new History();
                        loadFragment(history);
                        break;
                }
                return true;
            }
        });

    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.MainActivity_Frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}