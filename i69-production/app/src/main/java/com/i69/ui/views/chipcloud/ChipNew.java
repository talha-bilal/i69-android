package com.i69.ui.views.chipcloud;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.i69.R;


public class ChipNew extends LinearLayout implements View.OnClickListener {

    private int index = -1;
    private boolean selected = false;
    private ChipListener listener = null;
    private int selectedFontColor = -1;
    private int unselectedFontColor = -1;
    private TransitionDrawable crossfader;
    private int selectTransitionMS = 750;
    private int deselectTransitionMS = 500;
    private boolean isLocked = false;
    private ChipCloudNew.Mode mode;
    private int maxCount = -1;
    private ChipCloudNew.ChipSelectedCount chipCountListener;

    public ChipNew(Context context) {
        super(context);
        init();
    }

    public ChipNew(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChipNew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setChipListener(ChipListener listener) {
        this.listener = listener;
    }

    public void initChip(Context context, int index, int tag, Boolean isTags, String label, Typeface typeface, int textSizePx,
                         boolean allCaps, int selectedFontColor,
                         int unselectedFontColor, ChipCloudNew.Mode mode) {
        this.index = index;
        this.setTag(tag);
        this.selectedFontColor = selectedFontColor;
        this.unselectedFontColor = unselectedFontColor;
        this.mode = mode;

        if (selectedFontColor == -1) {
            this.selectedFontColor = ContextCompat.getColor(context, R.color.colorPrimaryDark);
        }
        if (unselectedFontColor == -1) {
            this.unselectedFontColor = ContextCompat.getColor(context, R.color.chip);
        }

        Drawable[] backgrounds = new Drawable[2];
        backgrounds[0] = ContextCompat.getDrawable(context, R.drawable.transp_background);
        if (isTags){
            backgrounds[1] = ContextCompat.getDrawable(context, R.drawable.transp_background);
        }else {
            backgrounds[1] = ContextCompat.getDrawable(context, R.drawable.transp_background);
        }
        crossfader = new TransitionDrawable(backgrounds);

        //Bug reported on KitKat where padding was removed, so we read the padding values then set again after setting background
        int leftPad = getPaddingLeft();
        int topPad = getPaddingTop();
        int rightPad = getPaddingRight();
        int bottomPad = getPaddingBottom();

        setBackgroundCompat(crossfader);
        setOrientation(LinearLayout.HORIZONTAL);

        AppCompatTextView tv1 = new AppCompatTextView(context);
        Chip chip = new Chip(context);


        addView(tv1);
        addView(chip);
        setPadding(leftPad, topPad, rightPad, bottomPad);

//        setText(label);
//        unselect();
//
//        if (typeface != null) {
//            setTypeface(typeface);
//        }
//        setAllCaps(allCaps);
//        if (textSizePx > 0) {
//            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizePx);
//        }
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public void setSelectTransitionMS(int selectTransitionMS) {
        this.selectTransitionMS = selectTransitionMS;
    }

    public void setDeselectTransitionMS(int deselectTransitionMS) {
        this.deselectTransitionMS = deselectTransitionMS;
    }

    private void init() {
        setOnClickListener(this);
    }

    private void setChipCountListener(ChipCloudNew.ChipSelectedCount chipCountListener) {
        this.chipCountListener = chipCountListener;
    }

    @Override
    public void onClick(View v) {
        boolean canSelectNewItem = true;
        if (chipCountListener != null && chipCountListener.count() == maxCount) {
            canSelectNewItem = false;
        }
        if (mode != ChipCloudNew.Mode.NONE)
            if (selected && !isLocked) {
                //set as unselected
                unselect();
                if (listener != null) {
                    listener.chipDeselected(index);
                }
            } else if (!selected) {
                //set as selected
                if (canSelectNewItem) {
                    crossfader.startTransition(selectTransitionMS);

//                    setTextColor(selectedFontColor);
                    if (listener != null) {
                        listener.chipSelected(index);
                    }
                } else {
                    listener.chipMaxCountLimit(chipCountListener.count());
                }
            }

        boolean newVal = !this.selected;
        if (newVal) {
            if (canSelectNewItem) {
                this.selected = true;
            }
        } else {
            this.selected = false;
        }
    }

    private void setMaxSelectedCount(int max) {
        this.maxCount = max;
    }

    public void select() {
        selected = true;
        crossfader.startTransition(selectTransitionMS);
//        setTextColor(selectedFontColor);
        if (listener != null) {
            listener.chipSelected(index);
        }
    }

    private void unselect() {
        if (selected) {
            crossfader.reverseTransition(deselectTransitionMS);
        } else {
            crossfader.resetTransition();
        }

//        setTextColor(unselectedFontColor);
    }

    private void setBackgroundCompat(Drawable background) {
        setBackground(background);
    }

    public void deselect() {
        unselect();
        selected = false;
    }

    public static class ChipBuilder {
        private int index, tag;
        private String label;
        private Boolean isTags;
        private Typeface typeface;
        private int textSizePx;
        private boolean allCaps;
        private int selectedFontColor;
        private int unselectedFontColor;
        private int chipHeight;
        private int selectTransitionMS = 750;
        private int deselectTransitionMS = 500;
        private ChipCloudNew.ChipSelectedCount chipCountListener;
        private ChipListener chipListener;
        private ChipCloudNew.Mode mode;
        private int maxSelectedCount = -1;

        public ChipBuilder index(int index) {
            this.index = index;
            return this;
        }

        public ChipBuilder tag(int tag) {
            this.tag = tag;
            return this;
        }

        public ChipBuilder selectedFontColor(int selectedFontColor) {
            this.selectedFontColor = selectedFontColor;
            return this;
        }

        public ChipBuilder unselectedFontColor(int unselectedFontColor) {
            this.unselectedFontColor = unselectedFontColor;
            return this;
        }

        public ChipBuilder label(String label) {
            this.label = label;
            return this;
        }

        public ChipBuilder typeface(Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        public ChipBuilder isTag(Boolean isTag){
            this.isTags = isTag;
            return this;
        }

        public ChipBuilder allCaps(boolean allCaps) {
            this.allCaps = allCaps;
            return this;
        }

        public ChipBuilder textSize(int textSizePx) {
            this.textSizePx = textSizePx;
            return this;
        }

        public ChipBuilder chipHeight(int chipHeight) {
            this.chipHeight = chipHeight;
            return this;
        }

        public ChipBuilder chipListener(ChipListener chipListener) {
            this.chipListener = chipListener;
            return this;
        }

        public ChipBuilder mode(ChipCloudNew.Mode mode) {
            this.mode = mode;
            return this;
        }

        public ChipBuilder selectTransitionMS(int selectTransitionMS) {
            this.selectTransitionMS = selectTransitionMS;
            return this;
        }

        public ChipBuilder deselectTransitionMS(int deselectTransitionMS) {
            this.deselectTransitionMS = deselectTransitionMS;
            return this;
        }

        public ChipBuilder chipCounterListener(ChipCloudNew.ChipSelectedCount chipCountListener) {
            this.chipCountListener = chipCountListener;
            return this;
        }

        public ChipBuilder chipCounter(int maxSelectedCount) {
            this.maxSelectedCount = maxSelectedCount;
            return this;
        }

        public ChipNew build(Context context) {
            ChipNew chip = (ChipNew) LayoutInflater.from(context).inflate(R.layout.chipnew, null);
            chip.initChip(context,
                    index,
                    tag,
                    isTags,
                    label,
                    typeface,
                    textSizePx,
                    allCaps,
                    selectedFontColor,
                    unselectedFontColor,
                    mode);
            chip.setSelectTransitionMS(selectTransitionMS);
            if (maxSelectedCount > 0) {
                chip.setMaxSelectedCount(maxSelectedCount);
            }
            chip.setDeselectTransitionMS(deselectTransitionMS);
            chip.setChipListener(chipListener);
            chip.setChipCountListener(chipCountListener);
//            chip.setHeight(chipHeight);
            return chip;
        }
    }
}
