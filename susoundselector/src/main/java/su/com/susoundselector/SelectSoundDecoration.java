package su.com.susoundselector;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SelectSoundDecoration extends RecyclerView.ItemDecoration {

    int space;

    public SelectSoundDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(space,space,space,0);
    }
}
