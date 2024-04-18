package es.uc3m.android.travel_rex;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class FormListener implements TextWatcher {
    private Button postButton;
    private EditText[] fields;

    public FormListener(Button postButton, EditText... fields) {
        this.postButton = postButton;
        this.fields = fields;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        // Check if all EditText fields are filled
        boolean allFieldsFilled = true;
        for (EditText field : fields) {
            if (!isFieldFilled(field)) {
                allFieldsFilled = false;
                break;
            }
        }

        // Enable or disable the post button based on all fields being filled
        postButton.setEnabled(allFieldsFilled);
    }

    // Method to check if an EditText field is filled
    private boolean isFieldFilled(EditText editText) {
        return editText.getText().toString().trim().length() > 0;
    }
}
