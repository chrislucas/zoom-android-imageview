package components.com.project.scalingimage.entities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by r028367 on 09/08/2017.
 */

public class Post implements Parcelable {

    private byte [] bufferImage;

    public Post() {}

    public Post(byte[] bufferImage) {
        this.bufferImage = bufferImage;
    }

    public Post(Parcel in) {
        in.readByteArray(this.bufferImage);
    }

    public byte[] getBufferImage() {
        return bufferImage;
    }

    public void setBufferImage(byte[] bufferImage) {
        this.bufferImage = bufferImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.bufferImage);
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        /**
         * Create a new instance of the Parcelable class, instantiating it
         * from the given Parcel whose data had previously been written by
         * {@link Parcelable#writeToParcel Parcelable.writeToParcel()}.
         *
         * @param source The Parcel to read the object's data from.
         * @return Returns a new instance of the Parcelable class.
         */
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        /**
         * Create a new array of the Parcelable class.
         *
         * @param size Size of the array.
         * @return Returns an array of the Parcelable class, with every entry
         * initialized to null.
         */
        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    @Override
    public String toString() {
        return this.getBufferImage().toString();
    }

    public static List<Post> getList(Context context, Drawable a, Drawable b) {
        List<Post> posts = new ArrayList<>();
        for(int i=0; i<5; i++) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) ((i & 1) == 0 ? a : b);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Bitmap bitmap = bitmapDrawable.getBitmap();
        /*
            int size = bitmap.getWidth() * bitmap.getHeight();
            ByteBuffer byteBuffer = ByteBuffer.allocate(size * 4);
            bitmap.copyPixelsToBuffer(byteBuffer);
            posts.add(new Post(byteBuffer.array()));
        */
            bitmapDrawable.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            byte [] buffer = outputStream.toByteArray();
            posts.add(new Post(buffer));
        }
        return posts;
    }

}
