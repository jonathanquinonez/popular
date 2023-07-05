/**
 *  Project: CIBP
 *  Company: Evertec
 */
package com.popular.android.mibanco.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

/**
 * Methods that provide  show and hide with fade
 * @see FadeViewAnimation
 * @since Java 1.8
 */
public class FadeViewAnimation {

    /**
     * Method that provides a view to show with fading
     * @param view view
     * @param time animation view transition
     */
    public static void showFadeViewAnimation (View view, Long time) {
        view.setAlpha(0f);
        view.animate()
                .alpha(1.0f)
                .setDuration(time)
                .setListener(null);
    }

    /**
     * Method that provides a view to hide with fading
     * @param view view
     * @param time animation time
     * @param visibility visible or gone
     */
    public static void hideFadeViewAnimation (final View view, Long time, final int visibility) {
        view.setAlpha(1f);
        view.animate()
                .alpha(0.0f)
                .setDuration(time)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(visibility);
                    }
                });
    }
}
