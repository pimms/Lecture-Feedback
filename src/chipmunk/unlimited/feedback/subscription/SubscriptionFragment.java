package chipmunk.unlimited.feedback.subscription;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.sql.SQLException;

import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.R.id;
import chipmunk.unlimited.feedback.R.layout;
import chipmunk.unlimited.feedback.database.SubscriptionDatabase;

/**
 * Created by Tobias on 31.08.13, forked by Joakim on 11.11.2013
 */
public class SubscriptionFragment extends DialogFragment {	
    private SubscriptionProtocolListener mListener;
    private boolean update = false;
    private SubscriptionAdapter adapter;


    public SubscriptionFragment(SubscriptionProtocolListener listener) {
        assert(listener != null);
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        //Dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_subscriptions, null);

        /* Create an anonymous callback for the button */
        view.findViewById(R.id.subs_add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubscriptionFragment.this.displayAddSubscriptionFragment();
            }
        });

        String title = getActivity().getResources().getString(R.string.frag_sub_title);
        String cancel = getActivity().getResources().getString(R.string.frag_sub_back);

        builder.setView(view)
                .setTitle(title)
                .setNeutralButton(cancel, null);
        return builder.create();
    }

    @Override
    public void onStart(){
        super.onStart();
        AlertDialog dialog = (AlertDialog)getDialog();
        ListView list = (ListView)dialog.findViewById(R.id.subs_list);

        /* Load the subscriptions */
        SubscriptionDatabase datasource = new SubscriptionDatabase(getActivity());
        datasource.open();

        Cursor subCursor = datasource.getSubscriptionCursor();
        if (subCursor.getCount() != 0) {
            /* Display the existing subscriptions */
            adapter = new SubscriptionAdapter(getActivity(), subCursor, 0);
            list.setAdapter(adapter);
        } else {
            /* No subscriptions exists, go to Add-view */
            displayAddSubscriptionFragment();
            subCursor.close();
        }
        datasource.close();
    }

    @Override
    public void onDismiss(DialogInterface dialog){
        mListener.onSubscriptionsChanged();

        super.onDismiss(dialog);
    }


    private void displayAddSubscriptionFragment() {
        mListener.onAddSubscriptionRequest(this);
    }
}