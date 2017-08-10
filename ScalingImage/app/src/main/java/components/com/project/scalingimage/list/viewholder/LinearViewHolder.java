package components.com.project.scalingimage.list.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import components.com.project.scalingimage.R;
import components.com.project.scalingimage.views.ZoomableImageView;

/**
 * Created by r028367 on 09/08/2017.
 */

public class LinearViewHolder extends RecyclerView.ViewHolder {

    private ZoomableImageView zoomableImageView;

    public LinearViewHolder(View itemView) {
        super(itemView);
        zoomableImageView = (ZoomableImageView) itemView.findViewById(R.id.image_to_scaling_on_list);
    }

    public ZoomableImageView getZoomableImageView() {
        return zoomableImageView;
    }

    public void setZoomableImageView(ZoomableImageView zoomableImageView) {
        this.zoomableImageView = zoomableImageView;
    }
}
