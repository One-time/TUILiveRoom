package com.trtc.uikit.livekit.view.voiceroom.view.panel.seatmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.RecyclerView;

import com.trtc.tuikit.common.imageloader.ImageLoader;
import com.trtc.uikit.livekit.R;
import com.trtc.uikit.livekit.manager.LiveController;
import com.trtc.uikit.livekit.state.operation.SeatState;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SeatListPanelAdapter extends RecyclerView.Adapter<SeatListPanelAdapter.LinkMicViewHolder> {

    private final LiveController                           mLiveController;
    private final Context                                  mContext;
    private final CopyOnWriteArrayList<SeatState.SeatInfo> mData = new CopyOnWriteArrayList<>();

    public SeatListPanelAdapter(Context context, LiveController liveController) {
        mContext = context;
        mLiveController = liveController;
        initData();
    }

    @NonNull
    @Override
    public LinkMicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.livekit_voiceroom_item_seat_list_panel, parent, false);
        return new LinkMicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkMicViewHolder holder, int position) {
        if (TextUtils.isEmpty(mData.get(position).name.get())) {
            holder.textName.setText(mData.get(position).userId.get());
        } else {
            holder.textName.setText(mData.get(position).name.get());
        }

        if (TextUtils.isEmpty(mData.get(position).avatarUrl.get())) {
            holder.imageHead.setImageResource(R.drawable.livekit_ic_avatar);
        } else {
            ImageLoader.load(mContext, holder.imageHead, mData.get(position).avatarUrl.get(),
                    R.drawable.livekit_ic_avatar);
        }
        holder.textSeatIndex.setText(String.valueOf(mData.get(position).index + 1));
        holder.textHangUp.setTag(mData.get(position));
        holder.textHangUp.setOnClickListener((view) -> {
            final SeatState.SeatInfo seatInfo = (SeatState.SeatInfo) view.getTag();
            mLiveController.getSeatController().kickUserOffSeatByAdmin(seatInfo);
        });
    }

    private void initData() {
        mData.clear();
        String selfUserId = mLiveController.getUserState().selfInfo.userId;
        List<SeatState.SeatInfo> seatList = mLiveController.getSeatState().seatList.get();
        for (SeatState.SeatInfo seatInfo : seatList) {
            String userId = seatInfo.userId.get();
            if (TextUtils.isEmpty(userId)) {
                continue;
            }
            if (userId.equals(selfUserId)) {
                continue;
            }
            mData.add(seatInfo);
        }
    }

    public List<SeatState.SeatInfo> getData() {
        return mData;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData() {
        initData();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class LinkMicViewHolder extends RecyclerView.ViewHolder {
        public ImageFilterView imageHead;
        public TextView        textName;
        public TextView        textLevel;
        public TextView        textHangUp;
        public TextView        textSeatIndex;

        public LinkMicViewHolder(View itemView) {
            super(itemView);
            imageHead = itemView.findViewById(R.id.iv_head);
            textName = itemView.findViewById(R.id.tv_name);
            textLevel = itemView.findViewById(R.id.tv_level);
            textHangUp = itemView.findViewById(R.id.tv_hang_up);
            textSeatIndex = itemView.findViewById(R.id.tv_seat_index);
        }
    }
}
