package sugar6400.github.io.so2support.ui;


import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import sugar6400.github.io.so2support.R;

@GlideModule
public final class MyGlideModule extends AppGlideModule {
    public static RequestOptions iconOption;

    public void SetupOption() {
        iconOption = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.sprite_item2x_420);
    }
}
