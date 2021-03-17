package com.example.projectoFinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recycleviewlist.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;

/*
    This Detail activity include all detail about specific month. It does not affect other months features.
    This class should send the changed information to main activity for upload.
 */
public class DetailsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private TextView month, year;
    private MonthAccounting item;

    //As main activity this class has an recyclerview when show each transaction
    private ArrayList<Transaction> mItemTransaction;
    private RecyclerView recyclerVierTrans;
    private RecyclerView.Adapter adapterTrans;

    //Add Item fields that are used in add Item panel
    private TextView mTypeField;
    private Spinner mDescSpinner;
    private EditText mAmount;
    private TextView mDateTrans;
    String [] descTransE;
    ArrayAdapter<String> descAdapterE;
    String [] descTransI;
    ArrayAdapter<String> descAdapterI;
    public ImageView mDelateIcon;
    public ImageView mEditIcon;
    public ImageView mSaveIcon;
    public ImageView mAddIcon;
    public Button mIncomeBtn;
    public Button mExpenseBtn;
    public TextView mTdate; //Edit panel date
    public int positionEditItem; //Edit item index in transaction
    public boolean editMode = false; //Edit item mode in transaction
    public LinearLayout mAddItemLayout;
    private Toast mToast; //Create to avoid collision

    /*
        The method receives one element that will be analyse and modificated.
        This element is assigned in the Adapter in a intent to this class..
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //Set initial feature of recyclerView
        recyclerVierTrans = findViewById(R.id.recyclerViewTrans);
        recyclerVierTrans.setHasFixedSize(true);
        recyclerVierTrans.setLayoutManager(new LinearLayoutManager(this));
        //---------------

        //Get keyboard window down when start.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        item = (MonthAccounting)getIntent().getSerializableExtra("myObject");

        //Assign value of transaction store in elemente studied.
        mItemTransaction = item.transtactions;
        month = findViewById(R.id.monthID);
        year = findViewById(R.id.yearID);

        //amount and date secction
        mAmount = findViewById(R.id.ETnumID);
        mDateTrans = findViewById(R.id.dateTransID);
        //Spinner seccion Type transaction
        mTypeField = findViewById(R.id.typeFieldID);
        mDescSpinner = findViewById(R.id.descSpinnerID);
        descTransE = new String[] {"Rent", "Food", "Car", "Phone", "Gas", "School", "Bill", "cloths", "Entertainment", "Pet", "Health", "House", "Eat out", };
        descAdapterE = new ArrayAdapter<String>(this, R.layout.spinner_item, descTransE);
        descTransI = new String[] {"Salary", "Deposits", "Savings"};
        descAdapterI = new ArrayAdapter<String>(this, R.layout.spinner_item, descTransI);
        //--------------------------------

        //Asign flied of icons created in xml.
        mDelateIcon = findViewById(R.id.deleteID);
        mEditIcon = findViewById(R.id.editID);
        mSaveIcon = findViewById(R.id.saveID);
        mAddIcon = findViewById(R.id.addID);
        mIncomeBtn = findViewById(R.id.incomeBtnID);
        mExpenseBtn = findViewById(R.id.expenseBtnID);
        mAddItemLayout = findViewById(R.id.linearLayout);
        mTdate = findViewById(R.id.dateTransID);
        //Clear field if not information.
        if(item != null) {
            month.setText(item.getMonth()+"");
            year.setText(item.getYear()+"");
        }
        //Starts running recyclerView.
        runRecyclerView();
        //Show Toast when click icon
        findViewById(R.id.addID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Toast.makeText(DetailsActivity.this, "Choose the type of transaction" , Toast.LENGTH_LONG).show(); }
        });//end toast onclicklistenner icon

        //This section create onClickListenner on the TextView inside the edit panel that will be store and transaction
        findViewById(R.id.dateTransID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });//end date trans onclicklistenner

    }
    //This method show the Date Picker Dialog that implements DetailsActivity class
    public void showDatePickerDialog(){

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (DatePickerDialog.OnDateSetListener) this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        //After get intance of Month, year, day, in Calendar class set as interval showed only the month clicked item.
        //This way the dialog only will show the days of thta specific month because is setted Min and Max Date.
        Calendar c = Calendar.getInstance();
        c.set(item.getYear(), item.getMonthInt()-1, 1);
        c.add(Calendar.MONTH, 0);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.add(Calendar.DAY_OF_MONTH,maxDay-1);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.show();
    }//end onDateDialog ---------------------------------

    //The method set the date in the TextView that holds the value stored.
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfWeek) {

        //Give formate to day name of week with SimpleDateFormat
        SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date myDate = inFormat.parse(dayOfWeek+"-"+month+"-"+year);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE");
            String dayName = simpleDateFormat.format(myDate).toUpperCase();
            String date = "";
            if (dayOfWeek < 10) date =  "0"+dayOfWeek  + " "+ dayName ;
            else date = dayOfWeek  + " "+ dayName ;
            mDateTrans.setText(date);
        } catch (ParseException e) {
            DisplayMessage("Error with the Picked date, try again");
        }

    }//end onDateSet--------------------------------

    //Method that send bacl to main activity and send the infromation changed.
    public void onClickBack(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("myObjectUpDate", item);
        startActivity(intent);
    }//-------------

    //This emthod add a new transaction to the transtaction arrayList.
    public void AddTransactionToList(View view) {
        // Hide the keyboard when the button is pushed.
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        //Get information in panels add arrayList.
        String holdType = mTypeField.getText().toString();
        String holdDesc= mDescSpinner.getSelectedItem().toString();
        String holdDate = mDateTrans.getText().toString();
        double holdAmount;
        if (mAmount.getText().toString().isEmpty())
            DisplayMessage("You should enter a number");
        else if (mDateTrans.getText().toString().isEmpty())
            DisplayMessage("You should enter a Date");
        else {
            if (editMode)
                removeTrans(positionEditItem);
            holdAmount = Double.parseDouble(mAmount.getText().toString());
            item.addTransaction(holdAmount, holdType, holdDesc, holdDate);
        }
        //Sort array
        Collections.sort(mItemTransaction, new Comparator<Transaction>(){
            public int compare(Transaction t1, Transaction t2) {
                return t1.getDate().compareToIgnoreCase(t2.getDate());
            }
        });
        runRecyclerView();
        //MainActivity.updateRecyclerView(item);
        closeAddPanel(null);
        //Clean filed after add element.
        mAmount.setText("");
        mDateTrans.setText("");
    }

    //When call this method it runs the recyclerView after modificate the arrayList
    private void runRecyclerView() {
        adapterTrans = new TransactionAdapter(this, mItemTransaction);
        recyclerVierTrans.setAdapter(adapterTrans);
    }//------------------------------

    //Remove an elemente from the arrayList
    public void removeTrans(int position) {
        item.transtactions.remove(position);
        runRecyclerView();
    }//--------------

    //This emthod change information in a specific trantsaction.
    public void EditTrans(int position, boolean modeEdit, TransactionAdapter.ViewHolder holder) {
        editMode = modeEdit;
        positionEditItem = position;
        if (editMode) {

            mAddItemLayout.setBackgroundColor(Color.parseColor("#B8D5FC"));
            Transaction editThisTrans = item.transtactions.get(position);
            if (editThisTrans.getType().equals("Expense")) {
                ExpenseTrans(null);
            } else {
                IncomeTrans(null);
            }
            DisplayMessage("Edit current transaction. push save Bottom for saving changes.");
            int index = 0;

            if (holder.mType.getText().toString().equals("Expense")) {
                for (int i = 0; i < descTransE.length; i++)
                    if (holder.mDescription.getText().toString().equals(descTransE[i]))
                        index = i;
            }else {
                for (int i = 0; i < descTransI.length; i++)
                    if (holder.mDescription.getText().toString().equals(descTransI[i]))
                        index = i;
            }
            mDescSpinner.setSelection(index);
            String date = holder.mDate.getText().toString();
            mDateTrans.setText(date.substring(5, 7) +"\n " + date.substring(0, 3)) ;
            mAmount.setText(holder.mTransaction.getText().toString().substring(2));
            //item.transtactions.remove(position);
        }
        else {
            closeAddPanel(null);
            mAddItemLayout.setBackgroundColor(Color.WHITE);
            DisplayMessage("Edit Mode Canceled");
        }

    }//end ediTransaction ------------------------------------

    //This emthod is create for make test in the app.
    public void SaveTrans(int position) {
        Toast.makeText(this, "save item num " + position, Toast.LENGTH_LONG).show();
    }//---------------------------------------------

    //Thisplay a toast message method that get a string as paraamter
    public void DisplayMessage(String message) {
        if(mToast!=null) mToast.cancel(); //Avoid collapt multiple toast on item clicked
        mToast =  Toast.makeText(this, message, Toast.LENGTH_LONG);
        mToast.show();
    }//----------------------------------------

    //User open pannel for add a new transaction
    public void OpenPanelAddList(View view) {
        mAddIcon.setVisibility(View.GONE);
        mExpenseBtn.setVisibility(View.GONE);
        mIncomeBtn.setVisibility(View.GONE);
        mAddItemLayout.setVisibility(View.VISIBLE);
    }//-------------------------------

    //User press the close icon, clos add panel. Just hide elementes.
    public void closeAddPanel(View view) {
        mAddIcon.setVisibility(View.VISIBLE);
        mExpenseBtn.setVisibility(View.VISIBLE);
        mIncomeBtn.setVisibility(View.VISIBLE);
        mAddItemLayout.setVisibility(View.GONE);
        runRecyclerView();
    }//--------------------------------

    //Customer chose an expense transaction that will show a expense description spinner.
    public void ExpenseTrans(View view) {
        mTypeField.setText("Expense");
        mDescSpinner.setAdapter(descAdapterE);
        OpenPanelAddList(view);
    }//----------------------------------
    //Customer chose an income transaction that will show a income description spinner.
    public void IncomeTrans(View view) {
        mTypeField.setText("Income");
        mDescSpinner.setAdapter(descAdapterI);
        OpenPanelAddList(view);
    }//---------------------------------


    //This method override the balck android defaul buttom.
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("myObjectUpDate", item);
        startActivity(intent);
    }//--------------------------------------------------

}
