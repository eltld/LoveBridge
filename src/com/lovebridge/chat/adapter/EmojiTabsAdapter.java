package com.lovebridge.chat.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.lovebridge.R;
import com.lovebridge.chat.fragment.EmojiRecentPageFragment;

import java.util.ArrayList;
import java.util.List;

public class EmojiTabsAdapter {
    private final Context context;
    private final EmojiFragmentStatePagerAdapter pagerAdapter;
    private final List<EmojiTab> pages = new ArrayList<EmojiTab>();
    private OnTabChangeListener onTabChangeListener;
    private int selectedPage;
    private final BaseAdapter baseAdapter = new BaseAdapter() {
        public int getCount() {
            return pages.size();
        }

        public Object getItem(int i) {
            return pages.get(i);
        }

        public long getItemId(int i) {
            return 0L;
        }

        public View getView(final int i, View view, ViewGroup viewgroup) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.emoji_tab, viewgroup, false);
            }
            view.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    onTabChangeListener.tabSelected(i);
                }
            });
            ImageView imageview = (ImageView) view.findViewById(R.id.emoji_image);
            boolean flag;
            View view1;
            int j;
            int k;
            if (selectedPage == i) {
                flag = true;
            } else {
                flag = false;
            }
            imageview.setSelected(flag);
            imageview.setImageDrawable(pages.get(i).getTabDrawable());
            view1 = view.findViewById(R.id.selected_tab_indicator);
            j = selectedPage;
            k = 0;
            if (j != i) {
                k = 8;
            }
            view1.setVisibility(k);
            return view;
        }
    };
    private final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        public void onPageScrollStateChanged(int i) {
        }

        public void onPageScrolled(int i, float f, int j) {
        }

        public void onPageSelected(int i) {
            selectedPage = i;
            baseAdapter.notifyDataSetChanged();
        }
    };

    public EmojiTabsAdapter(Context context1, FragmentManager fragmentmanager) {
        selectedPage = 0;
        pages.add(new EmojiTab("recent", R.drawable.ic_tab_recent_statelist));
        pages.add(new EmojiTab("people", R.drawable.ic_tab_people_statelist));
        pages.add(new EmojiTab("nature", R.drawable.ic_tab_nature_statelist));
        pages.add(new EmojiTab("objects", R.drawable.ic_tab_objects_statelist));
        pages.add(new EmojiTab("places", R.drawable.ic_tab_places_statelist));
        pages.add(new EmojiTab("symbols", R.drawable.ic_tab_symbols_statelist));
        context = context1;
        pagerAdapter = new EmojiFragmentStatePagerAdapter(fragmentmanager);
    }

    public BaseAdapter getBaseAdapter() {
        return baseAdapter;
    }

    public int getCount() {
        return pages.size();
    }

    public FragmentStatePagerAdapter getFragmentStatePagerAdapter() {
        return pagerAdapter;
    }

    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }

    public void setTabSelectedListener(OnTabChangeListener ontabchangelistener) {
        onTabChangeListener = ontabchangelistener;
    }

    public static interface OnTabChangeListener {
        public abstract void tabSelected(int i);
    }

    private class EmojiFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
        public EmojiFragmentStatePagerAdapter(FragmentManager fragmentmanager) {
            super(fragmentmanager);
        }

        public int getCount() {
            return pages.size();
        }

        public Fragment getItem(int i) {
            String s = pages.get(i).category;
            if (s.equals("recent")) {
                return EmojiRecentPageFragment.newInstance();
            } else {
                return EmojiCategoryPageFragment.newInstance(s);
            }
        }

        public CharSequence getPageTitle(int i) {
            return pages.get(i).category;
        }
    }

    private class EmojiTab {
        public final int drawableID;
        public final String category;

        public EmojiTab(String s, int i) {
            category = s;
            drawableID = i;
        }

        public Drawable getTabDrawable() {
            return EmojiTabsAdapter.this.context.getResources().getDrawable(this.drawableID);
        }
    }


}
