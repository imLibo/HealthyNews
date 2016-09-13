package com.xieyao.healthynews.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.edmodo.cropper.CropImageView;
import com.xieyao.healthynews.R;
import com.xieyao.healthynews.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.xutils.ex.DbException;

/**
 * Created by bobo1 on 2016/5/5.
 */
public class ChooseIconActivity extends BaseActivity {
    private CropImageView mCroppImage;
    @Override
    protected void BindUI() {
        setContentView(R.layout.activity_chooseicon);
    }

    @Override
    protected String getmTitle() {
        return "头像裁剪";
    }

    @Override
    protected void initViews() throws DbException {

        String imagepath = getIntent().getStringExtra("imagePath");
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mCroppImage = (CropImageView) findViewById(R.id.CropImageView);
        Bitmap bitmap = BitmapFactory.decodeFile(imagepath);
        mCroppImage.setImageBitmap(bitmap);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_menu_favorite:
                        try {
                            EventBus.getDefault().post(mCroppImage.getCroppedImage());
                            System.gc();
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.menu_choose,menu);
        return super.onCreateOptionsMenu(menu);
    }


}
