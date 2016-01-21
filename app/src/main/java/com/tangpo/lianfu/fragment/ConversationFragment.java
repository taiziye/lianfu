package com.tangpo.lianfu.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.ConversationAdapter;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.ui.ChatActivity;
import com.tangpo.lianfu.utils.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by 果冻 on 2016/1/8.
 */
public class ConversationFragment extends Fragment {
    private ListView listView = null;
    private EditText query = null;
    private Button clear = null;
    private InputMethodManager inputMethodManager = null;
    private ConversationAdapter adapter = null;
    private List<EMConversation> list = new ArrayList<>();
    //private DataHelper helper = null;
    private View view;
    private String myid = "";
    private String photo = "";
    private boolean hidden;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_conversation, container, false);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        myid = getArguments().getString("hxid");
        photo = getArguments().getString("photo");
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
        //EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage});
        //helper = new DataHelper(getActivity());
        //Log.e("tag", "resume");
        list.clear();
        init(view);
    }

    @Override
    public void onStop() {
        //EMChatManager.getInstance().unregisterEventListener(this);
        super.onStop();
    }

    /*@Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage:
                break;
            case EventOfflineMessage:
                break;
        }
    }*/

    @Override
    public void onPause() {
        super.onPause();
        //helper.close();
    }

    private void init(View view){
        listView = (ListView) view.findViewById(R.id.list);
        query = (EditText) view.findViewById(R.id.query);
        clear = (Button) view.findViewById(R.id.clear);
        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
                if (s.length() > 0) {
                    clear.setVisibility(View.VISIBLE);
                } else {
                    clear.setVisibility(View.GONE);
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
                hideSoftKeyBoard();
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyBoard();
                return false;
            }
        });

        list.addAll(loadConversationsWithRecentChat());
        //conversations = loadCoversationList();
        /*Collections.sort(list, new Comparator<ChatAccount>() {
            @Override
            public int compare(ChatAccount lhs, ChatAccount rhs) {
                return Tools.CompareDate(lhs.getTime(), rhs.getTime());
            }
        });*/
        adapter = new ConversationAdapter(getActivity(), list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("tag", list.size() + " size " + adapter.getCount());
                EMConversation conversation = adapter.getItem(position);
                String username = adapter.getUserName(position);
                String hxid = conversation.getUserName();
                Log.e("tag", "hxid " + hxid + "  " + ChatAccount.getInstance().getEasemod_id());
                if (hxid.toLowerCase().equals(ChatAccount.getInstance().getEasemod_id().toLowerCase())) {
                    Tools.showToast(getActivity(), "无法跟自己聊天");
                } else {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("hxid", hxid);
                    intent.putExtra("myid", myid);
                    intent.putExtra("photo", photo);
                    startActivity(intent);
                }
            }
        });
    }

    private void hideSoftKeyBoard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        list.clear();
        list.addAll(loadConversationsWithRecentChat());
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 加载聊天记录
     * @return
     */
    protected List<EMConversation> loadConversationsWithRecentChat() {
        //EMChatManager.getInstance().loadAllConversations();
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();

        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }
        }
        try{
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }

        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {
                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

}
