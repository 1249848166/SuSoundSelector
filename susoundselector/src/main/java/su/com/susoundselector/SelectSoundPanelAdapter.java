package su.com.susoundselector;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SelectSoundPanelAdapter extends RecyclerView.Adapter<SelectSoundPanelAdapter.Holder> implements View.OnClickListener{

    Context context;
    List<SoundData> soundDataList;
    List<SoundData> selectedList=new ArrayList<>();
    int selectedNum=0;
    int maxSelectNum=5;

    OnSoundSelectedListener onSoundSelectedListener;

    public void setOnSoundSelectedListener(OnSoundSelectedListener onSoundSelectedListener) {
        this.onSoundSelectedListener = onSoundSelectedListener;
    }

    public List<SoundData> getSelectedList() {
        return selectedList;
    }

    public void setMaxSelectNum(int maxSelectNum) {
        this.maxSelectNum = maxSelectNum;
    }

    public SelectSoundPanelAdapter(Context context, List<SoundData> soundDataList) {
        this.context = context;
        this.soundDataList = soundDataList;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FilterViewLayout itemView= (FilterViewLayout)LayoutInflater.from(context).inflate(R.layout.sound_item,parent,false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        try {
            TextView title = holder.itemView.findViewById(R.id.title);
            TextView path = holder.itemView.findViewById(R.id.path);
            title.setText(soundDataList.get(position).getTitle());
            path.setText(soundDataList.get(position).getPath());
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(this);
            holder.itemView.setGroup(selectedList);
            holder.itemView.setMaxSize(maxSelectNum);
            if(!holder.itemView.getTag().equals(soundDataList.get(position))){
                holder.itemView.setSelected(false);
            }
            if(selectedList.contains(soundDataList.get(position))){
                holder.itemView.setSelected(true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return soundDataList.size();
    }

    @Override
    public void onClick(View v) {
        try {
            int position = (int) v.getTag();
            if (!selectedList.contains(soundDataList.get(position))) {
                if (selectedNum < maxSelectNum) {
                    selectedList.add(soundDataList.get(position));
                    selectedNum++;
                    if(onSoundSelectedListener!=null)
                        onSoundSelectedListener.onSelect(soundDataList.get(position),true);
                } else {
                    Toast.makeText(context, "最多选择" + maxSelectNum + "个", Toast.LENGTH_SHORT).show();
                }
            } else {
                selectedList.remove(soundDataList.get(position));
                selectedNum--;
                onSoundSelectedListener.onSelect(soundDataList.get(position),false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class Holder extends RecyclerView.ViewHolder{

        FilterViewLayout itemView;

        public Holder(FilterViewLayout itemView) {
            super(itemView);
            this.itemView=itemView;
        }
    }
}
