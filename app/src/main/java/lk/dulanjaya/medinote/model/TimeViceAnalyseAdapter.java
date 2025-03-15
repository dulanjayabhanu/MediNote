package lk.dulanjaya.medinote.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import lk.dulanjaya.medinote.R;

public class TimeViceAnalyseAdapter extends RecyclerView.Adapter<TimeViceAnalyseAdapter.TimeViceAnalyseViewHolder>{
    private List<TimeViceAnalyseDTO> timeViceAnalyseDTOList;

    public TimeViceAnalyseAdapter(List<TimeViceAnalyseDTO> timeViceAnalyseDTOList){
        this.timeViceAnalyseDTOList = timeViceAnalyseDTOList;
    }

    public static class TimeViceAnalyseViewHolder extends RecyclerView.ViewHolder{
        private TextView dateTextView;
        private TextView recordTimeTextView;
        private TextView sysValueTextView;
        private TextView pulValueTextView;
        private TextView diaValueTextView;
        public TimeViceAnalyseViewHolder(@NonNull View itemView) {
            super(itemView);

            this.dateTextView = itemView.findViewById(R.id.timeViceAnalyseTextViewDate);
            this.recordTimeTextView = itemView.findViewById(R.id.timeViceAnalyseTextViewRecordTime);
            this.sysValueTextView = itemView.findViewById(R.id.timeViceAnalyseTextViewSys);
            this.pulValueTextView = itemView.findViewById(R.id.timeViceAnalyseTextViewPul);
            this.diaValueTextView = itemView.findViewById(R.id.timeViceAnalyseTextViewDia);
        }
    }

    @NonNull
    @Override
    public TimeViceAnalyseAdapter.TimeViceAnalyseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.time_vice_analyse_item_layout, parent, false);

        TimeViceAnalyseViewHolder timeViceAnalyseViewHolder = new TimeViceAnalyseViewHolder(view);

        return timeViceAnalyseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViceAnalyseAdapter.TimeViceAnalyseViewHolder holder, int position) {
        TimeViceAnalyseDTO timeViceAnalyseDTO = timeViceAnalyseDTOList.get(position);

        holder.dateTextView.setText(timeViceAnalyseDTO.getDate());

        String recordTime = "Record Time : " + timeViceAnalyseDTO.getRecordTime();
        holder.recordTimeTextView.setText(recordTime);

        holder.sysValueTextView.setText(timeViceAnalyseDTO.getSysValue());
        holder.pulValueTextView.setText(timeViceAnalyseDTO.getPulValue());
        holder.diaValueTextView.setText(timeViceAnalyseDTO.getDiaValue());
    }

    @Override
    public int getItemCount() {
        return this.timeViceAnalyseDTOList.size();
    }
}
