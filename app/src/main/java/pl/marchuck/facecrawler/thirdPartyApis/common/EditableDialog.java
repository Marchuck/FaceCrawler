package pl.marchuck.facecrawler.thirdPartyApis.common;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pl.marchuck.facecrawler.R;

/**
 * @author Lukasz Marczak
 * @since 19.05.16.
 */
public class EditableDialog {
    public interface EditCallback{
        void onEdit(String s);
    }
    public EditableDialog(String source,Activity activity, final EditCallback callback){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.editable_dialog);
        final EditText editText = (EditText) dialog.findViewById(R.id.edittext);
        editText.setText(source);
       final  TextView tv = (TextView) dialog.findViewById(R.id.textView);
        Button btn = (Button) dialog.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editable  = editText.getText().toString();
                if (editable.isEmpty()) {
                    tv.setText("Enter valid tag!");
                }else{
                    callback.onEdit(editable);
                    dialog.dismiss();
                }
            }
        });
        dialog.setCancelable(true);
        dialog.show();
    }
}
