package com.bw.yxx.animtro_gift;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class GiftView extends RelativeLayout {
    private static TranslateAnimation giftLayoutInAnim;//飞进动画
    private static Animation giftOutAnim;//飞出
    private static ArrayList<AnimMessage> giftlist = new ArrayList<>();
    private static LinearLayout animViewContainer;//作为动画的容器
    public GiftView(Context context,LinearLayout linearLayout,AnimMessage animMessage,String UserName,String GiftName) {
        super(context);
        init(linearLayout,animMessage,UserName,GiftName);
    }

    public GiftView(Context context, AttributeSet attrs,LinearLayout linearLayout,AnimMessage animMessage,String UserName,String GiftName) {
        super(context, attrs);
        init(linearLayout,animMessage,UserName,GiftName);
    }

    public GiftView(Context context, AttributeSet attrs, int defStyleAttr,LinearLayout linearLayout,AnimMessage animMessage,String UserName,String GiftName) {
        super(context, attrs, defStyleAttr);
        init(linearLayout,animMessage,UserName,GiftName);
    }
    private void init(LinearLayout linearLayout,AnimMessage animMessage,String UserName,String GiftName){
        giftOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.gift_out);
        animViewContainer = linearLayout;
        giftlist.add(animMessage);
        giftLayoutInAnim = (TranslateAnimation) AnimationUtils.loadAnimation(getContext(),R.anim.gift_in);
        LayoutInflater.from(getContext()).inflate(R.layout.item_gift_animal, this);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

        setLayoutParams(layoutParams);
        TextView sendUser = findViewById(R.id.send_user);
        TextView giftName = findViewById(R.id.gift_name);
        TextView giftNumView = findViewById(R.id.giftNum);
        RelativeLayout giftTextLayout = findViewById(R.id.rlparent);
        giftNumView.setTag(1);//给数量控件设置标记
        sendUser.setText(UserName);
        giftName.setText("送出:"+GiftName);
        setTag(animMessage);//设置view的标识
        //开始执行动画
        giftTextLayout.setVisibility(VISIBLE);
        giftTextLayout.startAnimation(giftLayoutInAnim);//开始执行显示礼物的动画


        giftLayoutInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                giftNumView.setVisibility(View.VISIBLE);
                giftNumView.setText("x" + giftNumView.getTag());
                startComboAnim(giftNumView);//设置一开始的连击事件
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void startComboAnim(View giftNumView) {
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(giftNumView, "scaleX", 1.8f, 1.0f);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(giftNumView, "scaleY", 1.8f, 1.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(300);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.playTogether(animator1, animator2);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 为了让新添加的礼物可以正常的计时消失
                giftNumView.setTag((Integer) giftNumView.getTag() + 1);
                //如果当前显示中的礼物数量L<=AnimMessage中的数量,继续触发Combo动画
                if ((Integer) giftNumView.getTag() <= ((AnimMessage) getTag()).getGiftNum()) {
                    ((TextView) giftNumView).setText("x" + giftNumView.getTag());
                    startComboAnim(giftNumView);
                } else {
                    int count = animViewContainer.getChildCount();
                    for (int i = 0; i < count; i++) {
                        removeAnimalView(i);
                        return;
                    }
                    return;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }
    private void removeAnimalView(int index) {
        //如果index超出linearLayout子View的数量,不继续执行
        if (index >= animViewContainer.getChildCount()) {
            return;
        }
        View removeView = animViewContainer.getChildAt(index);//找到需要删除的View
        giftOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().post(() -> {
                    animViewContainer.removeViewAt(index);//根据index删除当前礼物View
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ((Activity)getContext()).runOnUiThread(() -> removeView.startAnimation(giftOutAnim));
    }
}
