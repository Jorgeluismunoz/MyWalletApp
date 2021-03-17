//Jorge Munoz - Final project
package com.example.projectoFinal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recycleviewlist.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/*
    WalletApp is a program develop in Android Studio.
    This is a personal accounting app that allows the user to have a control of their finance.
 */

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerVier;
    private RecyclerView.Adapter adapter;
    private ArrayList<MonthAccounting> monthAccounting;
    private ArrayList<Integer> myYarsInList;
    private MonthAccounting item;
    private MonthAccounting itemUpdate; //This field will store any change made in in other activity.

    //This field are used for cotral which record store in the program.
    private TextView current_date;
    private String controlCurrentMonth;
    private int monthNum, yearNum;

    //This fields are used by pie and bar charts.
    private BarChart barChart;
    private PieChart pieChart;
    private int monthIndex = 0; //Wich month will be showed in pieChard
    private int yearIndex = 0; //Wich year will be showed in barChard
    private TextView noDataTV;
    private ConstraintLayout noDataContainer;
    private boolean chartStartPie = true; //To know wich chart open when reload ativity.
    private TextView mTitle;
    private TextView mAmountI;
    private TextView mAmountE;
    ArrayList<String> labelsName;
    ArrayList<BarEntry> barEntryArrayList;

    //Small imgs will confrim or cancel add process.
    private ImageView yesButton;
    private ImageView noButton;
    private Toast mToast; //Create to avoid collision

    /*
        In this main activity run the home activity program that shows a barChart(years) and pieChart(Month)
        With a buttom the user may add ney months registers where stores differents transaction.
        Each month is store in an ArrayList and when the user click in each recyclerView position
        item well open other activity with details of specific months record.

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set icon next to title of project
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
        //----------------------------------

        //Set initial feature of recyclerView
        recyclerVier = findViewById(R.id.RecyclerViewId);
        recyclerVier.setHasFixedSize(true);
        recyclerVier.setLayoutManager(new LinearLayoutManager(this));
        //---------------

        //Assign field to elemente in xml
        current_date = findViewById(R.id.dateSelectedID);
        yesButton= findViewById(R.id.saveID);
        noButton = findViewById(R.id.cancelID);

        //check if detailActivity has something for us.
        itemUpdate = (MonthAccounting) getIntent().getSerializableExtra("myObjectUpDate");
        loadData();
        //---------------

        //Assig charts to fields and run pieChart
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);
        noDataTV = findViewById(R.id.noDataID);
        noDataContainer = findViewById(R.id.noDataContainer);
        mTitle = findViewById(R.id.titleID);
        mAmountI = findViewById(R.id.AmountI);
        mAmountE = findViewById(R.id.amountE);
        //--------------

        //Starts running recyclerView and charts with data gotten.
        runRecyclerView();
        runCharts();


    }
    //This method create an Bar chart with the ArrayList elements.
    public void runBarChart(View view) {
        chartStartPie = false; //When call runBarChart method change to false this boolean. it means that when start the activity again will show this chart first.
        noDataTV.setText("");
        float income = 0;
        float expense = 0;
        float totalIncomeYear = 0;
        float totalExpenseYear = 0;
        mTitle.setText("");
        mAmountI.setText("");
        mAmountE.setText("");

        barEntryArrayList = new ArrayList<>();; //The data that will show bar chart
        labelsName = new ArrayList<>(); // Months of year List.
        int[] colorClassArray = new int[]{  Color.parseColor("#FF72C70F"), /* Color.parseColor("#F00505")*/}; //in the case the program want to show two colum for month assign different colors.
        String[] labelsDescription = new String[]{ "Net Income"};
        //Assign new values to ArrayList.
        myYarsInList = new ArrayList<Integer>();

        //If the arrayList is empty show no data contains infromation
        if ( monthAccounting.size() == 0 ) {
            barChart.setVisibility(View.GONE);
            noDataContainer.setVisibility(View.VISIBLE);
            noDataTV.setText("");
        }
        else {
            //Frist loop check all years that will be displayed.
            for(int i = 0; i < monthAccounting.size(); i++) {
                if (!myYarsInList.contains(monthAccounting.get(i).getYear()))
                    myYarsInList.add(monthAccounting.get(i).getYear());
                labelsName.add(monthAccounting.get(i).getMonth().substring(0, 3));
            }
            //Second loop assign values for each mont in specific year.
            for(int i = 0; i <  monthAccounting.size() ; i++)
            {
                if (monthAccounting.get(i).getYear() == myYarsInList.get(yearIndex) && monthAccounting.get(i).transtactions.size() > 0 ) { //Just add specific year.
                        income =  (float) monthAccounting.get(i).getTotalIncome(); //check double result
                        expense =  (float) monthAccounting.get(i).getTotalExpense(); //check double result
                        barEntryArrayList.add(new BarEntry(monthAccounting.size()-1 - i, income));
                        totalIncomeYear += income;
                        totalExpenseYear += expense;
                }
            }
            if (income <= 0) { //If the year exist and do not has any income, show specific informacion to user.
                noDataContainer.setVisibility(View.VISIBLE);
                barChart.setVisibility(View.GONE);
                pieChart.setVisibility(View.GONE);
                noDataTV.setText(myYarsInList.get(yearIndex) +"");
            } else { //If everyThing is okay, show contain chart.
                mTitle.setText(myYarsInList.get(yearIndex)+"");
                mAmountI.setText("$" +totalIncomeYear);
                if (totalExpenseYear < 0) totalExpenseYear *= -1;
                mAmountE.setText("$"+totalExpenseYear);
                pieChart.setVisibility(View.GONE);
                noDataContainer.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);
                Collections.reverse(labelsName); //The information came reverse.
                //Create Bar Date set attribute.
                BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Monthly incomes");
                barDataSet.setColors(colorClassArray);
                barDataSet.setValueTextSize(14f);
                barDataSet.setStackLabels(labelsDescription);
                Description description = new Description();
                description.setText("Months");
                barChart.setDescription(description);
                //Creatye Bar Chart and Asisgn attributes.
                BarData barData = new BarData(barDataSet);
                barChart.setData(barData); //Assigns value to the Bar Chart elemente in the xml.
                //Modify X axi to show months year assigned in arrayList.
                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labelsName));
                xAxis.setPosition(XAxis.XAxisPosition.TOP);
                xAxis.setDrawAxisLine(false);
                xAxis.setDrawGridLines(false);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(labelsName.size());
                barChart.animateY(2000);
                barChart.invalidate(); //upload infromation when something is chnaged.
                saveData();
            }
        }
        saveData();
    }//End BarChart------------------------------------------

    //This method run the pie Chart. When user caan see grapic with the type transtaction.
    public void runPieChart(View view) {
        chartStartPie = true;
        noDataTV.setText("");
        barChart.setVisibility(View.GONE);
        mTitle.setText("");
        mAmountI.setText("");
        mAmountE.setText("");
        ArrayList<PieEntry> pieChartEntry = new ArrayList<>();
        HashMap<String, Double> map = new HashMap<>();
        //If the arrayList is empty show no data contains information
        if ( monthAccounting.size() == 0 ) {
            pieChart.setVisibility(View.GONE);
            barChart.setVisibility(View.GONE);
            noDataContainer.setVisibility(View.VISIBLE);
            noDataTV.setText("");
        }
        else {//else analys the data
            if (monthAccounting.get(monthIndex).transtactions.size() == 0) { //If month exist but dionot has transaction show infromation tpo user.
                noDataTV.setText(monthAccounting.get(monthIndex).getMonth() + "\n" + monthAccounting.get(monthIndex).getYear());
                noDataContainer.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.GONE);
            }
            else { //If everything ok analyse information and show chart.
                mTitle.setText(monthAccounting.get(monthIndex).getMonth());
                mAmountI.setText("$" +monthAccounting.get(monthIndex).getTotalIncome());
                double totalExpenseMonth = monthAccounting.get(monthIndex).getTotalExpense();
                if (totalExpenseMonth < 0) totalExpenseMonth *= -1;
                mAmountE.setText("$"+totalExpenseMonth);

                pieChart.setVisibility(View.VISIBLE);
                noDataContainer.setVisibility(View.GONE);
                //This hasMap groups transaction for description key and store the total as Value
                //the method distributeTransactionByDescription is called in class and return a HaspMap which values.
                map = monthAccounting.get(monthIndex).distributeTransactionByDescription();
                for (HashMap.Entry<String, Double> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Float value = ((Double) entry.getValue()).floatValue() * -1;
                    if (value < 0) value *= -1;
                    //In the chart is stored the description transaction and the total value.
                    pieChartEntry.add(new PieEntry(value, key));
                }
                //Create Pie Date set attribute.
                PieDataSet pieDataSet = new PieDataSet(pieChartEntry, "Month Chart");
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                pieDataSet.setValueTextColor(Color.BLACK);
                pieDataSet.setValueLineColor(Color.YELLOW);
                pieDataSet.setValueTextSize(16f);
                //Creatye Pie Chart and Asisgn attributes.
                PieData pieData = new PieData(pieDataSet);
                pieChart.setData(pieData);
                pieChart.invalidate(); //Data upload.
                pieChart.getDescription().setEnabled(false);
                pieChart.setCenterTextSize(16f);
                pieChart.setCenterText(monthAccounting.get(monthIndex).getMonth() + "\n" + monthAccounting.get(monthIndex).getYear());
                pieChart.animate();
            }
        }
        saveData();
    }//End PieChart------------------------------------------

    //This method save infromation when is colled, This information will be load in loadData()
    //This mthos use SharedPreferences for store an String Json object that contains the arrayList of our class object.
    //and a boolean value that indicate which chart the user left before destroy activity.
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(monthAccounting);
        editor.putString("task list", json);
        editor.putBoolean("charPie", chartStartPie);
        editor.apply();
    }

    //This methos load the SharedPreferences elemente create in saveDate();
    //This information is transformer in type ArrayList<MonthAccounting> and  and boolean and assign both to our program variables.
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<MonthAccounting>>() {}.getType();
        monthAccounting = gson.fromJson(json, type);
        chartStartPie= sharedPreferences.getBoolean("charPie", true);
        //if there is not any data to load, assignm empty values.
        if (monthAccounting == null) {
            monthAccounting = new ArrayList<>();
        }
        //If the DetailActivity does send any information store in field.
        if (itemUpdate != null) updateRecyclerView(itemUpdate);
        //Run recyclerview
        adapter = new ItemAdapter(this, monthAccounting);
        recyclerVier.setAdapter(adapter);
    }

    //This method add a month record to our ArrayList
    public void addMonth(View view) {
        boolean exist = false; //Bool that check if the element exist in list
        for(int i = 0; i < monthAccounting.size(); i++)
        {
            if (monthAccounting.get(i).toString().equals(controlCurrentMonth))
                exist = true;
        }
        if (current_date.length() == 0 || yearNum ==0)
            DisplayMessage("Please enter month and year");
        else if(exist)
            DisplayMessage("This date already exist");
        else
        { //Everything ok, add record to arrayList
            item = new MonthAccounting( monthNum, yearNum);
            monthAccounting.add(item);

            //Sort array
            Collections.sort(monthAccounting, new Comparator<MonthAccounting>(){
                public int compare(MonthAccounting s1, MonthAccounting s2) {
                    if (s1.getYear()> s2.getYear()) return 1;
                    else if (s1.getYear()< s2.getYear()) return -1;
                    else if (s1.getMonthInt()> s2.getMonthInt()) return 1;
                    else if (s1.getMonthInt()< s2.getMonthInt()) return -1;
                    else return 0;
                }
            });
            Collections.reverse(monthAccounting);
            runRecyclerView();
            runCharts();
            ///-------------------------------------
            saveData();
        }
        yesButton.setVisibility(View.GONE);
        noButton.setVisibility(View.GONE);;
        current_date.setText("");
    }

    //When call this method it runs the recyclerView after modificate the arrayList
    private void runRecyclerView() {
        adapter = new ItemAdapter(this, monthAccounting);
        recyclerVier.setAdapter(adapter);
    }//----------------------------

    //This method show the Date dialog where the user chose the data
    public void setDate(View view) {
        //-------------Calendar interface----------------------
        final Calendar today = Calendar.getInstance();
        MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(MainActivity.this,
                new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        //Give formate to Month in letter
                        SimpleDateFormat inFormat = new SimpleDateFormat("MM");
                        Date myDate = null;
                        try {
                            myDate = inFormat.parse(selectedMonth+1+"");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM");
                            String Monthstr = simpleDateFormat.format(myDate);

                        current_date.setText(selectedYear+ ", " + (Monthstr) );
                        controlCurrentMonth = selectedYear+ " - " + (selectedMonth+1);
                        monthNum = selectedMonth+1;
                        yearNum = selectedYear;
                        if ( monthNum > 0) //If the user not chose any date, do not show confirm icons
                        {
                            yesButton.setVisibility(View.VISIBLE);
                            noButton.setVisibility(View.VISIBLE);
                        }
                    }
                }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
        builder.setActivatedMonth(today.get(Calendar.MONTH))
                .setActivatedYear(today.get(Calendar.YEAR))
                .setMonthAndYearRange(Calendar.JANUARY, Calendar.DECEMBER, 1990, 2030)
                .setTitle("Select month and year")
                .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
                .build().show();
    }//------------------------------------------------------

    //This emthod remove a month record of the arrayList
    public void removeItem(int position)
    {
        //Toast.makeText(this, "deberia borrar el" + , Toast.LENGTH_LONG).show();
        monthAccounting.remove(position);
        runRecyclerView();
        runCharts();
        saveData();
    }//-----------------------

    //This method upload an specific month in the arrayList.
    //The user can change any filed in that specific month
    public void updateRecyclerView(MonthAccounting updateItem)
    {
        for(int i = 0; i < monthAccounting.size(); i++)
        {
            if (monthAccounting.get(i).toString().equals(updateItem.toString()))
                monthAccounting.get(i).setItem(updateItem);
        }
        runRecyclerView();
        saveData();
    }//-------------------------


    //Rigth Arrow method changes the position of monthIndex and yearindex,, integer variables defines the position of ArrayList that will be showed in pie and bar charts.
    public void LeftArrow(View view) {
        if (monthAccounting.size() != 0) {
            if (chartStartPie) { //evaluate month displayed in pie chart
                if (monthIndex+1 > monthAccounting.size()-1)
                    DisplayMessage(monthAccounting.get(monthAccounting.size()-1).getMonth()  + " is the First Month record.");
                else {
                    monthIndex += 1;
                    runPieChart(null);
                }
            } else { //evaluate year displayed in bar chart
                if (yearIndex+1 > myYarsInList.size()-1)
                    DisplayMessage(myYarsInList.get(myYarsInList.size()-1) +" is the First Year record." );
                else {
                    yearIndex += 1;
                    runBarChart(null);
                }
            }
        } else DisplayMessage("There is not record");

    }
    //Left Arrow method changes the position of yearIndex and monthindex, integer variables defines the position of ArrayList that will be showed in pie and bar charts.
    public void RigtArrow(View view) {
        if (monthAccounting.size() != 0) {
            if (chartStartPie) { //evaluate month displayed in pie chart
                if (monthIndex-1 < 0)
                    DisplayMessage(monthAccounting.get(0).getMonth() + " is the Last month record.");
                else {
                    monthIndex -= 1;
                    runPieChart(null);
                }
            } else { //evaluate year displayed in bar chart
                if (yearIndex-1 < 0)
                    DisplayMessage(myYarsInList.get(0) +" is the Last Year record.");
                else {
                    yearIndex -= 1;
                    runBarChart(null);
                }
            }
        }else DisplayMessage("There is not record");
    }
    //End Rigth and Left arrow click methods.-----------------------------------

    //Method that display a Toast Message.
    public void DisplayMessage(String message) {
        if(mToast!=null) mToast.cancel(); //Avoid collapt multiple toast on item clicked
        mToast =  Toast.makeText(this, message, Toast.LENGTH_LONG);
        mToast.show();
    }//---------------------------

    //This method cancel the proccess of add a new month/
    public void clickNo(View view){
        yesButton.setVisibility(View.GONE);
        noButton.setVisibility(View.GONE);
        current_date.setText("");
    }//---------------------------------------

    //This method run both chart if need to be uploaded.
    public void runCharts(){
        if (chartStartPie) runPieChart(null);
        else  runBarChart(null);
    }//---------------------------------------


}