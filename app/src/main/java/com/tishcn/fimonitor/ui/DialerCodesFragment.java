package com.tishcn.fimonitor.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tishcn.fimonitor.R;
import com.tishcn.fimonitor.util.Constants;
import com.tishcn.fimonitor.util.DialerCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leona on 6/29/2016.
 */
public class DialerCodesFragment extends Fragment {

    public DialerCodesFragment(){}

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dialer_codes, container, false);
        RecyclerView dialerCodesRecycler = (RecyclerView) rootView.findViewById(R.id.dialerCodesRecycler);
        List<DialerCode> dialerCodeList = createDialerCodeList();
        DialerCodesAdapter dialerCodesAdapter = new DialerCodesAdapter(dialerCodeList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        dialerCodesRecycler.setLayoutManager(layoutManager);
        dialerCodesRecycler.setItemAnimator(new DefaultItemAnimator());
        dialerCodesRecycler.setAdapter(dialerCodesAdapter);
        dialerCodesRecycler.addOnItemTouchListener(new RecyclerTouchListener(getContext(), dialerCodesRecycler, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                TextView code = (TextView) view.findViewById(R.id.dialer_code_code);
                String strCode = code.getText().toString();
                ClipboardManager clipboard = (ClipboardManager) getActivity()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(strCode,strCode);
                clipboard.setPrimaryClip(clip);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                startActivity(intent);
                Toast.makeText(getContext(), "The dialer code has been copied to your clipboard. " +
                        "Simply paste into the dialer and your request will be " +
                        "processed.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return rootView;
    }

    private List<DialerCode> createDialerCodeList() {

        List<DialerCode> dialerCodeList = new ArrayList<>();

        DialerCode dialerCodeSprint = new DialerCode(
                1,
                Constants.DIALER_CODE_CODE_SPRINT,
                Constants.NETWORK_OPERATOR_NAME_SPRINT,
                "Force change to ".concat(Constants.NETWORK_OPERATOR_NAME_SPRINT).concat("."));

        DialerCode dialerCodeTmobile = new DialerCode(
                2,
                Constants.DIALER_CODE_CODE_TMOBILE,
                Constants.NETWORK_OPERATOR_NAME_TMOBILE,
                "Force change to ".concat(Constants.NETWORK_OPERATOR_NAME_TMOBILE).concat("."));

        DialerCode dialerCodeUsCellular = new DialerCode(
                3,
                Constants.DIALER_CODE_CODE_US_CELLULAR,
                Constants.NETWORK_OPERATOR_NAME_US_CELLULAR,
                "Force change to ".concat(Constants.NETWORK_OPERATOR_NAME_US_CELLULAR).concat("."));

        DialerCode dialerCodeNextCarrier = new DialerCode(
                4,
                Constants.DIALER_CODE_CODE_NEXT_CARRIER,
                Constants.DIALER_CODE_TITLE_NEXT_CARRIER,
                "Force change to ".concat(Constants.DIALER_CODE_TITLE_NEXT_CARRIER).concat("."));

        DialerCode dialerCodeAutoSwitch = new DialerCode(
                5,
                Constants.DIALER_CODE_CODE_AUTO_SWITCH,
                Constants.DIALER_CODE_TITLE_AUTO_SWITCH,
                "Enable/Re-enable ".concat(Constants.DIALER_CODE_TITLE_AUTO_SWITCH).concat("."));

        DialerCode dialerCodeRepair = new DialerCode(
                6,
                Constants.DIALER_CODE_CODE_REPAIR,
                Constants.DIALER_CODE_TITLE_REPAIR,
                "Attempt to ".concat(Constants.DIALER_CODE_TITLE_REPAIR)
                        .concat(" cellular connection."));

        DialerCode dialerCodeInfo = new DialerCode(
                7,
                Constants.DIALER_CODE_CODE_INFO,
                Constants.DIALER_CODE_TITLE_INFO,
                "Get current cellular connection ".concat(Constants.DIALER_CODE_TITLE_INFO).concat("."));

        DialerCode dialerCodeTestingActivity = new DialerCode(
                0,
                Constants.DIALER_CODE_CODE_TESTING_ACTIVITY,
                Constants.DIALER_CODE_TITLE_TESTING_ACTIVITY,
                "Opens the ".concat(Constants.DIALER_CODE_TITLE_TESTING_ACTIVITY).concat("."));

        dialerCodeList.add(dialerCodeSprint);
        dialerCodeList.add(dialerCodeTmobile);
        dialerCodeList.add(dialerCodeUsCellular);
        dialerCodeList.add(dialerCodeNextCarrier);
        dialerCodeList.add(dialerCodeAutoSwitch);
        dialerCodeList.add(dialerCodeRepair);
        dialerCodeList.add(dialerCodeInfo);
        dialerCodeList.add(dialerCodeTestingActivity);

        return dialerCodeList;
    }

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private DialerCodesFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final DialerCodesFragment.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

}
