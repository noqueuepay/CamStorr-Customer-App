package com.anvay.cctvcustomer.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.anvay.cctvcustomer.R;
import com.google.android.material.imageview.ShapeableImageView;

public class CardOptionView extends CardView {
    private TextView cardText;
    private ShapeableImageView cardImage;

    public CardOptionView(@NonNull Context context) {
        super(context);
    }

    public CardOptionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CardOptionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        if (context == null || attrs == null)
            return;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardOptionView, 0, 0);
        String text = a.getString(R.styleable.CardOptionView_cardText);
        Drawable drawable = a.getDrawable(R.styleable.CardOptionView_imageSrc);
        a.recycle();
        inflate(getContext(), R.layout.layout_card_options, this);
        cardText = findViewById(R.id.card_text);
        cardImage = findViewById(R.id.card_image);
        cardText.setText(text);
        cardImage.setImageDrawable(drawable);
    }
}
