package su.com.susoundselector;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SelectSoundPanelActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SelectSoundPanelAdapter adapter;
    List<SoundData> soundDataList;
    SelectSoundDecoration decoration;

    TextView num;
    Button selectedNum;

    int maxSize=5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_select_sound_panel);

            selectedNum=findViewById(R.id.selectedNum);
            selectedNum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (adapter.getSelectedList().size() > 0) {
                            Intent intent = new Intent();
                            List<String> list = new ArrayList<>();
                            for (SoundData s : adapter.getSelectedList()) {
                                list.add(s.getPath());
                            }
                            intent.putStringArrayListExtra("paths", (ArrayList<String>) list);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(SelectSoundPanelActivity.this, "请选择", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            num=findViewById(R.id.num);

            recyclerView = findViewById(R.id.recycler);
            soundDataList = new ArrayList<>();
            adapter = new SelectSoundPanelAdapter(this, soundDataList);
            adapter.setMaxSelectNum(maxSize);
            adapter.setOnSoundSelectedListener(new OnSoundSelectedListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSelect(SoundData soundData, boolean select) {
                    try {
                        num.setText(adapter.getSelectedList().size() + "个");
                        selectedNum.setText(adapter.getSelectedList().size() + "/" + maxSize);
                        if (adapter.getSelectedList().size() > 0) {
                            selectedNum.setBackgroundColor(Color.GREEN);
                            selectedNum.setEnabled(true);
                        } else {
                            selectedNum.setBackgroundColor(Color.parseColor("#aaaaaa"));
                            selectedNum.setEnabled(false);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            decoration = new SelectSoundDecoration(1);
            recyclerView.addItemDecoration(decoration);

            searchSound();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void searchSound(){
        SuSoundLoader.getInstance().loadFileSounds(this, new LoadSoundCallback() {
            @Override
            public void onLoad(List<SoundData> datas) {
                try {
                    soundDataList.clear();
                    soundDataList.addAll(datas);
                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
