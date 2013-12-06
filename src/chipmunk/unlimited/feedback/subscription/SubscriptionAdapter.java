package chipmunk.unlimited.feedback.subscription;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.R.drawable;
import chipmunk.unlimited.feedback.R.id;
import chipmunk.unlimited.feedback.R.layout;
import chipmunk.unlimited.feedback.database.SubscriptionDatabase;

/**
 * Created by Tobias on 02.09.13, forked by Joakim on 11.11.2013
 *
 * The code in this file is largely unchanged since the fork, and
 * is bar compatibility changes marginally altered for improved functinality.
 */
public class SubscriptionAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private Context act;
    private boolean updated = false;

    public SubscriptionAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        act = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return inflater.inflate(R.layout.list_item_subscription, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final Context ctx = context;
        final String name = cursor.getString(cursor.getColumnIndex(SubscriptionDatabase.COLUMN_NAME));
        final int id = cursor.getInt(cursor.getColumnIndex(SubscriptionDatabase.COLUMN_ID));

        TextView tvName = (TextView) view.findViewById(R.id.sd_textName);
        ImageView img = (ImageView) view.findViewById(R.id.cancel_img);

        tvName.setText(cursor.getString(cursor.getColumnIndex(SubscriptionDatabase.COLUMN_NAME)));
        img.setImageResource(R.drawable.navigation_cancel);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = ctx.getResources().getString(R.string.frag_sub_toast_removed);
                Toast.makeText(ctx, msg + "'" + name + "'", Toast.LENGTH_LONG).show();
                deleteSub(id);
            }
        });
    }

    private void deleteSub(int id){
        updated = true;
        SubscriptionDatabase ds = new SubscriptionDatabase(act);
        ds.open();

        ds.deleteSubscription(id);
        this.changeCursor(ds.getSubscriptionCursor());
        ds.close();
        this.notifyDataSetChanged();
    }

    public boolean update(){
        return updated;
    }
}