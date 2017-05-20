package com.lvsijian8.flowerpot.ui.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lvsijian8.flowerpot.ClientThread;
import com.lvsijian8.flowerpot.R;

public class FragmentPot extends Fragment {
    TextView wendu;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view==null){
            view = inflater.inflate(R.layout.fragment_time, container, false);
        }
        wendu = (TextView) view.findViewById(R.id.wendu);
        Button bt = (Button) view.findViewById(R.id.refresh_wendu);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pot_id=1;
                int wenduint=0;
                Message msg = new Message();
                msg.what = 0x348;
                Bundle bundle = new Bundle();
                bundle.putInt("pot_id", pot_id);
                msg.setData(bundle);
                ClientThread.revHandler.sendMessage(msg);
                wendu.setText(""+wenduint);
            }
        });
        return view;
    }
}
