package com.diagram.diagramandroid.sbeauty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.diagram.diagramandroid.R;

/**
 * Set result for Activity
 */
public class SetResultActivity extends AppCompatActivity {

    private EditText mEtText;
    private Button mBtnSetData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_result);
        initView();
        initListener();
    }

    private void initView() {
        mEtText = findViewById(R.id.et_text);
        mBtnSetData = findViewById(R.id.btn_set_data);
    }

    private void initListener() {
        mBtnSetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("text", mEtText.getText().toString());
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }
}
