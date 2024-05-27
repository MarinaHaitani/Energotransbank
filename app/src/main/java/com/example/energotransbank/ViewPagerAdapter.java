package com.example.energotransbank;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

    Context context;
    List<ScreenItem> listScreen;

    public ViewPagerAdapter(Context context, List<ScreenItem> listScreen) {
        this.context = context;
        this.listScreen = listScreen;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.layout_screen, null);

        ImageView imageBoard = layoutScreen.findViewById(R.id.imageBoard);
        ImageView imagePlus = layoutScreen.findViewById(R.id.imagePlus);

        TextView nameBoard = layoutScreen.findViewById(R.id.nameBoard);
        TextView descriptionBoard = layoutScreen.findViewById(R.id.descriptionBoard);
        TextView skip = layoutScreen.findViewById(R.id.skip);

        nameBoard.setText(listScreen.get(position).getNameBoard());
        descriptionBoard.setText(listScreen.get(position).getDescriptionBoard());
        skip.setText(listScreen.get(position).getSkip());
        imageBoard.setImageResource(listScreen.get(position).getImageBoard());
        imagePlus.setImageResource(listScreen.get(position).getImagePlus());

        container.addView(layoutScreen);
        return layoutScreen;
    }

    @Override
    public int getCount() {
        return listScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
