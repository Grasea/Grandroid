/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandroid.anim;

import android.view.View;
import android.view.animation.Animation;
import grandroid.action.Action;
import java.util.ArrayList;

/**
 *
 * @author Rovers
 */
public class Animator {

    protected View view;
    protected ArrayList<Animation> anims;
    protected ArrayList<Action> acts;
    protected int playCount;//0 = loop

    public Animator(View view) {
        this.view = view;
        anims = new ArrayList<Animation>();
        acts = new ArrayList<Action>();
        playCount = 1;
    }

    public Animator loop() {
        this.playCount = 0;
        return this;
    }

    public Animator rotate(int duration, int spanCount, Action act) {
        anims.add(Granimator.createRotateAnimation(duration, spanCount, null));
        acts.add(act);
        return this;
    }

    public Animator translate(int duration, int translateType, float[] from, float[] to, Action act) {
        anims.add(Granimator.createTranslateAnimation(duration, translateType, from, to, act));
        acts.add(act);
        return this;
    }

    public Animator alpha(int duration, float from, float to, Action act) {
        anims.add(Granimator.createAlphaAnimation(0, duration, from, to, null));
        acts.add(act);
        return this;
    }

    public Animator scale(int duration, float from, float to, Action act) {
        anims.add(Granimator.createScaleAnimation(0, duration, from, to, null));
        acts.add(act);
        return this;
    }

    public void start() {
        if (anims.size() > 1) {
            for (int i = 0; i < anims.size() - 1; i++) {
                final int index = i;
                anims.get(i).setAnimationListener(new Animation.AnimationListener() {

                    public void onAnimationStart(Animation arg0) {
                    }

                    public void onAnimationEnd(Animation arg0) {
                        if (acts.get(index) != null) {
                            acts.get(index).setSrc(view).execute();
                        }
                        view.clearAnimation();
                        view.startAnimation(anims.get(index + 1));
                    }

                    public void onAnimationRepeat(Animation arg0) {
                    }
                });
            }
        }
        if (anims.size() > 0) {
            anims.get(anims.size() - 1).setAnimationListener(new Animation.AnimationListener() {

                public void onAnimationStart(Animation arg0) {
                }

                public void onAnimationEnd(Animation arg0) {
                    if (playCount > 0) {
                        view.clearAnimation();
                        if (acts.get(anims.size() - 1) != null) {
                            acts.get(anims.size() - 1).setSrc(view).execute();
                        }
                    } else {
                        if (acts.get(anims.size() - 1) != null) {
                            if (acts.get(anims.size() - 1).setSrc(view).execute()) {
                                for (Animation ani : anims) {
                                    ani.reset();
                                }
                                view.startAnimation(anims.get(0));
                            }
                        }
                    }
                }

                public void onAnimationRepeat(Animation arg0) {
                }
            });
            view.startAnimation(anims.get(0));
        }
    }
}
