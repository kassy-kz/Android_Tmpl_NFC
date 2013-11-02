
package orz.kassy.android_tmpl_nfc;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

    private static final String TAG = null;
    
    private Object[] activities = {
            "NFC Write",  NfcWriteActivity.class,
            "NFC Read",   NfcReadActivity.class,
            "NFC Beam",   NfcBeamActivity.class,
    };
    private ArrayAdapter<String> mAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        String[] list = new String[activities.length/2];
        for (int i = 0; i < list.length; i++) {
            list[i] = (String)activities[i * 2];
        }
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        setListAdapter(mAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(MainActivity.this, (Class<?>)activities[position * 2 + 1]);
        startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
