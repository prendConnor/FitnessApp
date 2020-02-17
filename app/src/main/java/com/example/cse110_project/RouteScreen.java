package com.example.cse110_project;
import android.app.Activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.cse110_project.Firebase.MyCallback;
import com.example.cse110_project.Firebase.RouteCollection;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.opencensus.resource.Resource;

import static java.lang.Integer.parseInt;
import static java.lang.Integer.valueOf;

public class RouteScreen extends AppCompatActivity {
    private String fitnessServiceKey = "GOOGLE_FIT";
    private BottomNavigationView bottomNavigationView;
    private Button addRoute;
    private Button expandBtn;
    private RelativeLayout invis;

    private ArrayList<Route> currentRoutes;
    private static int routesNum = 0;
    public static boolean testing = false;

    private Route dummyRoute;

    private LinearLayout routesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_screen);

        currentRoutes = new ArrayList<Route>();

        final Intent intent = new Intent(this, RouteFormScreen.class);

        routesContainer = findViewById(R.id.routesContainer);

        addRoute = findViewById(R.id.addRouteBtn);
        addRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        RouteCollection rc = new RouteCollection();
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        rc.getRoutes(deviceID, new MyCallback() {
                    @Override
                    public void getRoutes(ArrayList<Route> routes) {

                        if(testing) {
                            Log.d("ROUTE SCREEN: ", "adding testing route");
                            Route testRoute = new Route("Regular Walk", "Stressman and Bragg");
                            //routes.clear();
                            //routes.add(testRoute);
                            addElement(testRoute);
                            return;
                        }

                        currentRoutes = routes;
                        routesNum = currentRoutes.size();
                        Log.d("TAG", "SIZE IS = " + routes.size());

                        for (int i = 0; i < routes.size(); i++) {
                            Log.d("TAG", "ROUTE NAME: " + routes.get(i).getName());

                            // TODO : CALLS METHOD THAT BUILDS THE ROUTE HERE

                            dummyRoute = routes.get(i);

                            addElement(dummyRoute);

                        }
                    }
                });


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectFragment(item);
                return false;
            }
        });

    }

    public void toggle_contents(Button exp, RelativeLayout hide){
        if( hide.isShown()) {
            hide.setVisibility(View.GONE);
            exp.setText("Expand");
        } else {
            hide.setVisibility(View.VISIBLE);
            exp.setText("Hide");
        }
    }


    public void addElement(Route routeEntry){
        int fontColor = Color.parseColor("#FFFFFFFF");

        /* Rounded button drawable */
        Drawable draw = getDrawable(R.drawable.rounded_edges);

        /* Linear Layouts containers */

        /* route contain --> outer most layer */
        LinearLayout routeContain = findViewById(R.id.routeContain);

        /* container --> holds all views within routeContain */
        LinearLayout container = new LinearLayout(this);

        /* container parameter, setting height and width */
        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        /* layout configuration for container */
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPaddingRelative(30, 25, 30, 25);
        container.setLayoutParams(containerParams);
        container.setBackground(draw);

        /* title of each route */
        LinearLayout titleEntry = new LinearLayout(this);
        titleEntry.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        ));
        titleEntry.setOrientation(LinearLayout.HORIZONTAL);

        /* title label text view creation with styling */
        TextView title = new TextView(this);
        title.setText("Route Name:");
        title.setTextColor(fontColor);
        title.setTextSize(20);
        title.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 0.6f
        ));

        /* title value text view creation with styling */
        TextView titleDisplay = new TextView(this);
        titleDisplay.setText(routeEntry.getName());
        titleDisplay.setTextColor(fontColor);
        titleDisplay.setTextSize(20);
        titleDisplay.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f
        ));
        titleDisplay.setId(valueOf("1404"));

        /* fav button */
        ImageView favDisplay =  new ImageView(this);
        favDisplay.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        Drawable favImageWhite = getDrawable(R.drawable.ic_favorite_border_black_24dp);
        Drawable favImageRed = getDrawable(R.drawable.ic_favorite_border_red);
        if (routeEntry.getFavorite()){
            favDisplay.setImageDrawable(favImageRed);
        } else {
            favDisplay.setImageDrawable(favImageWhite);
        }

        /* start position row */
        LinearLayout startEntry = new LinearLayout(this);
        titleEntry.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        ));
        startEntry.setOrientation(LinearLayout.HORIZONTAL);


        TextView start = new TextView(this);
        start.setText("Start Position:");
        start.setTextColor(fontColor);
        start.setTextSize(20);
        start.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 0.6f
        ));

        TextView startDisplay = new TextView(this);
        startDisplay.setText(routeEntry.getStartingPoint());
        startDisplay.setTextColor(fontColor);
        startDisplay.setTextSize(20);
        startDisplay.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 0.4f
        ));


        int fontSmall = 14;
        int smallColor = Color.parseColor("#FF868686");

        /* HIDDEN EXPANDABLE SECTION -- containing other features */
        // TODO : check whether last completion stats exist and insert to row

        final LinearLayout hidden = new LinearLayout(this);
        hidden.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        hidden.setOrientation(LinearLayout.VERTICAL);
        hidden.setVisibility(View.GONE);

        /* TAG HOLDER LAYOUT */
        LinearLayout tagHolder = new LinearLayout(this);
        tagHolder.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tagHolder.setOrientation(LinearLayout.HORIZONTAL);
        TextView tags = new TextView(this);
        tags.setText("Tags:");


        tags.setTextSize(20);
        tags.setTextColor(fontColor);

        TextView tagsDisplay = new TextView(this);


        /* PUT TAGS TO STRINGS */
        if(routeEntry.getTags() != null) {
            Object[] tagString = parseTags(routeEntry.getTags());
            tagsDisplay.setText(Arrays.toString(tagString));
            tagsDisplay.setTextSize(fontSmall);
            tagsDisplay.setTextColor(smallColor);
            tagsDisplay.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    0.4f
            ));
        }



        /* LAST TIME COMPLETION */
        LinearLayout timeHolder = new LinearLayout(this);
        timeHolder.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        timeHolder.setOrientation(LinearLayout.HORIZONTAL);

        TextView time = new TextView(this);
        time.setText("Time:");
        time.setTextSize(fontSmall);
        time.setTextColor(smallColor);
        time.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.6f
        ));
        TextView timeDisplay = new TextView(this);
        timeDisplay.setText(routeEntry.getLastCompletedTime());
        timeDisplay.setTextSize(fontSmall);
        timeDisplay.setTextColor(smallColor);
        timeDisplay.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.6f
        ));
        timeHolder.addView(time);
        timeHolder.addView(timeDisplay);


        /* LAST COMPLETED DISTANCE LAYOUT */
        LinearLayout distHolder = new LinearLayout(this);
        distHolder.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        distHolder.setOrientation(LinearLayout.HORIZONTAL);

        TextView distance = new TextView(this);
        distance.setText("Distance:");
        distance.setTextSize(fontSmall);
        distance.setTextColor(smallColor);
        distance.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.6f
        ));

        TextView distanceDisplay = new TextView(this);
        distanceDisplay.setText(routeEntry.getLastCompletedDistance());
        distanceDisplay.setTextSize(fontSmall);
        distanceDisplay.setTextColor(smallColor);
        distanceDisplay.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.4f
        ));
        distHolder.addView(distance);
        distHolder.addView(distanceDisplay);


        /* LAST COMPLETED STEPS LAYOUT */
        LinearLayout stepHolder = new LinearLayout(this);
        stepHolder.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        stepHolder.setOrientation(LinearLayout.HORIZONTAL);

        TextView steps = new TextView(this);
        steps.setText("Steps:");
        steps.setTextSize(fontSmall);
        steps.setTextColor(smallColor);
        steps.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.6f
        ));

        TextView stepDisplay = new TextView(this);
        stepDisplay.setText(routeEntry.getLastCompletedSteps());
        stepDisplay.setTextSize(fontSmall);
        stepDisplay.setTextColor(smallColor);
        stepDisplay.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.4f
        ));
        stepHolder.addView(steps);
        stepHolder.addView(stepDisplay);


        //     BUTTON SECTION

        /* EXPANDABLE BUTTON FOR MORE FEATURES */
        final Button expand = new Button(this);
        expand.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( hidden.isShown()) {
                    hidden.setVisibility(View.GONE);
                    expand.setText("Expand");
                } else {
                    hidden.setVisibility(View.VISIBLE);
                    expand.setText("Hide");
                }
            }
        });

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        Drawable buttonBack = getDrawable(R.drawable.btn_rounded);

        expand.setBackground(buttonBack);
        expand.setText("Expand");
        expand.setTextSize(14);
//        int black = Color.parseColor("#ff000000");
        expand.setTextColor(fontColor);
        expand.setVisibility(View.VISIBLE);

        LinearLayout btnHolder = new LinearLayout(this);

        /* START ROUTE BUTTON */
        final Button newButton = new Button(RouteScreen.this);
        newButton.setBackground(buttonBack);
        newButton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        newButton.setText("Start this Route");
        newButton.setTag(routeEntry);
        newButton.setVisibility(View.VISIBLE);
        newButton.setTextColor(fontColor);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RouteScreen.this, WalkScreen.class);
                Route dummy = (Route) view.getTag();
                intent.putExtra("routeID", dummy.getId());
                intent.putExtra("routeName", dummy.getName());
                intent.putExtra("routeStart", dummy.getStartingPoint());
                intent.putExtra("routeNotes", dummy.getNotes());
                intent.putExtra("lastCompletedTime", dummy.getLastCompletedTime());
                intent.putExtra("lastCompletedSteps", dummy.getLastCompletedSteps());
                intent.putExtra("lastCompletedDistance", dummy.getLastCompletedDistance());
                startActivity(intent);
            }
        });

        btnParams.setMargins(150, 50, 150, 0);

        btnHolder.setLayoutParams(btnParams);
        newButton.setLayoutParams(btnParams);



        // ADD COMPLETED ELEMENTS
        btnHolder.addView(expand);

        tagHolder.addView(tags);
        tagHolder.addView(tagsDisplay);
        hidden.addView(tagHolder);
        hidden.addView(timeHolder);
        hidden.addView(distHolder);
        hidden.addView(stepHolder);
        hidden.addView(newButton);
        startEntry.addView(start);
        startEntry.addView(startDisplay);
        titleEntry.addView(title);
        titleEntry.addView(titleDisplay);
        titleEntry.addView(favDisplay);
        container.addView(titleEntry);
        container.addView(startEntry);
        container.addView(hidden);
        container.addView(btnHolder);
        routeContain.addView(container);
    }


    public Object[] parseTags(boolean[] tags) {
        //boolean[] tags ={out, loop, flat, hills, even, rough, street, trail, easy, medium, hard};
        List<String> tagList = new ArrayList<String>();


        if (tags[0]) {
            tagList.add("Out&Back");
        } else if (tags[1]) {
            tagList.add("Loop");
        }

        if (tags[2]) {
            tagList.add("Flat");
        } else if (tags[3]){
            tagList.add("Hills");
        }

        if (tags[4]){
            tagList.add("Even");
        } else if (tags[5]){
            tagList.add("Rough");
        }

        if (tags[6]){
            tagList.add("Street");
        } else if (tags[7]){
            tagList.add("Trail");
        }

        if (tags[8]){
            tagList.add("Easy");
        } else if (tags[9]){
            tagList.add("Medium");
        } else if (tags[10]){
            tagList.add("Hard");
        }


        return tagList.toArray();
    }

    private void selectFragment(MenuItem item){

        Intent newIntent = new Intent(this, this.getClass());
        switch(item.getItemId()) {
            case R.id.navigation_home:
                newIntent = new Intent(this, HomeScreen.class);
                newIntent.putExtra(HomeScreen.FITNESS_SERVICE_KEY, fitnessServiceKey);
                startActivity(newIntent);
                break;
            case R.id.navigation_walk:
                newIntent = new Intent(this, WalkScreen.class);
                startActivity(newIntent);
                break;
            default:
                break;
        }
    }

    public static int getRouteNumber() {
        return routesNum;
    }

}
