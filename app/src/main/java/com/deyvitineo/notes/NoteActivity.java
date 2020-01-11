package com.deyvitineo.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.deyvitineo.notes.entities.Note;
import com.deyvitineo.notes.util.Utility;
import com.deyvitineo.notes.viewmodels.AddEditNoteViewModel;

public class NoteActivity extends AppCompatActivity implements View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        View.OnClickListener {
    private static final String TAG = "NoteActivity";

    public static final String EXTRA_TITLE = "com.deyvitineo.notes.EXTRA_TITLE";
    public static final String EXTRA_CONTENT = "com.deyvitineo.notes.EXTRA_CONTENT";
    public static final String EXTRA_ID = "com.deyvitineo.notes.EXTRA_ID";

    private AddEditNoteViewModel mAddEditNoteViewModel;

    private String mTitle, mContent;
    private Long mID;


    //UI Components
    private EditText mEditTextContent;
    private EditText mEditTitle;
    private TextView mViewTitle;
    private RelativeLayout mCheckContainer, mBackArrowContainer;
    private ImageButton mCheck, mBackArrow;

    //vars
    private GestureDetector mGestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_edit_add_note);

        setupWidgets();
        setListeners();
        initActivityFromIntent();
    }

    private void setupWidgets() {
        mEditTextContent = findViewById(R.id.note_content);
        mEditTitle = findViewById(R.id.note_edit_title);
        mViewTitle = findViewById(R.id.note_view_title);
        mCheckContainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
        mCheck = findViewById(R.id.toolbar_check);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);

        mAddEditNoteViewModel = ViewModelProviders.of(this).get(AddEditNoteViewModel.class);
    }

    private void setListeners() {
        mEditTextContent.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(this, this);
        mViewTitle.setOnClickListener(this);
        mCheck.setOnClickListener(this);
        mBackArrow.setOnClickListener(this);
    }

    private void initActivityFromIntent() {
        Intent intent = getIntent();

        if (!intent.hasExtra(EXTRA_ID)) {
            enableNewNote();
        } else if (intent.hasExtra(EXTRA_ID)) {
            mTitle = intent.getStringExtra(EXTRA_TITLE);
            mContent = intent.getStringExtra(EXTRA_CONTENT);
            mID = intent.getLongExtra(EXTRA_ID, -1);
            enableViewMode();
        }
    }

    private void disableContentInteraction() {
        mEditTextContent.setKeyListener(null);
        mEditTextContent.setFocusable(false);
        mEditTextContent.setFocusableInTouchMode(false);
        mEditTextContent.setCursorVisible(false);
        mEditTextContent.clearFocus();
    }

    private void enableContentInteraction() {
        mEditTextContent.setKeyListener(new EditText(this).getKeyListener());
        mEditTextContent.setFocusable(true);
        mEditTextContent.setFocusableInTouchMode(true);
        mEditTextContent.setCursorVisible(true);
        mEditTextContent.requestFocus();

    }

    private void enableNewNote() {
        enableEditMode();
    }

    private void enableViewMode() {
        disableEditMode();
        mViewTitle.setText(mTitle);
        mEditTextContent.setText(mContent);
    }

    private void enableEditMode() {
        mEditTitle.setText(mViewTitle.getText().toString());
        enableContentInteraction();
        mBackArrowContainer.setVisibility(View.GONE);
        mCheckContainer.setVisibility(View.VISIBLE);
        mViewTitle.setVisibility(View.GONE);
        mEditTitle.setVisibility(View.VISIBLE);
    }

    private void disableEditMode() {
        disableContentInteraction();
        mBackArrowContainer.setVisibility(View.VISIBLE);
        mCheckContainer.setVisibility(View.GONE);
        mViewTitle.setVisibility(View.VISIBLE);
        mEditTitle.setVisibility(View.GONE);
    }

    private void saveNote() {

        mViewTitle.setText(mEditTitle.getText().toString());
        String timeStamp = Utility.getCurrentTimestamp();
        timeStamp = timeStamp.replace("-", " ");
        Note note = new Note(mTitle, mContent, timeStamp);

        Log.d(TAG, "saveNote: MID value is: " + mID);
        //updates existing note. Next Statement adds new note
        if (mID != null) {
            note.setId(mID);
            mAddEditNoteViewModel.update(note);
            Log.d(TAG, "saveNote: ID = :  " + mID + "/nTitle: " + mTitle + "/nContent: " + mContent);
            Toast.makeText(this, "Note Updated with ID: " + mID, Toast.LENGTH_SHORT).show();

        } else {
            mID =  mAddEditNoteViewModel.insert(note);
            Log.d(TAG, "saveNote: NEW NOTE CREATED WITH ID: " + mID);
            Toast.makeText(this, "New Note Created: " + mID, Toast.LENGTH_SHORT).show();
        }
    }

    //hides keyboard
    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.toolbar_check:
                mTitle = mEditTitle.getText().toString();
                mContent = mEditTextContent.getText().toString();

                //Requires notes to have both title and description
                if (mTitle.trim().isEmpty() || mContent.trim().isEmpty()) {
                    Toast.makeText(this, "Please add a title and description", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveNote();
                hideSoftKeyboard();
                disableEditMode();
                break;
            case R.id.note_view_title:
                enableEditMode();
                mEditTitle.requestFocus();
                mEditTitle.setSelection(mEditTitle.length());
                showKeyboard();
                break;
            case R.id.toolbar_back_arrow:
                finish(); //calls on destroy method
                break;
        }
    }

    //    Method for implementing touch listeners
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(TAG, "onDoubleTap: Double Tapped!");
        enableEditMode();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    //shows keyboard
    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditTitle, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


//TODO: Bugs: double insert when turning device sideways, editing note right after insert
