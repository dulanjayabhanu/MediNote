package lk.dulanjaya.medinote.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.dulanjaya.medinote.R;

public class CheckupRoundAdapter extends RecyclerView.Adapter<CheckupRoundAdapter.CheckoutTimeViewHolder> {
    List<CheckupRoundDTO> checkupRoundDTOList;

    public CheckupRoundAdapter(List<CheckupRoundDTO> checkupRoundDTOList){
        this.checkupRoundDTOList = checkupRoundDTOList;
    }

    public static class CheckoutTimeViewHolder extends RecyclerView.ViewHolder{
        private TextView date;
        private TextView count;
        private TextView recordTime;

        public CheckoutTimeViewHolder(@NonNull View itemView) {
            super(itemView);

            this.date = itemView.findViewById(R.id.checkupRoundTextViewDate);
            this.count = itemView.findViewById(R.id.checkupRoundTextViewCount);
            this.recordTime = itemView.findViewById(R.id.checkupRoundTextViewRecordTime);
        }
    }

    @NonNull
    @Override
    public CheckupRoundAdapter.CheckoutTimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.checkup_round_item_layout, parent, false);

        CheckoutTimeViewHolder checkoutTimeViewHolder = new CheckoutTimeViewHolder(view);

        return checkoutTimeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CheckupRoundAdapter.CheckoutTimeViewHolder holder, int position) {
        CheckupRoundDTO checkupRoundDTO = checkupRoundDTOList.get(position);

        holder.date.setText(checkupRoundDTO.getDate());

        String count = "Total rounds : " + (checkupRoundDTO.getCount() < 10 ? "0" + checkupRoundDTO.getCount() : checkupRoundDTO.getCount());
        holder.count.setText(count);

        holder.recordTime.setText(checkupRoundDTO.getRecordTimes());
    }

    @Override
    public int getItemCount() {
        return checkupRoundDTOList.size();
    }
}
