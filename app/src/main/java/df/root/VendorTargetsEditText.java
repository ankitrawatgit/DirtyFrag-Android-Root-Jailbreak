package df.root;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.material.textfield.TextInputEditText;

/**
 * Keeps a vertical drag inside the multiline vendor-target editor. Without
 * this, the settings NestedScrollView can intercept the gesture and scroll the
 * whole page instead of the text field.
 */
public class VendorTargetsEditText extends TextInputEditText {

    public VendorTargetsEditText(Context context) {
        super(context);
    }

    public VendorTargetsEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VendorTargetsEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if (action == MotionEvent.ACTION_UP) performClick();
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
