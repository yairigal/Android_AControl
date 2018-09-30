package yairigal.com.homeac;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton fab;
    ImageButton onoff,mode;
    ImageButton up, down;
    ProgressBar loader;
    FrameLayout mainLayout;
    TextView loadingText, temp;
    ListView listview;

    ACInstance globalData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = findViewById(R.id.fab);
        onoff = findViewById(R.id.onOffBtn);
        up = findViewById(R.id.upBtn);
        down = findViewById(R.id.dwnBtn);
        loader = findViewById(R.id.loadingSpinner);
        mainLayout = findViewById(R.id.mainLayout);
        loadingText = findViewById(R.id.loadingDataTextView);
        temp = findViewById(R.id.tempTV);
        mode = findViewById(R.id.modeBtn);
        listview = findViewById(R.id.itemsListView);

        setupViewsEvents();
        setupFAB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataFromArduino();
    }

    private void setupOnOffUISwitchBasedOnData() {
        if (globalData.on) {
            onoff.setImageResource(R.drawable.icon_on);
        } else {
            onoff.setImageResource(R.drawable.icon_onoff);
        }
    }

    private void setupTempUIBasedOnData() {
        temp.setText(globalData.temp + "°C");
        if (globalData.temp <= 20)
            temp.setTextColor(Color.parseColor("#7B1FA2"));
        else
            temp.setTextColor(Color.parseColor("#388E3C"));
    }

    private void setupViewsEvents() {
        onoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globalData.on = !globalData.on;
                sendDataAsync(globalData);
            }
        });

        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalData.acMode = 2 / globalData.acMode;
                sendDataAsync(globalData);
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalData.temp += 1;
                sendDataAsync(globalData);
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                globalData.temp -= 1;
                sendDataAsync(globalData);
            }
        });
    }

    private void getDataFromArduino() {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                loader.setVisibility(View.VISIBLE);
                loadingText.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                getData();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                loader.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);

                setupViews();
            }
        };
        task.execute();
    }

    private void getData() {
        try {
            globalData = WebRequests.getData();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * sends data to arduino, and returns the new data.
     * @param data
     * @return
     */
    private void sendData(ACInstance data) {
        try {
            globalData = WebRequests.sendData(data);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateData(SortedActions data){
        try {
            globalData = WebRequests.updateActions(data);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupViews() {
        setupOnOffUISwitchBasedOnData();
        setupTempUIBasedOnData();
        setupModeUIBasedOnData();
        listview.setAdapter(new ArrayAdapter<FutureAction>(getApplicationContext(), R.layout.item_layout, globalData.actions.getArray()) {

            @SuppressLint("SetTextI18n")
            @NonNull
            @Override
            public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    // inflate the layout
                    LayoutInflater inflater = (MainActivity.this).getLayoutInflater();
                    convertView = inflater.inflate(R.layout.item_layout, parent, false);
                }

                // object item based on the position
                final FutureAction objectItem = globalData.actions.get(position);

                // get the TextView and then set the text (item name) and tag (item ID) values
                TextView textViewItem = convertView.findViewById(R.id.contentTV);
                String first = "Turning ";
                String next;
                String last = " at " + objectItem.time.toString();

                if (objectItem.on)
                    next = "<font color='#388E3C'>ON</font>";
                else
                    next = "<font color='#D32F2F'>OFF</font>";
                textViewItem.setText(Html.fromHtml(first + next + last, Html.FROM_HTML_OPTION_USE_CSS_COLORS));
                Button deleteBtn = convertView.findViewById(R.id.deleteItemBtn);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        globalData.actions.remove(objectItem);
                        updateDataAsync(globalData.actions);
                    }
                });

                return convertView;
            }

            @Override
            public int getCount() {
                if (globalData.actions != null)
                    return globalData.actions.size();
                return 0;
            }
        });

    }

    private void setupModeUIBasedOnData() {
        if(globalData.acMode == 1){
            mode.setImageResource(R.drawable.icon_cold);
        }else
        if(globalData.acMode == 2){
            mode.setImageResource(R.drawable.icon_hot);
        }

    }

    private void setupFAB() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DateTimeContainer selectedDate = new DateTimeContainer();

                // Get Current Date
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog date = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayofmonth) {
                        selectedDate.selectedYear = year;
                        selectedDate.selectedMonth = month + 1;
                        selectedDate.selectedDay = dayofmonth;

                        // get current time
                        int mHour = c.get(Calendar.HOUR_OF_DAY);
                        int mMinute = c.get(Calendar.MINUTE);

                        final TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                selectedDate.selectedHour = i;
                                selectedDate.selectedMin = i1;
                                if(DateTimeContainer.now().after(selectedDate)){
                                    Toast.makeText(getApplicationContext(),"Time has passed...",Toast.LENGTH_LONG).show();
                                    return;
                                }
                                showOnOffDialog(selectedDate);
                            }
                        }, mHour, mMinute, true);
                        timePickerDialog.show();
                    }
                }, mYear, mMonth, mDay);
                date.show();
            }
        });
    }

    private void showOnOffDialog(final DateTimeContainer timeSelected) {
        // custom dialog
        final boolean turn[] = new boolean[1];
        turn[0] = false;
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.set_on_off_dialog);
        dialog.setTitle("Turn on or off?");

        // set the custom dialog components - text, image and button
        final Switch aSwitch = dialog.findViewById(R.id.onoffswitch);
        aSwitch.setText("turn off at " + timeSelected.toString() + "?");
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    aSwitch.setText("Turn on at " + timeSelected.toString() + " ?");
                else
                    aSwitch.setText("Turn off at " + timeSelected.toString() + " ?");
                turn[0] = b;
            }
        });

        Button dialogButton = dialog.findViewById(R.id.submitBtnDialog);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                FutureAction newAction = new FutureAction();
                newAction.on = turn[0];
                newAction.time = timeSelected;
                globalData.actions.add(newAction);
                updateDataAsync(globalData.actions);
            }
        });
        dialog.show();
    }

    private void sendDataAsync(final ACInstance data) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                loader.setVisibility(View.VISIBLE);
                loadingText.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                sendData(data);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                loader.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);

                setupViews();
            }
        };
        task.execute();
    }

    private void updateDataAsync(final SortedActions data) {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                loader.setVisibility(View.VISIBLE);
                loadingText.setVisibility(View.VISIBLE);
                mainLayout.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                updateData(data);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                loader.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);
                mainLayout.setVisibility(View.VISIBLE);

                setupViews();
            }
        };
        task.execute();
    }
}




