package com.takeiteasy.materialexpansionpanel.panel;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.takeiteasy.materialexpansionpanel.R;

/**
 * Created by VLogachev on 19.10.2016.
 */

public class MaterialExpansionPanelView extends CardView implements ExpansionPanelInterface, View.OnClickListener {
    private static final NestedScrollView.LayoutParams LAYOUT_PARAMS
            = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    public static final int ROTATE_ANIM_DURATION = 200;

    private Interpolator interpolator = new LinearInterpolator();
    private ExpansionPanelInterface.OnClickListener listener;
    private TextView tvTitle;
    private TextView tvSubTitle;
    private NestedScrollView flContentContainer;
    private View vExpandableContent;
    private View vArrow;
    private View pbProgress;
    private Button btnNegative;
    private Button btnPositive;
    private boolean isExpansionEnabled;

    public MaterialExpansionPanelView(Context context) {
        super(context);
        init(context);
    }

    public MaterialExpansionPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MaterialExpansionPanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void init(Context context) {
        addView(inflate(context, R.layout.view_expansion_panel, null));

        setDefaultAttrs();
        initViews();
        setOnClickListeners();

        defaultState();
    }
    private void defaultState() {
        displayProgress(false);
        enableExpansion(true);
    }

    private void setDefaultAttrs() {
        setRadius(0);
        setUseCompatPadding(false);
        setPreventCornerOverlap(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setLayoutTransition(new LayoutTransition());
            getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }
    }

    private void initViews() {
        tvTitle = (TextView) findViewById(R.id.mep_title);
        tvSubTitle = (TextView) findViewById(R.id.mep_subtitle);
        flContentContainer = (NestedScrollView) findViewById(R.id.mep_content_container);
        vExpandableContent = findViewById(R.id.mep_expandable_content);
        vArrow = findViewById(R.id.mep_expand_button);
        pbProgress = findViewById(R.id.mep_progress);
        btnNegative = (Button) findViewById(R.id.panel_button_negative);
        btnPositive = (Button) findViewById(R.id.panel_button_positive);
    }

    private void setOnClickListeners() {
        btnNegative.setOnClickListener(this);
        btnPositive.setOnClickListener(this);
        findViewById(R.id.mep_header_container).setOnClickListener(this);
    }

    private void hideUnnecessaryViews() {
        tvTitle.setVisibility(INVISIBLE);
        tvSubTitle.setVisibility(GONE);
        btnNegative.setVisibility(GONE);
        btnPositive.setVisibility(GONE);
    }

    private void forceCollapse() {
        boolean wasEnabled = isEnabled();
        setEnabled(true);
        collapse();
        setEnabled(wasEnabled);
    }

    public void displayProgress(boolean display) {
        if (display) {
            pbProgress.setVisibility(VISIBLE);
            setEnabled(false);
        } else {
            setEnabled(true);
            pbProgress.setVisibility(INVISIBLE);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        btnNegative.setEnabled(enabled);
        btnPositive.setEnabled(enabled);
        findViewById(R.id.mep_header_container).setEnabled(enabled);
        findViewById(R.id.mep_header_container).setClickable(enabled);
    }

    @Override
    public void onClick(View view) {
        if (!isEnabled()) return;

        int i = view.getId();
        if (i == R.id.panel_button_negative) {
            collapse();

            if (listenerSet())
                listener.onNegativeButtonClicked();

        } else if (i == R.id.panel_button_positive) {
            collapse();

            if (listenerSet())
                listener.onPositiveButtonClicked();

        } else {
            if (isExpansionEnabled)
                handleLabelClick();
        }
    }

    private void handleLabelClick() {
        if (vExpandableContent.isShown()) {
            collapse();
        } else {
            expand();
        }
    }

    private boolean listenerSet() {
        return listener != null;
    }

    @Override
    public void expand() {
        if (!isEnabled()) return;

        addVerticalMargins();
        rotateArrow(true);
        vExpandableContent.setVisibility(VISIBLE);
    }

    @Override
    public void collapse() {
        if (!isEnabled()) return;
        removeVerticalMargins();
        rotateArrow(false);
        vExpandableContent.setVisibility(GONE);
    }

    private void addVerticalMargins() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) getLayoutParams();
        lp.topMargin = 20;
        lp.bottomMargin = 20;
    }

    private void removeVerticalMargins() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) getLayoutParams();
        lp.topMargin = 0;
        lp.bottomMargin = 0;
    }

    private void rotateArrow(boolean forward) {
        ViewCompat.animate(vArrow)
                .rotationX((forward) ? 180 : 0)
                .withLayer()
                .setDuration(ROTATE_ANIM_DURATION)
                .setInterpolator(interpolator)
                .start();
    }

    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
        tvTitle.setVisibility(VISIBLE);
    }

    public void setTitle(int res) {
        tvTitle.setText(res);
        tvTitle.setVisibility(VISIBLE);
    }

    public void setSubtitle(CharSequence subtitle) {
        tvSubTitle.setText(subtitle);
        tvSubTitle.setVisibility(VISIBLE);
    }

    public void setSubtitle(int res) {
        tvSubTitle.setText(res);
        tvSubTitle.setVisibility(VISIBLE);
    }

    public void setContentView(View view) {
        setContentView(view, null);
    }

    public void setContentView(View view, NestedScrollView.LayoutParams layoutParams) {
        flContentContainer.removeAllViews();
        flContentContainer.addView(
                view,
                (layoutParams == null) ?
                        LAYOUT_PARAMS
                        : layoutParams
        );
    }

    public void enableExpansion(boolean enable) {
        isExpansionEnabled = enable;
    }

    public void setButtonsClickListener(ExpansionPanelInterface.OnClickListener listener) {
        this.listener = listener;
        btnPositive.setVisibility(VISIBLE);
        btnNegative.setVisibility(VISIBLE);
    }

    public void setNegativeButton(CharSequence name) {
        btnNegative.setText(name);
        btnNegative.setVisibility(VISIBLE);
    }

    public void setPositiveButton(CharSequence name) {
        btnPositive.setText(name);
        btnPositive.setVisibility(VISIBLE);
    }

    public void setNegativeButton(int res) {
        btnNegative.setText(res);
        btnNegative.setVisibility(VISIBLE);
    }

    public void setPositiveButton(int res) {
        btnPositive.setText(res);
        btnPositive.setVisibility(VISIBLE);
    }

    public void hideSubTitle() {
        tvSubTitle.setVisibility(GONE);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }
}
