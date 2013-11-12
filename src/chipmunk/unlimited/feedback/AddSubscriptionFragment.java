package chipmunk.unlimited.feedback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by Tobias on 02.09.13, forked by Joakim on 11.11.2013.
 */
public class AddSubscriptionFragment extends DialogFragment {

    private SubscriptionDBHelper datasource;
    private SubscriptionsChangedListener mListener;

    
    public void setSubscriptionsChangedListener(SubscriptionsChangedListener listener) {
    	mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_subscription, null);

        Spinner spinner = (Spinner) view.findViewById(R.id.add_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        builder.setView(view)
                .setTitle("Title, idc")
                .setNeutralButton("whatevs", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }
    
    @Override
    public void onStart(){
        super.onStart();
        AlertDialog dialog = (AlertDialog)getDialog();

        final EditText et = (EditText)dialog.findViewById(R.id.add_edittext);
        final Spinner spinner = (Spinner)dialog.findViewById(R.id.add_spinner);
        final ProgressBar pb = (ProgressBar)dialog.findViewById(R.id.add_progressBar);
        final ListView lv = (ListView)dialog.findViewById(R.id.add_result_list);
        final TextView aerror = (TextView)dialog.findViewById(R.id.aerror_text);
        final Button abutton = (Button)dialog.findViewById(R.id.add_nores_button);

        final Button neutButton = (Button) dialog.getButton(Dialog.BUTTON_NEUTRAL);

        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    neutButton.performClick();
                    return true;
                }
                return false;
            }
        });

        assert neutButton != null;

        //Set onClick Listener for dialog button
        neutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(et.getText().length() > 0){
                    if(!et.getText().toString().matches("[^a-zA-Z0-9\\s-]")){
                        //Used to close dialog
                        Boolean closeDialog = false;

                        //Manager to close keyboard after searching
                        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imgr.hideSoftInputFromWindow(et.getWindowToken(), 0);

                        //Hide old shit, show progressbar, disable search button
                        spinner.setVisibility(View.GONE);
                        et.setVisibility(View.GONE);
                        pb.setVisibility(View.VISIBLE);
                        neutButton.setEnabled(false);

                        //Get string from search field
                        final String term = et.getText().toString();

                        //Send to network class
                        TimeEditHTTP.search(term, spinner.getSelectedItem().toString(), getResources().getStringArray(R.array.search_array), new AsyncHttpResponseHandler(){
                            @Override
                            public void onSuccess(String response){
                                Log.d("DIALOG", "onSuccess starting");

                                //Get parsed results from parser
                                final String[][] results = TimeEditParser.search(response, term);
                                String[] names = new String[results.length];

                                //Create separate (1D) array of names of classes/courses
                                for(int i = 0; i < results.length; i++){
                                    names[i] = results[i][1];
                                }

                                pb.setVisibility(View.GONE);

                                //Arrayadapter for populating the listview
                                final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_expandable_list_item_1, names);
                                lv.setAdapter(adapter);
                                lv.setVisibility(View.VISIBLE);

                                //onClick Listener for listview
                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        datasource = new SubscriptionDBHelper(getActivity());
                                        datasource.open();
                                        
                                        datasource.addSubscription(results[position][0], results[position][1]);
                                        datasource.close();
                                        mListener.onSubscriptionsChanged();
                                        dismiss();
                                    }
                                });

                                if(lv.getCount() == 0){
                                    aerror.setVisibility(View.VISIBLE);
                                    abutton.setVisibility(View.VISIBLE);
                                }

                            }
                            @Override
                            public void onFailure(Throwable e, String response){
                                Toast.makeText(getActivity(), "TOP NET", Toast.LENGTH_LONG).show();
                                Log.d("NET", e.toString());
                            }
                        });

                        if(closeDialog)
                            dismiss();
                    }else{
                        //If search string contains illegal characters, this will show up
                        Toast.makeText(getActivity(), "ILLEGAL", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //If nothing is written in search field, this will show up
                    Toast.makeText(getActivity(), "DUNNO", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}