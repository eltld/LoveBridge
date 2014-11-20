
package com.lovebridge.chat.view.tabs;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lovebridge.R;

public class ChatTabLayout extends RelativeLayout {
    class CachedChatTabInfo {
        public final Bitmap bitmap;
        public final String title;

        private CachedChatTabInfo(Bitmap bitmap, String title) {
            super();
            this.bitmap = bitmap;
            this.title = title;
        }
    }

    private static final int ANIMATION_DURATION = 0x64;
    private static final int AVATAR_BOUNCE_LIMIT = 3;
    private static final int AVATAR_BOUNCE_REPEAT_DELAY = 0x7D0;
    private static final int NUM_OBJECTS_TO_CACHE = 0x14;
    private final ImageView avatar;
    private final AnimationSet avatarAnimation;
    private boolean avatarAnimationIsAnimating;
    private final View avatarWrapper;
    private static Map<Message, Integer> bounceCount;
    private static LruCache<Addresses, CachedChatTabInfo> cache;
    private ChatTabEntry chatTabEntry;
    private final TextView title;
    private final TextView unreadIndicator;

    static {
        ChatTabLayout.bounceCount = new HashMap<Message, Integer>();
        ChatTabLayout.resetCache();
    }

    public ChatTabLayout(Context context) {
        super(context);
        this.avatarAnimationIsAnimating = false;
        ChatTabLayout.inflate(this.getContext(), R.layout.tab_chat, ((ViewGroup)this));
        this.setClipToPadding(true);
        this.avatar = (ImageView)this.findViewById(R.id.avatar);
        this.avatarWrapper = this.findViewById(R.id.avatar_wrapper);
        this.avatarAnimation = (AnimationSet)AnimationUtils.loadAnimation(context, R.anim.tabs_avatar_bounce);
        this.title = (TextView)this.findViewById(R.id.title);
        this.unreadIndicator = (TextView)this.findViewById(R.id.unread_indicator);
        this.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                ChatTabLayout.this.chatTabEntry.selectTab();
            }
        });
        this.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(View v) {
                ChatTabLayout.this.chatTabEntry.deleteChat();
                return true;
            }
        });
    }

    public void loadChatTabContents(ChatTabEntry chatTabEntry) {
        this.chatTabEntry = chatTabEntry;
        Addresses addresses = chatTabEntry.getAddresses();
        this.avatar.clearAnimation();
        this.title.clearAnimation();
        CachedChatTabInfo info = ChatTabLayout.cache.get(addresses);
        if (info != null) {
            this.updateContent(info);
        } else {
            this.title.setText("消息");
            this.title.setVisibility(4);
            this.unreadIndicator.setVisibility(8);
            ScaleAnimation scaleAnimation = new ScaleAnimation(0f, 1f, 0f, 1f,
                            ((float)(ChatTabLayout.this.avatar.getWidth() / 2)),
                            ((float)(ChatTabLayout.this.avatar.getHeight() / 2)));
            ((Animation)scaleAnimation).setDuration(ANIMATION_DURATION);
            ((Animation)scaleAnimation).setFillAfter(true);
            ChatTabLayout.this.avatar.startAnimation(((Animation)scaleAnimation));
            AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
            ((Animation)alphaAnimation).setDuration(0x64);
            ((Animation)alphaAnimation).setFillAfter(true);
            ChatTabLayout.this.title.startAnimation(((Animation)alphaAnimation));
        }
    }

    public static void resetBounces() {
        ChatTabLayout.bounceCount.clear();
    }

    public static void resetCache() {
        ChatTabLayout.cache = new LruCache<Addresses, CachedChatTabInfo>(NUM_OBJECTS_TO_CACHE);
    }

    private void bounceChatTab(boolean withDelay) {
        if (!this.avatarAnimationIsAnimating) {
            this.incrementBounceCount();
            AnimationSet animationSet = this.avatarAnimation;
            int i = withDelay ? AVATAR_BOUNCE_REPEAT_DELAY : 0;
            animationSet.setStartOffset(((long)i));
            this.avatarAnimation.setAnimationListener(new Animation.AnimationListener() {

                public void onAnimationEnd(Animation anim) {
                    ChatTabLayout.this.clearBounceChatTab();
                    if (ChatTabLayout.this.canChatTabContinueBouncing()) {
                        ChatTabLayout.this.bounceChatTab(true);
                    }
                }

                public void onAnimationRepeat(Animation anim) {
                }

                public void onAnimationStart(Animation anim) {
                    ChatTabLayout.this.avatarAnimationIsAnimating = true;
                }
            });
            this.avatarWrapper.startAnimation(this.avatarAnimation);
        }
    }

    private boolean canChatTabContinueBouncing() {
        Object object = ChatTabLayout.bounceCount.get(this.chatTabEntry.getMessage());
        boolean bool = object == null
                        || ((Integer)object).intValue() >= this.avatarAnimation.getAnimations().size()
                                        * AVATAR_BOUNCE_LIMIT ? false : true;
        return bool;
    }

    private boolean canChatTabStartBouncing() {

        return false;
    }

    private void clearBounceChatTab() {
        this.avatarAnimationIsAnimating = false;
        this.avatarAnimation.setAnimationListener(null);
        this.avatarWrapper.clearAnimation();
    }

    private void incrementBounceCount() {
        Message message = this.chatTabEntry.getMessage();
        Object object = ChatTabLayout.bounceCount.get(message);
        int i = object == null ? 1 : Integer.valueOf(((Integer)object).intValue() + 1).intValue();
        bounceCount.put(message, Integer.valueOf(i));
    }

    private void updateContent(CachedChatTabInfo info) {
        this.avatar.setImageBitmap(info.bitmap);
        this.title.setText(info.title);
        this.title.setVisibility(0);
        this.unreadIndicator.setText("message");
        if (this.canChatTabStartBouncing()) {
            this.bounceChatTab(false);
        } else {
            this.clearBounceChatTab();
        }
    }
}