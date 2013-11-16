package chipmunk.unlimited.feedback.subscription;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.sql.SQLException;

import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.R.id;
import chipmunk.unlimited.feedback.R.layout;

/**
 * Created by Tobias on 31.08.13, forked by Joakim on 11.11.2013
 */
public class SubscriptionFragment extends DialogFragment {	
    private SubscriptionsChangedListener mListener;
    private boolean update = false;
    private SubscriptionAdapter adapter;

    public SubscriptionFragment(){}
    

    public void setSubscriptionsChangedListener(SubscriptionsChangedListener listener) {
    	mListener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog){
        if(mListener != null) {
            mListener.onSubscriptionsChanged();
        }
        
        super.onDismiss(dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        //Dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_subscriptions, null);

        builder.setView(view)
                .setTitle("Title")
                .setNeutralButton("Neutral", new DialogInterface.OnClickListener() {
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
        ListView list = (ListView)dialog.findViewById(R.id.subs_list);
        
        SubscriptionDatabase datasource = new SubscriptionDatabase(getActivity());
        datasource.open();

        adapter = new SubscriptionAdapter(getActivity(), datasource.getSubscriptionCursor(), 0);
        list.setAdapter(adapter);
        datasource.close();
    }
}