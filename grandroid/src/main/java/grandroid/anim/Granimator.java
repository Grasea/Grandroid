/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.anim;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import grandroid.action.Action;

/**
 *
 * @author Rovers
 */
public class Granimator {

    public enum MotionType {

        Size, Alpha;
    }

    public static Animator animate(View view) {
        return new Animator(view);
    }

    public static TranslateAnimation createTranslateAnimation(int duration,int translateType, float[] from, float[] to, final Action finishAction) {
        TranslateAnimation ani = new TranslateAnimation(translateType, from[0], translateType, to[0], translateType, from[1], translateType, to[1]);
        ani.setDuration(duration);
        if (finishAction != null) {
            ani.setAnimationListener(new Animation.AnimationListener() {

                public void onAnimationStart(Animation arg0) {
                }

                public void onAnimationEnd(Animation arg0) {
                    //be careful, it will run on background thread
                    finishAction.execute();
                }

                public void onAnimationRepeat(Animation arg0) {
                }
            });
        }
        return ani;
    }

    public static RotateAnimation createRotateAnimation(int duration, int spanCount, final Action finishAction) {
        RotateAnimation ani = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        ani.setInterpolator(new IndexedInterpolator(spanCount));
        ani.setRepeatCount(Animation.INFINITE);
        ani.setDuration(duration);

        if (finishAction != null) {
            ani.setAnimationListener(new Animation.AnimationListener() {

                public void onAnimationStart(Animation arg0) {
                }

                public void onAnimationEnd(Animation arg0) {
                    //be careful, it will run on background thread
                    finishAction.execute();
                }

                public void onAnimationRepeat(Animation arg0) {
                }
            });
        }
        return ani;
    }

    public static ScaleAnimation createScaleAnimation(int startOffset, int duration, float fromScale, float toScale, final Action finishAction) {
        ScaleAnimation ani = new ScaleAnimation(fromScale, toScale, fromScale, toScale, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ani.setInterpolator(new LinearInterpolator());
        ani.setStartOffset(startOffset);
        ani.setDuration(duration);
        ani.setFillAfter(true);
        if (finishAction != null) {
            ani.setAnimationListener(new Animation.AnimationListener() {

                public void onAnimationStart(Animation arg0) {
                }

                public void onAnimationEnd(Animation arg0) {
                    //be careful, it will run on background thread
                    finishAction.execute();
                }

                public void onAnimationRepeat(Animation arg0) {
                }
            });
        }
        return ani;
    }

    public static Animation createAlphaAnimation(int startOffset, int duration, float fromAlpha, float toAlpha, final Action finishAction) {
        AlphaAnimation ani = new AlphaAnimation(fromAlpha, toAlpha);
        ani.setInterpolator(new LinearInterpolator());
        ani.setStartOffset(startOffset);
        ani.setDuration(duration);
        ani.setFillAfter(true);
        if (finishAction != null) {
            ani.setAnimationListener(new Animation.AnimationListener() {

                public void onAnimationStart(Animation arg0) {
                }

                public void onAnimationEnd(Animation arg0) {
                    //be careful, it will run on background thread
                    finishAction.execute();
                }

                public void onAnimationRepeat(Animation arg0) {
                }
            });
        }
        return ani;
    }
//
//    public static void makeViewTransition(View view, AnimationSet as1, AnimationSet as2, Action finishAction1, Action finishAction2) {
//        AnimationSet as = new AnimationSet(true);
//        //1代表完全不透明，0代表完全透明
//        AlphaAnimation aa1 = new AlphaAnimation(fromAlpha1, toAlpha1);
//        aa1.setStartOffset(startOffset1);
//        aa1.setDuration(duration1);
//        as.addAnimation(aa1);
//        if (duration2 > 0) {
//            AlphaAnimation aa2 = new AlphaAnimation(fromAlpha2, toAlpha2);
//            aa2.setStartOffset(startOffset2);
//            aa2.setDuration(duration2);
//            as.addAnimation(aa2);
//        }
//
//        as.setAnimationListener(new Animation.AnimationListener() {
//            public void onAnimationStart(Animation arg0) {
//            }
//
//            public void onAnimationEnd(Animation arg0) {
//                view.postDelayed(new Runnable() {
//                    public void run() {
//                        if (finishAction != null) {
//                            finishAction.execute();
//                        }
//                    }
//                }, 0);
//            }
//
//            public void onAnimationRepeat(Animation arg0) {
//            }
//        });
//        view.startAnimation(as);
//
//    }

    public static Animation makeVisibleMotion(View view, int startOffset1, int duration1, float fromAlpha1, float toAlpha1, final Action finishAction) {
        return makeVisibleMotion(view, startOffset1, duration1, fromAlpha1, toAlpha1, finishAction, 0, 0, 1.0f, 1.0f);
    }

    public static Animation makeVisibleMotion(final View view, int startOffset1, int duration1, float fromAlpha1, float toAlpha1, final Action finishAction, int startOffset2, int duration2, float fromAlpha2, float toAlpha2) {
//        AnimationSet as = new AnimationSet(false);
        //as.setFillAfter(true);
        //1代表完全不透明，0代表完全透明
        view.clearAnimation();
        if (duration2 > 0) {
            final Animation a2 = createAlphaAnimation(startOffset2, duration2, fromAlpha2, toAlpha2, finishAction);
            view.startAnimation(createAlphaAnimation(startOffset1, duration1, fromAlpha1, toAlpha1, new Action() {

                @Override
                public boolean execute() {
                    view.clearAnimation();
                    view.startAnimation(a2);
                    return true;
                }
            }));
        } else {
            view.startAnimation(createAlphaAnimation(startOffset1, duration1, fromAlpha1, toAlpha1, finishAction));
        }
        return null;
    }

    public static Animation makeVerticalMotion(final View view, int startOffset, int duration, final Action finishAction) {
        final TranslateAnimation mAnimation2 = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, -1);
        mAnimation2.setDuration(duration);
        mAnimation2.setStartOffset(startOffset);
        mAnimation2.setRepeatMode(Animation.RESTART);

//        final LinearLayout container = new LinearLayout(tvMarquee.getContext());
//        ViewGroup vg = (ViewGroup) tvMarquee.getParent();
//        ViewGroup.LayoutParams lp = tvMarquee.getLayoutParams();
//        vg.removeView(tvMarquee);
//        vg.addView(container, lp);
//        container.addView(tvMarquee, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, height));
        mAnimation2.setAnimationListener(new Animation.AnimationListener() {
            int index = -1;

            public void onAnimationStart(Animation arg0) {
            }

            public void onAnimationEnd(Animation arg0) {
                view.postDelayed(new Runnable() {
                    public void run() {
                        finishAction.execute();
                    }
                }, 0);
            }

            public void onAnimationRepeat(Animation arg0) {
            }
        });
        view.startAnimation(mAnimation2);
        return mAnimation2;
    }

    public static Animation makeVerticalMarquee(TextView tvMarquee, String[] msgs, int duration, int delay) {
        return makeVerticalMarquee(tvMarquee, msgs, null, duration, delay);
    }

    public static Animation makeVerticalMarquee(final TextView tvMarquee, final String[] msgs, final Action[] actions, int duration, final int delay) {
        final TranslateAnimation mAnimation2 = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_PARENT, 0);
        mAnimation2.setDuration(duration);
        mAnimation2.setStartOffset(200);
        mAnimation2.setRepeatMode(Animation.RESTART);
        tvMarquee.setAnimation(mAnimation2);

//        final LinearLayout container = new LinearLayout(tvMarquee.getContext());
//        ViewGroup vg = (ViewGroup) tvMarquee.getParent();
//        ViewGroup.LayoutParams lp = tvMarquee.getLayoutParams();
//        vg.removeView(tvMarquee);
//        vg.addView(container, lp);
//        container.addView(tvMarquee, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, height));
        //tvMarquee.setBackgroundColor(Color.rgb(155, 100, 100));
        if (actions != null) {
            tvMarquee.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (view.getTag() != null && view.getTag() instanceof Action) {
                        ((Action) view.getTag()).execute();
                    }
                }
            });
        }
        mAnimation2.setAnimationListener(new Animation.AnimationListener() {
            int index = -1;

            public void onAnimationStart(Animation arg0) {
                index = index >= msgs.length - 1 ? 0 : index + 1;
                tvMarquee.setText(msgs[index]);
                if (actions != null) {
                    tvMarquee.setTag(actions[index]);
                }
            }

            public void onAnimationEnd(Animation arg0) {
                tvMarquee.postDelayed(new Runnable() {
                    public void run() {
                        //mAnimation2.start();
                        tvMarquee.startAnimation(mAnimation2);
                    }
                }, delay);
            }

            public void onAnimationRepeat(Animation arg0) {
//                index = index >= msgs.length - 1 ? 0 : index + 1;
//                tvMarquee.setText(msgs[index]);
            }
        });
        return mAnimation2;
    }
}
