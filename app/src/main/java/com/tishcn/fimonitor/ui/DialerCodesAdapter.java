package com.tishcn.fimonitor.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tishcn.fimonitor.R;
import com.tishcn.fimonitor.util.Constants;
import com.tishcn.fimonitor.util.DialerCode;

import java.util.List;

/**
 * Created by leona on 7/10/2016.
 */
public class DialerCodesAdapter extends RecyclerView.Adapter<DialerCodesAdapter.DialerCodesViewHolder> {

    private List<DialerCode> mDialerCodeList;

    public DialerCodesAdapter(List<DialerCode> dialerCodeList){
        this.mDialerCodeList = dialerCodeList;
    }

    @Override
    public DialerCodesAdapter.DialerCodesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dialer_code_row, parent, false);
        return new DialerCodesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DialerCodesAdapter.DialerCodesViewHolder holder, int position) {
        DialerCode dialerCode = mDialerCodeList.get(position);
        holder.dialerCodeId.setText(String.valueOf(dialerCode.getId()));
        holder.dialerCodeCode.setText(dialerCode.getCode());
        holder.dialerCodeTitle.setText(dialerCode.getTitle());
        holder.dialerCodeSubTitle.setText(dialerCode.getSubTitle());

        int imageResourceId = 0;

        switch (dialerCode.getTitle()){
            case Constants.NETWORK_OPERATOR_NAME_SPRINT:
                imageResourceId = R.drawable.ic_sprint;
                break;
            case Constants.NETWORK_OPERATOR_NAME_TMOBILE:
                imageResourceId = R.drawable.ic_tmobile;
                break;
            case Constants.NETWORK_OPERATOR_NAME_US_CELLULAR:
                imageResourceId = R.drawable.ic_us_cellular;
                break;
            case Constants.DIALER_CODE_TITLE_NEXT_CARRIER:
                imageResourceId = R.drawable.ic_next;
                break;
            case Constants.DIALER_CODE_TITLE_AUTO_SWITCH:
                imageResourceId = R.drawable.ic_swap_horizontal_black_36dp;
                break;
            case Constants.DIALER_CODE_TITLE_REPAIR:
                imageResourceId = R.drawable.ic_wrench_black_36dp;
                break;
            case Constants.DIALER_CODE_TITLE_INFO:
                imageResourceId = R.drawable.ic_info_outline;
                break;
            case Constants.DIALER_CODE_TITLE_TESTING_ACTIVITY:
                imageResourceId = R.drawable.ic_perm_device_information;
                break;
        }

        holder.dialerCodeIcon.setImageResource(imageResourceId);

    }

    @Override
    public int getItemCount() {
        return mDialerCodeList.size();
    }

    public static class DialerCodesViewHolder extends RecyclerView.ViewHolder {

        public ImageView dialerCodeIcon;
        public TextView dialerCodeId;
        public TextView dialerCodeCode;
        public TextView dialerCodeTitle;
        public TextView dialerCodeSubTitle;

        public DialerCodesViewHolder(View itemView) {
            super(itemView);
            dialerCodeIcon = (ImageView) itemView.findViewById(R.id.dialer_code_icon);
            dialerCodeId = (TextView) itemView.findViewById(R.id.dialer_code_id);
            dialerCodeCode = (TextView) itemView.findViewById(R.id.dialer_code_code);
            dialerCodeTitle = (TextView) itemView.findViewById(R.id.dialer_code_title);
            dialerCodeSubTitle = (TextView) itemView.findViewById(R.id.dialer_code_sub_title);
        }
    }
}
