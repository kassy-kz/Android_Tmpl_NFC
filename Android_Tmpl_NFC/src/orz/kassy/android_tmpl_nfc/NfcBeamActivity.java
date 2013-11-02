package orz.kassy.android_tmpl_nfc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateBeamUrisCallback;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class NfcBeamActivity extends Activity implements OnCheckedChangeListener, OnNdefPushCompleteCallback, CreateNdefMessageCallback, CreateBeamUrisCallback{
    private static final String TAG = null;
    private NfcAdapter mNfcAdapter;
    private RadioGroup mRadioGroup;
    private File mPngFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        mRadioGroup.setOnCheckedChangeListener(this);

        savePng();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.radio0 || checkedId == R.id.radio1) {
            mNfcAdapter.setBeamPushUrisCallback(null, this);
            mNfcAdapter.setNdefPushMessageCallback(this, this);
        } else {
            mNfcAdapter.setNdefPushMessageCallback(null, this);
            mNfcAdapter.setBeamPushUrisCallback(this,this);
        }
    }
    
    private void savePng() {
        String filename = "ic_launcher.png";
        File dir = getFilesDir();
        mPngFile = new File(dir, filename);
        Log.d(TAG, "mJpeg is " + (mPngFile.exists() ? " exists." : "not exists."));
        if (!mPngFile.exists()) {
            Drawable d = getResources().getDrawable(R.drawable.ic_launcher);
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            FileOutputStream fos = null;
            try {
                // 送信するファイルは他のプロセスでも読み取れるようにしておくこと
                fos = openFileOutput(filename, Context.MODE_WORLD_READABLE);
                fos.write(data);
                Log.d(TAG, "file saved to '" + mPngFile.getPath() + "'");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        toast("Complete");        
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        Log.d(TAG, "createNdefMessage");
        NdefMessage msg = null;
        int checkedId = mRadioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radio0) {
            msg = new NdefMessage(NdefRecord.createExternal(
                    "orz.kassy.tmpl", "tekitou-type", "TEKITOU TEXT".getBytes())
            );
        } else if (checkedId == R.id.radio1) {
            msg = new NdefMessage(
                NdefRecord.createUri("http://www.atmarkit.co.jp/"));
        }
        return msg;
    }
    
    @Override
    public Uri[] createBeamUris(NfcEvent event) {
        Log.d(TAG, "createBeamUris");
        Uri[] uri = null;
        if (mRadioGroup.getCheckedRadioButtonId() == R.id.radio2) {
            uri = new Uri[] { Uri.fromFile(mPngFile) };
        }
        return uri;
    }
    

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    
}
