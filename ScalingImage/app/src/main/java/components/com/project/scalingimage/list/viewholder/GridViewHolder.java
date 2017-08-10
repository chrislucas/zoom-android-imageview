package components.com.project.scalingimage.list.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import components.com.project.scalingimage.R;
import components.com.project.scalingimage.views.ZoomableImageView;

/**
 * Created by r028367 on 10/08/2017.
 */

public class GridViewHolder extends RecyclerView.ViewHolder {

    private ZoomableImageView zoomableImageView;

    public GridViewHolder(View itemView) {
        super(itemView);
        this.zoomableImageView = (ZoomableImageView) itemView.findViewById(R.id.image_to_scaling_on_list);
    }

    public ZoomableImageView getZoomableImageView() {
        return zoomableImageView;
    }

    public void setZoomableImageView(ZoomableImageView zoomableImageView) {
        this.zoomableImageView = zoomableImageView;
    }
}
