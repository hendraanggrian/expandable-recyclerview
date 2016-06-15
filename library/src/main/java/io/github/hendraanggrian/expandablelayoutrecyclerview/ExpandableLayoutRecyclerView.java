package io.github.hendraanggrian.expandablelayoutrecyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author Hendra Anggrian (hendraanggrian@gmail.com)
 */
public class ExpandableLayoutRecyclerView extends RecyclerView {

    public ExpandableLayoutRecyclerView(Context context) {
        super(context);
    }

    public ExpandableLayoutRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableLayoutRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void addOnScrollListener(OnScrollListener listener) {
        if (!(listener instanceof OnExpandableLayoutScrollListener))
            throw new IllegalArgumentException("OnScrollListner must be an OnExpandableLayoutScrollListener");
        super.addOnScrollListener(listener);
    }

    public class OnExpandableLayoutScrollListener extends OnScrollListener {
        private int scrollState = 0;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            this.scrollState = newState;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (scrollState != SCROLL_STATE_IDLE) {
                for (int index = 0; index < getChildCount(); ++index) {
                    ExpandableLayoutItem currentExpandableLayout = (ExpandableLayoutItem) getChildAt(index).findViewWithTag(ExpandableLayoutItem.class.getName());
                    if (currentExpandableLayout.isOpened() && index != (((Adapter) getAdapter()).position - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition())) {
                        currentExpandableLayout.hideNow();
                    } else if (!currentExpandableLayout.getCloseByUser() && !currentExpandableLayout.isOpened() && index == ((((Adapter) getAdapter()).position - ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition()))) {
                        currentExpandableLayout.showNow();
                    }
                }
            }
        }
    }

    public static abstract class Adapter<VH extends ViewHolder> extends RecyclerView.Adapter<VH> {
        private final LinearLayoutManager layoutManager;
        private ExpandableMode expandableMode;
        private Integer position = -1;

        public Adapter(LinearLayoutManager layoutManager) {
            this.layoutManager = layoutManager;
            this.expandableMode = ExpandableMode.SINGLE;
        }

        public void setExpandableMode(ExpandableMode expandableMode) {
            this.expandableMode = expandableMode;
        }

        public ExpandableMode getExpandableMode() {
            return expandableMode;
        }

        public LinearLayoutManager getLayoutManager() {
            return layoutManager;
        }

        @Override
        public void onBindViewHolder(final VH holder, int position) {

            holder.getItem().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    performClick(holder.getAdapterPosition());
                }
            });
        }

        private void performClick(int position) {
            this.position = position;

            for (int index = 0; index < getLayoutManager().getChildCount(); ++index) {
                if (index != (position - getLayoutManager().findFirstVisibleItemPosition())) {
                    ExpandableLayoutItem currentExpandableLayout = (ExpandableLayoutItem) getLayoutManager().getChildAt(index).findViewWithTag(ExpandableLayoutItem.class.getName());
                    currentExpandableLayout.hide();
                }
            }

            ExpandableLayoutItem expandableLayout = (ExpandableLayoutItem) getLayoutManager().getChildAt(position - getLayoutManager().findFirstVisibleItemPosition()).findViewWithTag(ExpandableLayoutItem.class.getName());
            expandableLayout.showOrHide();
        }
    }

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {

        private boolean expanded;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        public abstract ExpandableLayoutItem getItem();
    }
}