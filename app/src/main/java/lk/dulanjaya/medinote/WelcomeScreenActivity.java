package lk.dulanjaya.medinote;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import lk.dulanjaya.medinote.model.RepoParams;
import lk.dulanjaya.medinote.model.UiToolkitManager;

public class WelcomeScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UiToolkitManager.setStatusBarAndNavigationBarColor(WelcomeScreenActivity.this, R.color.neutral_color_green, R.color.primary_border_color);

        Button continueButton = findViewById(R.id.welcomeScreenButtonContinue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UiToolkitManager.ActivityManager.navigateToActivity(WelcomeScreenActivity.this, new SignInActivity());
            }
        });

        TextView versionTextView = findViewById(R.id.welcomeScreenTextView4);
        versionTextView.setText(RepoParams.VERSION_NUMBER);
    }
}