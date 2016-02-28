package vnapnic.project.debtmanager;

import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import vnapnic.project.debtmanager.download.MyImageDownloader;

/**
 * Created by Matt Allen
 * http://mattallensoftware.co.uk
 * mattallen092@gmail.com
 * <p/>
 * 17/05/14
 * <p/>
 * A simple instantiation class for the app-wide variables needed for various functions
 */
public class Application extends android.app.Application {

    private static final String TAG = Application.class.getSimpleName();

    private static DisplayImageOptions mImageOptions;
    private ImageLoaderConfiguration mLoadConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Log.d(TAG, "Image options now being set");
            mImageOptions = new DisplayImageOptions.Builder().resetViewBeforeLoading(true)
                    .cacheInMemory(true).cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.NONE)
                    .showImageOnLoading(R.drawable.friend_image_light)
                    .showImageForEmptyUri(R.drawable.friend_image_light)
                    .build();
            mLoadConfig = new ImageLoaderConfiguration.Builder(this).imageDownloader(new MyImageDownloader(this)).build();
            ImageLoader.getInstance().init(mLoadConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DisplayImageOptions getImageOptions() {
        return mImageOptions;
    }
}
