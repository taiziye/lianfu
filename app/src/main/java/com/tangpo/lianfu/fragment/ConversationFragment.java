package com.tangpo.lianfu.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.google.gson.Gson;
import com.tangpo.lianfu.R;
import com.tangpo.lianfu.adapter.ConversationAdapter;
import com.tangpo.lianfu.entity.ChatAccount;
import com.tangpo.lianfu.entity.HXUser;
import com.tangpo.lianfu.http.NetConnection;
import com.tangpo.lianfu.parms.GetSpecifyHX;
import com.tangpo.lianfu.ui.AddFriendActivity;
import com.tangpo.lianfu.ui.ChatActivity;
import com.tangpo.lianfu.ui.ConversationActivity;
import com.tangpo.lianfu.utils.InviteMessageDao;
import com.tangpo.lianfu.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by 果冻 on 2016/1/8.
 */
public class ConversationFragment extends Fragment {
    private LinearLayout invite;
    private ImageView msg;
    private ListView listView = null;
    private EditText query = null;
    private Button clear = null;
    private InputMethodManager inputMethodManager = null;
    private ConversationAdapter adapter = null;
    private List<EMConversation> list = new ArrayList<>();
    private View view;
    private String myid = "";
    private String photo = "";
    private List<String> id = new ArrayList<String>();
    private List<HXUser> names = new ArrayList<HXUser>();
    private boolean hidden;
    private Gson gson = new Gson();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_conversation, container, false);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //myid = getArguments().getString("hxid");
        //photo = getArguments().getString("photo");
        myid = ChatAccount.getInstance().getEasemod_id();
        photo = ((ConversationActivity)getActivity()).getPhoto();
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
        /*Log.e("tag", "hidden " + hidden);
        if (!hidden) {
            refresh();
        }*/
        list.clear();
        init(view);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        //helper.close();
    }

    private void init(View view){
        listView = (ListView) view.findViewById(R.id.list);
        query = (EditText) view.findViewById(R.id.query);
        query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)){
                    String str = query.getText().toString().trim();
//                    if (str.length() == 0||str=="") {
//                        Tools.showToast(getActivity(),"没有找到对应的匹配项");
//                        //storeList.clear();
//                        //getStores();
//                    } else {
//                        adapter.getFilter().filter(str);
//                        //storeList.clear();
//                        //findStore(str);
//                    }
                    adapter.getFilter().filter(str);
                    InputMethodManager imm= (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });
        clear = (Button) view.findViewById(R.id.clear);
        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
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
        if (list.size() > 0) {
            getName();
        }
        //adapter = new ConversationAdapter(getActivity(), list);
        //listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = adapter.getItem(position);
                String username = adapter.getUserName(position);
                String photo = adapter.getPhoto(position);
                String hxid = conversation.getUserName();
                if (hxid.toLowerCase().equals(myid.toLowerCase())) {
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

        invite = (LinearLayout) view.findViewById(R.id.invite);
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFriendActivity.class);
                (new InviteMessageDao(getActivity())).saveUnreadMessageCount(0);
                //((ConversationActivity)getActivity()).saveUnreadMessageCount(0);
                msg.setVisibility(View.INVISIBLE);
                startActivity(intent);
            }
        });
        msg = (ImageView) view.findViewById(R.id.msg);
        if(((ConversationActivity)getActivity()).getUnread() <= 0) msg.setVisibility(View.INVISIBLE);
        else msg.setVisibility(View.VISIBLE);
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
        //getName();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void setMsgView(int flag) {
        if(flag == 0) {
            msg.setVisibility(View.INVISIBLE);
        } else {
            msg.setVisibility(View.VISIBLE);
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
                    id.add(conversation.getUserName());
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

    private void getName(){
        String easemod_id = "";
        for (int i = 0; i<id.size(); i++) {
            if (i > 0) easemod_id += ",";
            easemod_id += id.get(i);
        }
        //dialog = ProgressDialog.show(getActivity(), getString(R.string.connecting), getString(R.string.please_wait));
        String[] kvs = new String[]{easemod_id};
        String param = GetSpecifyHX.packagingParam(getActivity(), kvs);

        new NetConnection(new NetConnection.SuccessCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                //dialog.dismiss();
                try {
                    JSONArray array = result.getJSONArray("param");
                    JSONObject object;
                    for (int i=0; i<array.length(); i++) {
                        object = array.getJSONObject(i);
                        HXUser user = gson.fromJson(object.toString(), HXUser.class);
                        names.add(user);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Message msg = new Message();
                msg.what = 1;
                msg.obj = names;
                handler.sendMessage(msg);
            }
        }, new NetConnection.FailCallback() {
            @Override
            public void onFail(JSONObject result) {
                //dialog.dismiss();
                Tools.showToast(getActivity(), "获取聊天用户信息失败");
            }
        }, param);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    names = (List<HXUser>) msg.obj;
                    adapter = new ConversationAdapter(getActivity(), list, names);
                    listView.setAdapter(adapter);
                    break;
            }
        }
    };

}
