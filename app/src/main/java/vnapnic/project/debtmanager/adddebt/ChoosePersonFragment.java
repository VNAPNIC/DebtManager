package vnapnic.project.debtmanager.adddebt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import vnapnic.project.debtmanager.ContactsContractHelper;
import vnapnic.project.debtmanager.R;
import vnapnic.project.debtmanager.database.DatabaseHandler;
import vnapnic.project.debtmanager.model.Friend;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Property of Matt Allen
 * mattallen092@gmail.com
 * http://mattallensoftware.co.uk/
 * <p/>
 * This software is distributed under the Apache v2.0 license and use
 * of the Repay name may not be used without explicit permission from the project owner.
 */

public class ChoosePersonFragment extends DebtFragment implements OnItemClickListener, OnClickListener {

    public static final int PICK_CONTACT_REQUEST = 1;
    private static final String TAG = ChoosePersonFragment.class.getName();
    private ChoosePersonAdapter mAdapter;
    private ListView mListView;
    private RelativeLayout mEmptyState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Tell the activity that we have ActionBar items
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inf) {
        super.onCreateOptionsMenu(menu, inf);
        if (menu.size() <= 1) {
            inf.inflate(R.menu.chooseperson, menu);
        }
    }

    private void showAddFriendDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.enter_friends_name);
        dialog.setItems(new CharSequence[]{getActivity().getResources().getString(R.string.Add_From_Contacts), getActivity().getResources().getString(R.string.Add_A_Name)}, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                    startActivityForResult(intent, PICK_CONTACT_REQUEST);
                } else if (which == 1) {
                    addFriendByName();
                }
            }
        });
        dialog.show();
    }

    public void addFriendByName() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.enter_friends_name);
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.add_friend_by_name, null);
        dialog.setView(v);
        dialog.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = ((EditText) v.findViewById(R.id.name_entry)).getText().toString();
                try {
                    if (!TextUtils.isEmpty(name)) {
                        Friend newFriend = new Friend(DatabaseHandler.generateRepayID(), null, name, new BigDecimal("0"));
                        ((DebtActivity) getActivity()).getDBHandler().addFriend(newFriend);
                        new GetFriendsFromDB().execute();
                    } else {
                        ((EditText) v.findViewById(R.id.name_entry)).setError(getActivity().getResources().getString(R.string.please_enter_name));
                    }
                } catch (SQLException e) {
                    Toast.makeText(getActivity(), R.string.friend_could_not_be_added, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST) {
            try {
                String contactUri = data.getData().toString();
                String displayName = ContactsContractHelper.getNameForContact(getActivity(), contactUri);

                Friend pickerResult = new Friend(DatabaseHandler.generateRepayID(), contactUri, displayName, new BigDecimal("0"));
                ((DebtActivity) getActivity()).getDBHandler().addFriend(pickerResult);

                new GetFriendsFromDB().execute();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.problem_getting_from_contacts, Toast.LENGTH_LONG).show();
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.problem_adding_to_repay, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_addfriend:
                showAddFriendDialog();
                return true;

            default:
                return true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_friendchooser, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = (ListView) getView().findViewById(R.id.activity_friendchooser_list);
        mListView.setOnItemClickListener(this);

        mEmptyState = (RelativeLayout) getView().findViewById(R.id.activity_friendchooser_emptystate);

        (getView().findViewById(R.id.activity_friendchooser_helpbtn)).setOnClickListener(this);

        new GetFriendsFromDB().execute();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Friend selectedFriend = (Friend) arg1.getTag();
        Log.i(TAG, selectedFriend.getName() + " selected (" + selectedFriend.getRepayID() + ")");
        if (((DebtActivity) getActivity()).getDebtBuilder().getSelectedFriends().contains(selectedFriend)) {
            ((DebtActivity) getActivity()).getDebtBuilder().removeSelectedFriend(selectedFriend);
            arg1.setBackgroundColor(ChoosePersonAdapter.DESELECTED_COLOUR);
        } else {
            ((DebtActivity) getActivity()).getDebtBuilder().getSelectedFriends().add(selectedFriend);
            arg1.setBackgroundColor(ChoosePersonAdapter.SELECTED_COLOUR);
        }
    }

    @Override
    public void saveFields() {
        // No need to do anything here. This fragment uses the DebtBuilder object as storage.
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activity_friendchooser_helpbtn) {
            showAddFriendDialog();
        }
    }

    private class GetFriendsFromDB extends AsyncTask<DatabaseHandler, Integer, ArrayList<Friend>> {

        @Override
        protected void onPreExecute() {
            mListView.setVisibility(ListView.INVISIBLE);
            mEmptyState.setVisibility(RelativeLayout.INVISIBLE);
        }

        @Override
        protected ArrayList<Friend> doInBackground(DatabaseHandler... params) {
            try {
                ArrayList<Friend> friends = ((DebtActivity) getActivity()).getDBHandler().getAllFriends();
                return friends;
            } catch (Throwable e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Friend> result) {
            if (result != null && result.size() > 0) {
                mListView.setVisibility(ListView.VISIBLE);
                mAdapter = new ChoosePersonAdapter(getActivity(), R.layout.fragment_adddebt_friendslist_item, result, ((DebtActivity) getActivity()).getDebtBuilder().getSelectedFriends());
                mListView.setAdapter(mAdapter);
                mAdapter.setSelectedFriends(((DebtActivity) getActivity()).getDebtBuilder().getSelectedFriends());
            } else {
                mEmptyState.setVisibility(RelativeLayout.VISIBLE);
            }
        }
    }
}
