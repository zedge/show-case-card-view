package ru.dimorinny.showcasecard.step;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.View;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

import ru.dimorinny.showcasecard.R;
import ru.dimorinny.showcasecard.ShowCaseView;
import ru.dimorinny.showcasecard.position.ViewPosition;
import ru.dimorinny.showcasecard.radius.Radius;

/**
 * Created by Frank on 2017/08/16.
 * <p>
 * Controls the displaying of a list of {@link ShowCaseStep}'s one by one.
 */
public class ShowCaseStepDisplayer {

    private final ViewClickedListener viewClickedListener;
    private Context context;

    @Nullable
    private Activity activity;
    @Nullable
    private Fragment fragment;
    @Nullable
    private ScrollView scrollView;
    @LayoutRes
    private int customLayout = R.layout.item_show_case_content;
    @ColorRes
    private int backgroundColor;

    private boolean shouldDisplayProgress = false;
    private boolean radiusProportionalToView = false;

    /**
     * All items to be displayed.
     */
    private List<ShowCaseStep> items = new ArrayList<>();

    @Nullable
    private ShowCaseStepScroller showCaseStepScroller;

    /**
     * Index of the currently shown item in the items list.
     */
    private int currentlyDisplayedTipIndex = -1;

    @Nullable
    private ShowCaseView showCaseView;

    /**
     * @param scrollView scrollView to use on all {@link ShowCaseStep}'s that dictate
     *                   scrolling on activation.
     */
    private ShowCaseStepDisplayer(@Nullable Activity activity, @Nullable Fragment fragment, @Nullable ScrollView scrollView, @LayoutRes int layout, boolean shouldDisplayProgress, @ColorRes int backgroundColor, boolean radiusProportionalToView, ViewClickedListener viewClickedListener) {
        this.activity = activity;
        this.fragment = fragment;
        this.scrollView = scrollView;
        this.customLayout = layout;
        this.shouldDisplayProgress = shouldDisplayProgress;
        this.backgroundColor = backgroundColor;
        this.radiusProportionalToView = radiusProportionalToView;
        this.viewClickedListener = viewClickedListener;

        //noinspection ConstantConditions
        this.context = activity != null ? activity : fragment.getContext();

        if (scrollView != null) {
            showCaseStepScroller = new ShowCaseStepScroller(scrollView);
        }
    }

    /**
     * Starts the tip-flow. Toggles (on click) through all help items on this page.
     */
    public void start() {
        tryShowNextTip();
    }

    /**
     * Closes and resets the tips screen.
     */
    public void dismiss() {

        if (showCaseView != null) {
            showCaseView.hide();
        }

        currentlyDisplayedTipIndex = -1;
        items.clear();
    }

    /**
     * Displays the next tip on the screen, or closes the tip screen if no more tips are left.
     */
    private void tryShowNextTip() {

        if (!isContextActive()) {
            return;
        }

        if (currentlyDisplayedTipIndex >= items.size() - 1) {

            // end of tips reached.
            dismiss();
        } else {

            currentlyDisplayedTipIndex++;
            displayTip(items.get(currentlyDisplayedTipIndex));
        }
    }

    /**
     * Displays one tip on the screen. Tapping the screen will dismiss it.
     *
     * @param item tip details to display.
     */
    private void displayTip(final ShowCaseStep item) {

        if (item.getPosition().getScrollPosition(scrollView) != null
                && showCaseStepScroller != null) {
            // try to scroll to the item

            if (showCaseView != null) {
                // hide last card, just show dark overlay for now:
                showCaseView.hideCard();
            }

            // scroll first, after that display the item:
            showCaseStepScroller.scrollToShowCaseStepItem(
                    item,
                    new ShowCaseStepScroller.OnCompleteListener() {
                        @Override
                        public void onComplete() {
                            doDisplayTip(item);
                        }
                    }
            );

        } else {
            // display item right away
            doDisplayTip(item);
        }
    }

    private void doDisplayTip(ShowCaseStep item) {

        if (!isContextActive()) {
            return;
        }

        if (showCaseView != null) {
            // completely remove old view now:
            showCaseView.hide();
        }

        int showCaseRadius = (int) context.getResources().getDimension(R.dimen.default_showcase_radius);

        if (radiusProportionalToView && item.getPosition() instanceof ViewPosition) {
            ViewPosition viewPosition = (ViewPosition) item.getPosition();
            showCaseRadius = viewPosition.getView().getWidth();
        }

        final int myTipIndex = currentlyDisplayedTipIndex;
        showCaseView = new ShowCaseView.Builder(context)
                .withTypedPosition(item.getPosition())
                .setLayout(customLayout)
                .withTypedRadius(new Radius(showCaseRadius))
                .dismissOnTouch(false)
                .withColor(backgroundColor)
                .withTouchListener(new ShowCaseView.TouchListener() {
                    @Override
                    public void onTouchEvent(boolean clickedCircle) {
                        if (clickedCircle && viewClickedListener != null) {
                            dismiss();
                            viewClickedListener.onViewClicked(showCaseView);
                        } else {
                            if (myTipIndex == currentlyDisplayedTipIndex) {
                                tryShowNextTip();
                            }
                        }
                    }
                })
                .withContent(item.getTitle(), item.getMessage())
                .build();

        if (items.size() > 1 && shouldDisplayProgress) {
            showCaseView.setProgressIndicator(currentlyDisplayedTipIndex, items.size());
        }
        if (activity == null) {
            showCaseView.show(fragment);
        } else {
            showCaseView.show(activity);
        }
    }

    @SuppressWarnings("unused")
    public Context getContext() {
        return context;
    }

    /**
     * Adds on item to the list of items to display.
     *
     * @param item
     */
    @SuppressWarnings("unused")
    public void addStep(ShowCaseStep item) {
        items.add(item);
    }

    /**
     * Sets the list of items to display.
     *
     * @param items
     */
    @SuppressWarnings("unused")
    public void setSteps(List<ShowCaseStep> items) {
        this.items = items;
    }

    /**
     * Returns true if the attached Context is still active / not shutting down.
     */
    private boolean isContextActive() {
        if (fragment != null) {
            return fragment.isAdded();
        } else if (activity != null) {
            return !activity.isFinishing();
        }
        return true;
    }

    public static class Builder {

        @Nullable
        private Activity activity;
        @Nullable
        private Fragment fragment;
        /**
         * ScrollView used on all {@link ShowCaseStep}'s that used to scroll to the View
         * on activation.
         */
        @Nullable
        private ScrollView scrollView;

        @LayoutRes
        private int customLayout;

        private List<ShowCaseStep> items = new ArrayList<>();
        private boolean shouldDisplayProgress = false;
        private @ColorRes int backgroundColor = R.color.black20;
        private boolean radiusProportionalToView;
        private ViewClickedListener viewClickedListener = null;

        @SuppressWarnings("unused")
        public Builder(@NonNull Fragment fragment) {
            this.fragment = fragment;
        }

        @SuppressWarnings("unused")
        public Builder(@NonNull Activity activity) {
            this.activity = activity;
        }

        /**
         * ScrollView used on all {@link ShowCaseStep}'s to scroll to the View
         * on activation.
         */
        public Builder withScrollView(@Nullable ScrollView scrollView) {
            this.scrollView = scrollView;
            return this;
        }

        public Builder withCustomLayout(@LayoutRes int layout) {
            this.customLayout = layout;
            return this;
        }

        public Builder setShouldDisplayProgress(boolean shouldDisplayProgress) {
            this.shouldDisplayProgress = shouldDisplayProgress;
            return this;
        }
        
        public Builder withClickViewListener(ViewClickedListener listener) {
            this.viewClickedListener = listener;
            return this;
        }

        /**
         * Set to true to make the radius of the circle proportional to the view
         * @param radiusProportionalToView
         * @return
         */
        public Builder setRadiusProportionalToView(boolean radiusProportionalToView) {
            this.radiusProportionalToView = radiusProportionalToView;
            return this;
        }

        /**
         * Sets the color of the background for a ShowCaseStep scenario
         * @param backgroundColor
         */
        public Builder withColor(@ColorRes int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        /**
         * Adds on item to the list of items to display.
         *
         * @param item
         */
        @SuppressWarnings("unused")
        public Builder addStep(ShowCaseStep item) {
            items.add(item);
            return this;
        }

        @SuppressWarnings("unused")
        public ShowCaseStepDisplayer build() {

            ShowCaseStepDisplayer stepController =
                    new ShowCaseStepDisplayer(activity, fragment, scrollView, customLayout, shouldDisplayProgress, backgroundColor, radiusProportionalToView, viewClickedListener);

            stepController.setSteps(items);

            return stepController;
        }
    }
    
    public interface ViewClickedListener {
        void onViewClicked(ShowCaseView view);
    }
}
