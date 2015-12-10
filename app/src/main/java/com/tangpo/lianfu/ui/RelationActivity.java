package com.tangpo.lianfu.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.tangpo.lianfu.R;

/**
 * Created by 果冻 on 2015/12/9.
 */
public class RelationActivity extends Activity implements View.OnClickListener {
    private TextView relate;
    private TextView registe;
    private TextView con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_relation);

        relate = (TextView) findViewById(R.id.relate);
        relate.setOnClickListener(this);
        registe = (TextView) findViewById(R.id.registe);
        registe.setOnClickListener(this);
        con = (TextView) findViewById(R.id.con);
        con.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.relate:
                intent = new Intent(RelationActivity.this, RelateActivity.class);
                startActivity(intent);
                break;
            case R.id.registe:
                intent = new Intent(RelationActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.con:
                intent=new Intent(RelationActivity.this,HomePageActivity.class);
                startActivity(intent);
                break;
        }
    }
}
