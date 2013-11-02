package orz.kassy.android_tmpl_nfc;

import java.io.IOException;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.EditText;
import android.widget.Toast;

public class NfcWriteActivity extends Activity {

    private IntentFilter[] mWriteTagFilters;
    EditText mNote;
    private Vibrator mVib;

    
    /**
     * onCreate
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 画面表示
        setContentView(R.layout.activity_write);
        
        // バイブ
        mVib = (Vibrator)getSystemService(VIBRATOR_SERVICE);

    }

    /**
     * onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        
        // NFC書き込み可能状態にする
        enableTagWriteMode();
    }

    /**
     * NFC書き込み可能状態にする
     */
    private void enableTagWriteMode() {
        
        // このアプリ以外がNFCに反応しないようにする
        mWriteTagFilters = new IntentFilter[] {
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        };
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent nfcIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, nfcIntent, mWriteTagFilters, null);
    }

    
    /**
     * onNewIntent NFCが近づけられると呼ばれる
     */
    @Override
    protected void onNewIntent(Intent intent) {

        // タグを検出したとき
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            toast("Tag get.");

            // Intentからタグを取得して
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            // タグに書き込むNdefMessageを用意する
            NdefRecord appRecord = NdefRecord.createUri("Tekitou URI");
            NdefMessage ndefMessage = new NdefMessage(new NdefRecord[] {
                    appRecord
            });
            
            // 書き込む
            writeTag(ndefMessage, detectedTag);
        }
    }

    /**
     * タグ書き込みを行う
     * @param message
     * @param tag
     * @return
     */
    boolean writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    toast("Tag is read-only.");
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    toast("Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size
                            + " bytes.");
                    return false;
                }

                ndef.writeNdefMessage(message);
                mVib.vibrate(300);
                toast("Wrote message to pre-formatted tag.");
                return true;
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        mVib.vibrate(300);
                        toast("Formatted tag and wrote message");
                        return true;
                    } catch (IOException e) {
                        toast("Failed to format tag.");
                        return false;
                    }
                } else {
                    toast("Tag doesn't support NDEF.");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            toast("Failed to write tag");
        }
        return false;
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
