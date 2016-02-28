package vnapnic.project.debtmanager.adddebt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import vnapnic.project.debtmanager.R;
import vnapnic.project.debtmanager.settings.SettingsActivity;

import java.math.BigDecimal;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class AddDebtActivity extends DebtActivity {

    private static final String TAG = AddDebtActivity.class.getName();

    private int mFrame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adddebt);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setSubtitle(R.string.choose_people);
        mFrame = R.id.activity_adddebt_framelayout;

        if (getFragmentManager().findFragmentById(mFrame) == null) {
            // Show the first fragment
            getFragmentManager().beginTransaction().replace(mFrame, new ChoosePersonFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.adddebt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent();
                intent.setClass(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                break;
        }
        return false;
    }

    public void onNextButtonClick(View v) {
        switch (v.getId()) {
            case R.id.activity_friendchooser_donebtn:
                ((DebtFragment) getFragmentManager().findFragmentById(mFrame)).saveFields();
                if (getDebtBuilder().getSelectedFriends() != null && getDebtBuilder().getSelectedFriends().size() > 0) {
                    Log.i(TAG, Integer.toString(getDebtBuilder().getSelectedFriends().size()) + " people selected");
                    getFragmentManager().beginTransaction().replace(mFrame, new EnterAmountFragment()).addToBackStack(null).commit();
                } else {
                    Toast.makeText(this, "Please choose 1 or more people first", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.fragment_enterdebtamount_donebtn:
                ((DebtFragment) getFragmentManager().findFragmentById(mFrame)).saveFields();
                if (getDebtBuilder().getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    getFragmentManager().beginTransaction().replace(mFrame, new DebtSummaryFragment()).addToBackStack(null).commit();
                } else {
                    Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.donebtn:
                ((DebtFragment) getFragmentManager().findFragmentById(mFrame)).saveFields();
                save();
                break;

            default:
                break;
        }
    }
}
