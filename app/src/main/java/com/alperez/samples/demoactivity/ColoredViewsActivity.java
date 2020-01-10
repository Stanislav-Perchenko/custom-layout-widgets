package com.alperez.samples.demoactivity;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alperez.samples.R;
import com.alperez.samples.utils.UserRole;
import com.alperez.utils.ViewUtils;
import com.alperez.widget.customlayout.PrepassListItemColorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by stanislav.perchenko on 1/10/2020, 4:13 PM.
 */
public class ColoredViewsActivity extends BaseDemoActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_colored_views;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 21) {
            Toast.makeText(this, "This demo requires at least API 21 (LOLLIPOP)", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupList(R.id.recycler_1, R.layout.colored_view_item_1, getDemoData(48));
        setupList(R.id.recycler_2, R.layout.colored_view_item_2, getDemoData(24));
    }

    private void setupList(@IdRes int recyclerResId, @LayoutRes int listItemLayout, List<IPrepassListItemView> demoData) {
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(RecyclerView.HORIZONTAL);
        RecyclerView rv = findViewById(recyclerResId);
        rv.setLayoutManager(llm);
        rv.setAdapter(new ItemsAdapter(listItemLayout, demoData));
    }

    private List<IPrepassListItemView> getDemoData(int nItems) {
        List<IPrepassListItemView> dataItems = new ArrayList<>(nItems);
        char[] LETTERS = new char[]{'A', 'D', 'E', 'G', 'I', 'J', 'N', 'N', 'P', 'R', 'T'};
        final Random rnd = new Random();

        int ind1, ind2;
        final int nRolse = UserRole.values().length;
        for (int i=0; i<nItems; i++) {
            ind1 = rnd.nextInt(LETTERS.length);
            ind2 = rnd.nextInt(LETTERS.length);
            String txt = new String(new char[]{LETTERS[ind1], LETTERS[ind2]});
            dataItems.add(buildItem(txt, UserRole.values()[rnd.nextInt(nRolse)], UserRole.values()[rnd.nextInt(nRolse)], rnd.nextBoolean()));
        }
        return dataItems;
    }

    private IPrepassListItemView buildItem(final String text, UserRole r1, UserRole r2, final boolean isActive) {

        final UserRole[] roles = new UserRole[]{r1, r2};
        return new IPrepassListItemView() {
            @Override
            public String getAcronym() {
                return text;
            }

            @Override
            public int getUsersCount() {
                return 2;
            }

            @Override
            public UserRole getUserRole(int index) {
                return roles[index];
            }

            @Override
            public boolean isActive() {
                return isActive;
            }
        };
    }



    /**********************************************************************************************/
    /**********************************************************************************************/
    interface IPrepassListItemView {
        String getAcronym();
        UserRole getUserRole(int index);
        int getUsersCount();
        boolean isActive();
    }

    class VHolder extends RecyclerView.ViewHolder {
        final PrepassListItemColorView vColor;
        final TextView vText;
        final View vDot;

        int position;
        IPrepassListItemView prepass;

        private final int colorActive, colorInactive;

        public VHolder(@NonNull View itemView) {
            super(itemView);
            vColor = itemView.findViewById(R.id.color_view);
            vText = itemView.findViewById(R.id.txt_acron);
            vDot  = itemView.findViewById(R.id.dot_view);
            colorActive = ViewUtils.getColorFromResourcesCompat(getResources(), R.color.text_dark, null);
            colorInactive = ViewUtils.getColorFromResourcesCompat(getResources(), R.color.text_light_gray, null);
        }

        void bindData(int position, IPrepassListItemView prepass) {
            this.position = position;
            this.prepass  = prepass;
            vText.setText(prepass.getAcronym());
            vText.setTextColor(prepass.isActive() ? colorActive : colorInactive);
            vDot.setVisibility(prepass.isActive() ? View.VISIBLE : View.GONE);
            vColor.setColors(prepass.getUserRole(0).getColor(), prepass.getUserRole(1).getColor());
            vDot.getBackground().setLevel(prepass.isActive() ? 1 : 0);
        }
    }

    class ItemsAdapter extends RecyclerView.Adapter<VHolder> {
        private final LayoutInflater inflater;
        private final List<IPrepassListItemView> mItems;
        @LayoutRes
        private final int itemLayoutResId;

        public ItemsAdapter(@LayoutRes int itemLayoutResId, List<IPrepassListItemView> items) {
            inflater = getLayoutInflater();
            this.itemLayoutResId = itemLayoutResId;
            mItems = new ArrayList<>((items.size() > 0) ? items.size() : 24);
            mItems.addAll(items);
        }

        @NonNull
        @Override
        public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VHolder(inflater.inflate(itemLayoutResId, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VHolder holder, int position) {
            holder.bindData(position, mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

}
