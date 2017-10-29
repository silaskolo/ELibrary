/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.silaskolo.elibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryAdapterViewHolder> {

    private String[][] mCategoriesData;


    /*
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView
     */
    private final CategoryAdapterOnClickHandler mClickHandler;

    /**
     * The interface that receives onClick messages.
     */
    public interface CategoryAdapterOnClickHandler {
        void onClick(String[] category);
    }

    /**
     * Creates a CategoryAdapter.
     *
     * @param clickHandler The on-click handler for this adapter. This single handler is called
     *                     when an item is clicked.
     */
    public CategoryAdapter(CategoryAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Cache of the children views for a Category list item.
     */
    public class CategoryAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final TextView mCategoryTextView;
//        public final TextView mCategoryImageView;

        public CategoryAdapterViewHolder(View view) {
            super(view);
            mCategoryTextView = (TextView) view.findViewById(R.id.tv_category_data);
//            mCategoryImageView = (TextView) view.findViewById(R.id.recommended_category_data);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String[] category = mCategoriesData[adapterPosition];
            mClickHandler.onClick(category);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new CategoryAdapterViewHolder that holds the View for each list item
     */
    @Override
    public CategoryAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.category_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new CategoryAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the weather
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param CategoryAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(CategoryAdapterViewHolder CategoryAdapterViewHolder, int position) {
        String categoryName = mCategoriesData[position][1];
        CategoryAdapterViewHolder.mCategoryTextView.setText(categoryName);
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our Category
     */
    @Override
    public int getItemCount() {
        if (null == mCategoriesData) return 0;
        return mCategoriesData.length;
    }

    /**
     * This method is used to set the weather Category on a CategoryAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new CategoryAdapter to display it.
     *
     * @param categoryData The new weather data to be displayed.
     */
    public void setCategoryData(String[][] categoryData) {
        mCategoriesData = categoryData;
        notifyDataSetChanged();
    }
}