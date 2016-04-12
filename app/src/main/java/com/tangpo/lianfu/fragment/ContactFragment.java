package com.tangpo.lianfu.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.ContactAdapter;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.ui.ChatActivity;
import com.tangpo.lianfu.ui.ConversationActivity;

import java.util.ArrayList;

/**
 * Created by 果冻 on 2016/1/8.
 */
public class ContactFragment extends Fragment {
    private ListView list_waiter = null;
    private ListView list_friend = null;
    private LinearLayout waiter;
    private LinearLayout friend;
    private ImageView wtag;
    private ImageView ftag;
    private EditText query = null;
    private Button clear = null;
    private InputMethodManager inputMethodManager = null;
    private ArrayList<ChatAccount> waiters = new ArrayList<>(); //客服
    private ArrayList<ChatAccount> friends = new ArrayList<>(); //好友

    private ContactAdapter contactAdapter = null;
    private ContactAdapter friendsAdapter = null;
    private String hx_id = "";
    private String photo = "";

    private int w=0;
    private int f=0;

    public void refresh() {
        //waiters.clear();
        friends.clear();
        //waiters.addAll(((ConversationActivity) getActivity()).getAccounts());
        friends.addAll(((ConversationActivity) getActivity()).getFriendList());
        setListViewHeightBasedOnChildren(list_friend);
        //getName();
        if (contactAdapter != null) {
            contactAdapter.notifyDataSetChanged();
        }
        if (friendsAdapter != null) {
            friendsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        init(view);
        return view;
    }

    private void init(View view){
        list_waiter = (ListView) view.findViewById(R.id.list_waiter);
        list_friend = (ListView) view.findViewById(R.id.list_friend);
        query = (EditText) view.findViewById(R.id.query);
        clear = (Button) view.findViewById(R.id.clear);
        waiter = (LinearLayout) view.findViewById(R.id.waiter);
        waiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list_waiter.getVisibility() == View.VISIBLE) {
                    w=0;
                    setView(w, list_waiter, wtag);
                } else {
                    w=1;
                    setView(w, list_waiter, wtag);
                }
            }
        });
        friend = (LinearLayout) view.findViewById(R.id.friend);
        friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list_friend.getVisibility() == View.VISIBLE) {
                    f=0;
                    setView(f, list_friend, ftag);
                } else {
                    f=1;
                    setView(f, list_friend, ftag);
                }
            }
        });
        list_waiter.setVisibility(View.GONE);
        list_friend.setVisibility(View.GONE);
        wtag = (ImageView) view.findViewById(R.id.wtag);
        ftag = (ImageView) view.findViewById(R.id.ftag);

        //waiters.addAll((ArrayList<ChatAccount>) getArguments().getSerializable("acstr"));
        //hx_id = getArguments().getString("hxid");
        //photo = getArguments().getString("photo");
        waiters.addAll(((ConversationActivity) getActivity()).getAccounts());
        hx_id = ((ConversationActivity)getActivity()).getHxid();
        photo = ((ConversationActivity)getActivity()).getPhoto();
        contactAdapter = new ContactAdapter(getActivity(), waiters);
        list_waiter.setAdapter(contactAdapter);
        list_waiter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                String username = waiters.get(position).getName();
                //String userid = waiters.get(position).getUser_id();
                String hxid = waiters.get(position).getEasemod_id();
                intent.putExtra("account", waiters.get(position));
                //intent.putExtra("userid", userid);
                intent.putExtra("username", username);
                intent.putExtra("hxid", hxid);
                intent.putExtra("myid", hx_id);
                intent.putExtra("photo", photo);
                //ChatUser user = new ChatUser(hxid, username);
                //Tools.saveConversation(user);
                startActivity(intent);
            }
        });

        friends.addAll(((ConversationActivity) getActivity()).getFriendList());
        friendsAdapter = new ContactAdapter(getActivity(), friends);
        list_friend.setAdapter(friendsAdapter);
        list_friend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                String username = friends.get(position).getName();
                String hxid = friends.get(position).getEasemod_id();
                intent.putExtra("account", friends.get(position));
                intent.putExtra("username", username);
                intent.putExtra("hxid", hxid);
                intent.putExtra("myid", hx_id);
                intent.putExtra("photo", photo);
                startActivity(intent);
            }
        });

        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (contactAdapter != null) {
                    contactAdapter.getFilter().filter(s.toString());
                }
                if (friendsAdapter != null) {
                    friendsAdapter.getFilter().filter(s.toString());
                }
                if (s.length() > 0) {
                    setView(1, list_waiter, wtag);
                    setView(1, list_friend, ftag);
                    clear.setVisibility(View.VISIBLE);
                } else {
                    clear.setVisibility(View.GONE);
                    setView(w, list_waiter, wtag);
                    setView(f, list_friend, ftag);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                setView(w, list_waiter, wtag);
                setView(f, list_friend, ftag);
                hideSoftKeyBoard();
            }
        });

        list_waiter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyBoard();
                return false;
            }
        });

        list_friend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyBoard();
                return false;
            }
        });

        setListViewHeightBasedOnChildren(list_waiter);
        setListViewHeightBasedOnChildren(list_friend);
    }

    private void hideSoftKeyBoard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private void setView(int flag, View view, ImageView tag) {
        if(flag == 0) {
            view.setVisibility(View.GONE);
            tag.setImageResource(R.drawable.fold);
        }else{
            view.setVisibility(View.VISIBLE);
            tag.setImageResource(R.drawable.down);
        }
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
