package df.root;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.ScrollView;

/**
 * A log viewport nested inside the page ScrollView.
 *
 * The outer page is blocked from intercepting as soon as a touch starts in
 * this view. The normal ScrollView interception logic remains active, so a
 * vertical drag scrolls the log and cancels the TextView gesture. With no
 * drag, the selectable TextView still receives a long press for text copy.
 */
public class LogScrollView extends ScrollView {

    public LogScrollView(Context context) {
        super(context);
    }

    public LogScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LogScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void disallowOuterIntercept(boolean disallow) {
        ViewParent parent = getParent();
        if (parent != null) parent.requestDisallowInterceptTouchEvent(disallow);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            // Call the parent directly. This leaves this ScrollView free to
            // intercept its child once the touch becomes a vertical drag.
            disallowOuterIntercept(true);
        }

        boolean intercepted = super.onInterceptTouchEvent(event);

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            disallowOuterIntercept(false);
        }
        return intercepted;
    }
}
