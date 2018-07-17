package ru.dimorinny.showcasesample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import ru.dimorinny.showcasecard.ShowCaseView;
import ru.dimorinny.showcasecard.position.BottomLeft;
import ru.dimorinny.showcasecard.position.BottomRight;
import ru.dimorinny.showcasecard.position.Center;
import ru.dimorinny.showcasecard.position.ShowCasePosition;
import ru.dimorinny.showcasecard.position.TopLeft;
import ru.dimorinny.showcasecard.position.TopLeftToolbar;
import ru.dimorinny.showcasecard.position.TopRight;
import ru.dimorinny.showcasecard.position.TopRightToolbar;
import ru.dimorinny.showcasecard.position.ViewPosition;
import ru.dimorinny.showcasecard.radius.Radius;
import ru.dimorinny.showcasecard.radius.ShowCaseRadius;
import ru.dimorinny.showcasecard.step.ShowCaseStepDisplayer;
import ru.dimorinny.showcasecard.step.ShowCaseStep;

public class MainActivity extends AppCompatActivity implements ShowCaseStepDisplayer.ViewClickedListener {

    private ScrollView scrollView;
    private View dummyViewToScrollTo;

    private Button topLeft;
    private Button topRight;
    private Button bottomLeft;
    private Button bottomRight;
    private Button topLeftToolbar;
    private Button topRightToolbar;
    private Button viewPosition;
    private Button listOfSteps;
    private Button button1;
    private Button button2;
    private Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollView = findViewById(R.id.activity_main);
        dummyViewToScrollTo = findViewById(R.id.dummy_view_to_scroll_to);

        initButtons();

        topLeft.setOnClickListener(v -> showTipWithPosition(new TopLeft()));
        topRight.setOnClickListener(v -> showTipWithPosition(new TopRight()));
        bottomLeft.setOnClickListener(v -> showTipWithPosition(new BottomLeft()));
        bottomRight.setOnClickListener(v -> showTipWithPosition(new BottomRight()));
        topLeftToolbar.setOnClickListener(v -> showTipWithPosition(new TopLeftToolbar()));
        topRightToolbar.setOnClickListener(v -> showTipWithPosition(new TopRightToolbar()));
        viewPosition.setOnClickListener(v -> showTipWithPosition(new ViewPosition(
                viewPosition
        )));
        listOfSteps.setOnClickListener(v -> displayListOfSteps());
    }

    private void displayListOfSteps() {

        new ShowCaseStepDisplayer.Builder(this)
                .withScrollView(scrollView)
                .withCustomLayout(R.layout.custom_showcase_layout)
                .setRadiusProportionalToView(true)
                .addStep(new ShowCaseStep(new Center(), "Center", "This is the center of the screen. Tap anywhere to continue."))
                .addStep(new ShowCaseStep(button1, "Button", "This is the button you just clicked."))
                .addStep(new ShowCaseStep(button2, "AutoScroll","A dummy item to auto-scroll to."))
                .addStep(new ShowCaseStep(button3, "TopLeft", "We end our showcase at the top button."))
                .withClickViewListener(this)
                .build().start();
    }

    @Override
    public boolean onViewClicked(ShowCaseView view) {
        Toast.makeText(getApplicationContext(), "ViewClicked" + view.getClass().getName(), Toast.LENGTH_LONG).show();
        return true;
    }

    private void initButtons() {
        topLeft = findViewById(R.id.top_left);
        topRight = findViewById(R.id.top_right);
        bottomLeft = findViewById(R.id.bottom_left);
        bottomRight = findViewById(R.id.bottom_right);
        topLeftToolbar = findViewById(R.id.top_left_toolbar);
        topRightToolbar = findViewById(R.id.top_right_toolbar);
        viewPosition = findViewById(R.id.view_position);
        listOfSteps = findViewById(R.id.list_of_steps);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
    }

    private void showTipWithPosition(ShowCasePosition position) {
        showTip(
                position,
                new Radius(186F)
        );
    }

    private void showTip(ShowCasePosition position, ShowCaseRadius radius) {
        new ShowCaseView.Builder(MainActivity.this)
                .withTypedPosition(position)
                .withTypedRadius(radius)
                .withContent("Title",
                        "This is hello world!"
                )
                .build()
                .show(MainActivity.this);
    }
}
