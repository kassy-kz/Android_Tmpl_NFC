package orz.kassy.android_tmpl_nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

public class NfcReadActivity extends Activity {

    /**
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    /**
     * onResume
     */
    @Override
    public void onResume() {
        super.onResume();
        
        // NDEFを検知してアプリが立ち上がったときは、直ぐ読み込みを走らせる
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            Toast.makeText(this, "NDEF DISCOVERED", Toast.LENGTH_LONG).show();
            readNdefRecord(getIntent());
        }
        
        // NFC優先読み込みモードにする
        enableTagRead();
    }
    
    
    /**
     * 新規にタグが見つかった時
     */
    @Override
    protected void onNewIntent(Intent intent) {
        Toast.makeText(this, "Tag get. : " + intent.getAction(), Toast.LENGTH_LONG).show();
        readNdefRecord(intent);
    }
    
    /**
     * タグ優先読み込み可能にする（他のアプリにとらせない）
     */
    private void enableTagRead() {

        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent nfcIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        IntentFilter[] writeTagFilters = new IntentFilter[] {
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        };
        
        nfcAdapter.enableForegroundDispatch(this, nfcIntent, writeTagFilters, null);
    }

    /**
     * NFCタグを読み込む処理、onResume か onNewIntent から呼び出す
     * @param intent
     */
    private void readNdefRecord(Intent intent) {

        // NFCの中身を取得する（インテントに複数NdefMessageがあることがある）
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages == null) {
            return;
        }

        for (int i = 0; i < rawMessages.length; i++) {
            // NdefMessage型に格納
            NdefMessage ndefMsg = (NdefMessage) rawMessages[i];

            // NdefMessageからNdefRecordを取り出す
            for (NdefRecord record : ndefMsg.getRecords()) {

                // NdefRecordのTNFで分類
                switch(record.getTnf()) {
                    case NdefRecord.TNF_EXTERNAL_TYPE:
                        toast("TNF_EXTERAL_TYPE");
                        break;
                    case NdefRecord.TNF_ABSOLUTE_URI:
                        toast("TNF_ABSOLUTE_URI");
                        break;
                    case NdefRecord.TNF_WELL_KNOWN:
                        toast("TNF_WELL_KNOWN");
                        break;
                    default:
                        toast("TNF OTHER");
                        break;
                }

                // データ本体のType部を取り出す。
                byte[] typeBytes = record.getType();
                String typeStr = new String(typeBytes);
                toast("Ndef:" + i + " type = " + typeStr);

                // データ本体のPayload部を取り出す。
                byte[] payloadBytes = record.getPayload();
                String payloadStr = new String(payloadBytes);
                toast("Ndef:" + i + " payload : " + payloadStr);
            }
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

}
