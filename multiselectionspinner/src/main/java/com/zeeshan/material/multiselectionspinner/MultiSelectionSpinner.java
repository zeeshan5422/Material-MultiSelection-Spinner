package com.zeeshan.material.multiselectionspinner;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MultiSelectionSpinner extends TextInputEditText {

    private final String TAG = getClass().getSimpleName();

    private static final long POPUP_DISMISS_DELAY = 200;

    private String mText = "";
    private int defaultHeight = 130;
    private int rightMargin = 10;
    private int POPUP_WINDOW_MAX_HEIGHT_DP = 300;

    private static final int LABEL_ALIGN_CENTER = 0;
    private static final int LABEL_ALIGN_LEFT = 1;
    private int labelAlignment = LABEL_ALIGN_CENTER;

    private boolean showSearch = false;


    private PopupWindow popupWindow = null;
    private float SCALE_RATIO = 1.0f;

    private boolean isDisabled = false;
    private boolean sort = true;
    private boolean showClearSelection;


    private List<Object> originalList;
    private List<Object> mSelectedItems;
    private ItemsAdapter itemsAdapter = null;
    private OnItemSelectedListener mOnItemSelectedListener;

    public MultiSelectionSpinner(Context context) {
        super(context);
        init();
    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrbs(attrs);
    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

        initAttrbs(attrs);
    }


    private void initAttrbs(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MultiSelectionSpinner,
                0, 0);
        try {
            showSearch = a.getBoolean(R.styleable.MultiSelectionSpinner_showSearch, false);
            labelAlignment = a.getInt(R.styleable.MultiSelectionSpinner_label_alignment, LABEL_ALIGN_CENTER);
            isDisabled = a.getBoolean(R.styleable.MultiSelectionSpinner_disabled, false);
            sort = a.getBoolean(R.styleable.MultiSelectionSpinner_sort, true);
            showClearSelection = a.getBoolean(R.styleable.MultiSelectionSpinner_clear_selection, true);
            SCALE_RATIO = a.getFloat(R.styleable.MultiSelectionSpinner_width_ratio, 1.0f);
        } finally {
            a.recycle();
        }
/*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            setBackgroundResource(outValue.resourceId);
        }*/

        POPUP_WINDOW_MAX_HEIGHT_DP = Utils.dpToPx(getContext(), 300);

    }

    private void init() {

        setCursorVisible(false);
        setFocusable(false);
        setFocusableInTouchMode(false);

        super.setOnClickListener(mClickListener);
    }

    public void setOnItemSelectedListener(@Nullable OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
    }

    public void setText(String text) {
        super.setText(text);
    }

    public boolean isSort() {
        return sort;
    }

    public void setSort(boolean sort) {
        this.sort = sort;
    }

    public interface OnItemSelectedListener {

        void onItemSelected(View view, boolean isSelected, int position);

        void onSelectionCleared();
    }

    @Override
    public void setOnClickListener(View.OnClickListener l) {
        // disable external click listeners
    }

    public void setItems(List objects) {
        clear();
        this.originalList = objects;
        if (sort) {
            if (this.originalList != null && this.originalList.size() > 0) {
                Collections.sort(this.originalList, new Comparator<Object>() {
                    @Override
                    public int compare(final Object object1, final Object object2) {
                        String str1 = object1.toString();
                        String str2 = object2.toString();
                        if (str1 == null && str2 == null) {
                            return 0;
                        } else if (str1 == null && str2 != null) {
                            return -1;
                        } else if (str1 != null && str2 == null) {
                            return 1;
                        }
                        return object1.toString().compareTo(object2.toString());
                    }
                });
            }
        }
    }

    public void clear() {
        mSelectedItems = null;
        setText("");
        if (originalList != null) {
            originalList = null;
        }
        if (itemsAdapter != null) {
            itemsAdapter = null;
        }
    }

    public List<Object> getSelectedItems() {
        return mSelectedItems;
    }

    public void setSelection(int position) {
        setSelection(position, false);
    }

    public void setSelection(int position, boolean withCallback) {
        if (originalList == null) {
            return;
        }
        if (position < 0) {
            setSelection("", withCallback);
            return;
        }

        if (position >= originalList.size()) {
            return;
        }
        setSelection(originalList.get(position), withCallback);
    }

    public void setSelection(Object item) {
        setSelection(item, false);
    }

    public void setSelection(Object item, boolean withCallback) {
        if (item == null || originalList == null) {
            mSelectedItems = null;
            setText("");
            if (withCallback && mOnItemSelectedListener != null) {
                mOnItemSelectedListener.onItemSelected(this, false, -1);
            }
        } else {

            if (mSelectedItems == null) {
                mSelectedItems = new ArrayList<>();
            }

            // check and remove if item is already selected.
            List<Object> tempItem = mSelectedItems;
            boolean isSelected = true;
            for (Object i : mSelectedItems) {
                if (i.equals(item)) {
                    tempItem.remove(i);
                    isSelected = false;
                    break;
                }
            }
            mSelectedItems = tempItem;
            if (isSelected) {
                mSelectedItems.add(item);
            }

            int clickedItemPosition = -1;

            // get the current clicked item position
            for (int i = 0; i < originalList.size(); i++) {
                Object objj = originalList.get(i);
                if (objj.equals(item)) {
                    clickedItemPosition = i;
                    break;
                }
            }
            StringBuilder builder = new StringBuilder();

            if (mSelectedItems != null && mSelectedItems.size() > 0) {

                builder.append(mSelectedItems.get(0));
                if (mSelectedItems.size() > 1) {
                    for (int i = 1; i < mSelectedItems.size(); i++) {
                        builder.append(", ").append(mSelectedItems.get(i));
                    }
                }

                setText(builder);
            } else {
                setText("");
            }
            if (withCallback && clickedItemPosition != -1 /*positions.size() > 0*/ && mOnItemSelectedListener != null) {
                mOnItemSelectedListener.onItemSelected(this, isSelected, clickedItemPosition);
            }
        }
    }

    private List<Integer> getSelectedItemsPosition() {
        List<Integer> positions = new ArrayList<>();

        for (int i = 0; i < originalList.size(); i++) {
            Object objj = originalList.get(i);

            for (int j = 0; j < mSelectedItems.size(); j++) {
                Object slectedObj = mSelectedItems.get(j);
                if (objj.equals(slectedObj)) {
                    positions.add(i);
                    break;
                }
            }
        }
        return positions;
    }

    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!isDisabled) {
                showPopup();
            }
        }
    };

    private void showPopup() {
        View view = View.inflate(getContext(), R.layout.popup_menu, null);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        itemsAdapter = new ItemsAdapter(originalList == null ? new ArrayList<>() : originalList, mOnItemSelectedListener);
        mRecyclerView.setAdapter(itemsAdapter);

        final EditText searchBox = (EditText) view.findViewById(R.id.search_field);
        if (showSearch) {
            searchBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    itemsAdapter.getFilter().filter(searchBox.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } else {
            searchBox.setVisibility(View.GONE);
        }

        final View clearSelection = view.findViewById(R.id.clear_selection);
        if (showClearSelection && mSelectedItems != null && mSelectedItems.size() > 0) {
            clearSelection.setVisibility(View.VISIBLE);
            clearSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectedItems == null) {

                        setText("");

                        clearSelection.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                popupWindow.dismiss();
                            }
                        }, POPUP_DISMISS_DELAY);

                        return;
                    }

                    mSelectedItems = null;
                    setText("");

                    clearSelection.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            popupWindow.dismiss();
                        }
                    }, POPUP_DISMISS_DELAY);

                    if (mOnItemSelectedListener != null) {
                        mOnItemSelectedListener.onSelectionCleared();
                    }

                }
            });
        } else {
            clearSelection.setVisibility(View.GONE);
        }

        popupWindow = new PopupWindow(
                view,
                (int) (this.getWidth() * SCALE_RATIO),
                POPUP_WINDOW_MAX_HEIGHT_DP);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setClippingEnabled(true);
        popupWindow.update();
        popupWindow.showAsDropDown(this, 0, 0);

    }

    public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> implements Filterable {

        List<Object> filteredList;
        OnItemSelectedListener menuItemClickListener;
        private Filter mFilter;

        public ItemsAdapter(List<Object> objects, OnItemSelectedListener listener) {
            this.filteredList = new ArrayList<>();
            this.filteredList.addAll(objects);

            this.menuItemClickListener = listener;
            this.mFilter = new ItemsAdapter.SearchFilter(this);
        }

        @Override
        public ItemsAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.multi_popup_menu_item, null);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new ItemsAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemsAdapter.ItemViewHolder holder, int position) {
            holder.mTextView.setText(filteredList.get(position).toString());

            if (mSelectedItems != null) {
                boolean isSelected = false;
                for (Object selectedItem : mSelectedItems) {
                    if (selectedItem.equals(filteredList.get(position))) {
                        isSelected = true;
                        break;
                    }
                }

                int[][] states = new int[][]{
                        new int[]{android.R.attr.state_enabled}, // enabled
                        new int[]{-android.R.attr.state_enabled}, // disabled
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_pressed}  // pressed
                };

                if (isSelected) {

                    if (ContextCompat.getColor(getContext(), R.color.colorAccent) != 0) {
                        holder.mTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

                        int colorValue = ContextCompat.getColor(getContext(), R.color.colorAccent);
                        int[] colors = new int[]{
                                colorValue,
                                colorValue,
                                colorValue,
                                colorValue
                        };

                        ColorStateList myList = new ColorStateList(states, colors);
                        holder.checkBox.setButtonTintList(myList);
                    } else {
                        holder.mTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));

                        int colorValue = ContextCompat.getColor(getContext(), R.color.colorPrimary);
                        int[] colors = new int[]{
                                colorValue,
                                colorValue,
                                colorValue,
                                colorValue
                        };

                        ColorStateList myList = new ColorStateList(states, colors);
                        holder.checkBox.setButtonTintList(myList);
                    }
                } else {
                    holder.mTextView.setTextColor(Color.BLACK);
                    int[] colors = new int[]{
                            Color.BLACK,
                            Color.BLACK,
                            Color.BLACK,
                            Color.BLACK
                    };

                    ColorStateList myList = new ColorStateList(states, colors);
                    holder.checkBox.setButtonTintList(myList);
                }
                holder.checkBox.setChecked(isSelected);
            }
        }

        @Override
        public int getItemCount() {
            return filteredList == null ? 0 : filteredList.size();
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        public void setList(List<Object> list) {
            this.filteredList = list;
        }

        public void addItem(Object obj) {
            if (this.filteredList == null) {
                this.filteredList = new ArrayList<>();
            }
            this.filteredList.add(obj);
            notifyDataSetChanged();
        }

        public void addItem(Object obj, int position) {
            if (this.filteredList == null) {
                this.filteredList = new ArrayList<>();
            }
            this.filteredList.add(position, obj);
            notifyDataSetChanged();
        }

        public List<Object> getList() {
            return this.filteredList;
        }

        public void clear() {
            if (this.filteredList != null) {
                this.filteredList.clear();
                notifyDataSetChanged();
            }
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {

            View parent;
            TextView mTextView;
            MaterialCheckBox checkBox;

            public ItemViewHolder(View itemView) {
                super(itemView);
                this.parent = itemView;
                mTextView = (TextView) this.parent.findViewById(R.id.popup_item_title);
                checkBox = (MaterialCheckBox) this.parent.findViewById(R.id.popup_item_checked);


                if (labelAlignment == LABEL_ALIGN_CENTER) {
                } else if (labelAlignment == LABEL_ALIGN_LEFT) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.leftMargin = Utils.dpToPx(getContext(), 5);
                    mTextView.setLayoutParams(params);
                } else {
                }

                this.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parent.performClick();
                        notifyDataSetChanged();
                    }
                });

                this.parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        onSelectionChange();
                        notifyDataSetChanged();
                    }
                });
            }

            private void onSelectionChange() {
                setSelection(originalList.indexOf(filteredList.get(getAdapterPosition())), true);
            }
        }

        public class SearchFilter extends Filter {

            ItemsAdapter mAdapter;

            public SearchFilter(ItemsAdapter adapter) {
                super();
                this.mAdapter = adapter;
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<Object> values = new ArrayList<>();
                FilterResults results = new FilterResults();
                if (constraint.length() == 0) {
                    values.addAll(originalList);
                } else {
                    for (final Object item : originalList) {
                        if (item.toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            values.add(item);
                        }
                    }
                }
                results.values = values;
                results.count = values.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mAdapter.setList((List<Object>) results.values);
                mAdapter.notifyDataSetChanged();
            }
        }

    }

    public void setPopupHeight(int height) {
        POPUP_WINDOW_MAX_HEIGHT_DP = height;
    }


    public int spToPx(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getContext().getResources().getDisplayMetrics());
    }

    public void setDisabled(boolean flag) {
        this.isDisabled = flag;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public List<Object> getItems() {
        return originalList;
    }

}
