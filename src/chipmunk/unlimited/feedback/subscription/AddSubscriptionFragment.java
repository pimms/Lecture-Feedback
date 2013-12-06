package chipmunk.unlimited.feedback.subscription;

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
import android.view.WindowManager;
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

import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.TimeEditHTTP;
import chipmunk.unlimited.feedback.TimeEditParser;
import chipmunk.unlimited.feedback.R.id;
import chipmunk.unlimited.feedback.R.layout;
import chipmunk.unlimited.feedback.database.SubscriptionDatabase;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * Created by Tobias on 02.09.13, forked by Joakim on 11.11.2013.
 *
 * The code in this file is largely unchanged since the fork, and
 * is bar compatibility changes marginally altered for improved functinality.
 */
public class AddSubscriptionFragment extends DialogFragment {

    private SubscriptionDatabase datasource;
    private SubscriptionProtocolListener mListener;


    public AddSubscriptionFragment(SubscriptionProtocolListener listener) {
        assert(listener != null);
        mListener = listener;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_subscription, null);

        String title = getActivity().getResources().getString(R.string.frag_subadd_title);
        String search = getActivity().getResources().getString(R.string.frag_subadd_search);

        builder.setView(view).setTitle(title).setNeutralButton(search, null);
        return builder.create();
    }

    private void showKeyboard(final EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editText.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager imm = (InputMethodManager)
                                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        editText.requestFocus();
    }

    private void hideKeyboard(final EditText editText) {
        editText.setOnFocusChangeListener(null);
        
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    private void dismissAddSubscriptionFragment() {
        mListener.onAddSubscriptionFragmentDismiss(this);
    }

    @Override
    public void onStart(){
        super.onStart();
        AlertDialog dialog = (AlertDialog)getDialog();

        final EditText et = (EditText)dialog.findViewById(R.id.add_edittext);
        final ProgressBar pb = (ProgressBar)dialog.findViewById(R.id.add_progressBar);
        final ListView lv = (ListView)dialog.findViewById(R.id.add_result_list);
        final TextView aerror = (TextView)dialog.findViewById(R.id.aerror_text);
        final Button abutton = (Button)dialog.findViewById(R.id.add_nores_button);
        final Button neutButton = (Button) dialog.getButton(Dialog.BUTTON_NEUTRAL);

        showKeyboard(et);

        abutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSubscriptionFragment.this.dismissAddSubscriptionFragment();
            }
        });

        /**********************************************/
        /** Stop reading at this point. I'm serious. **/
        /**********************************************/
        assert(neutButton != null);
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
                        et.setVisibility(View.GONE);
                        pb.setVisibility(View.VISIBLE);
                        neutButton.setEnabled(false);

                        //Get string from search field
                        final String term = et.getText().toString();

                        //Send to network class
                        TimeEditHTTP.search(term, new AsyncHttpResponseHandler(){
                            @Override
                            public void onSuccess(String response){
                                Log.d("DIALOG", "onSuccess starting");

                                hideKeyboard(et);

                                //Get parsed results from parser
                                final String[][] results = TimeEditParser.search(response, term);
                                final String[] names = new String[results.length];

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
                                        datasource = new SubscriptionDatabase(getActivity());
                                        datasource.open();

                                        String[] split = results[position][1].split(",");
                                        String higCode = split[0];

                                        String name = split[1];
                                        for (int i=2; i<split.length; i++) {
                                        	name += "," + split[i];
                                        }

                                        datasource.addSubscription(results[position][0], higCode, name);
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
                                String msg = getActivity().getResources().getString(
                                        R.string.frag_subadd_error);
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                                Log.d("NET", e.toString());
                            }
                        });

                        if(closeDialog)
                            dismiss();
                    }else{
                        //If search string contains illegal characters, this will show up
                        String msg = getActivity().getResources().getString(
                                R.string.frag_subadd_invalid);
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //If nothing is written in search field, this will show up
                    String msg = getActivity().getResources().getString(
                            R.string.frag_subadd_notext);
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
