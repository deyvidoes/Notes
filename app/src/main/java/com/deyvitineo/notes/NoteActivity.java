package com.deyvitineo.notes;

import androidx.annotation.NonNull;
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

    private static final int EDIT_MODE_ENABLED = 1;
    private static final int EDIT_MODE_DISABLED = 0;


    private AddEditNoteViewModel mAddEditNoteViewModel;

    private String mTitle, mContent;
    private Long mID;
    private int mMode;


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

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("id")){
                mID = savedInstanceState.getLong("id");
            }
            mMode = savedInstanceState.getInt("mode");

            if(mMode == EDIT_MODE_ENABLED){
                mTitle = savedInstanceState.getString("editing_title");
            } else if(mMode == EDIT_MODE_DISABLED){
                mTitle = savedInstanceState.getString("title");
            }
        }

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

        Log.d(TAG, "initActivityFromIntent: MID = " + mID);
        if (mID != null) { //for newly created notes or notes loaded from intents after a config change
            if (mMode == EDIT_MODE_ENABLED) {
                enableEditMode();
                Log.d(TAG, "initActivityFromIntent: called 1: NEWLY CREATED NOTE OR LOADED AFTER CONFIG CHANGES");
            } else {
                if(intent.hasExtra(EXTRA_ID)){
                    mTitle = intent.getStringExtra(EXTRA_TITLE);
                } else{

                }
                enableViewMode();
                Log.d(TAG, "initActivityFromIntent: called 2: NEWLY CREATED NOTE OR LOADED AFTER CONFIG CHANGES");
            }
        } else if (mID == null && !intent.hasExtra(EXTRA_ID)) { //new note
            enableEditMode();
            Log.d(TAG, "initActivityFromIntent: called 3: NEW NOTE");
        } else if (intent.hasExtra(EXTRA_ID)) { //should only be called once for when the activity is loaded from intent. Might need boolean
            mTitle = intent.getStringExtra(EXTRA_TITLE);
            mContent = intent.getStringExtra(EXTRA_CONTENT);
            mID = intent.getLongExtra(EXTRA_ID, -1);
            enableViewMode();
            Log.d(TAG, "initActivityFromIntent: called 4: NOTE FROM INTENT. SHOULD ONLY BE CALLED ONCE");
        }
    }


    /**
     * Disables any content interaction with the edit text view for the description/content,
     * making it a "textview"
     */
    private void disableContentInteraction() {
        mEditTextContent.setKeyListener(null);
        mEditTextContent.setFocusable(false);
        mEditTextContent.setFocusableInTouchMode(false);
        mEditTextContent.setCursorVisible(false);
        mEditTextContent.clearFocus();
    }

    /**
     * Enables all content interaction with the edit text view for the description/content
     */
    private void enableContentInteraction() {
        mEditTextContent.setKeyListener(new EditText(this).getKeyListener());
        mEditTextContent.setFocusable(true);
        mEditTextContent.setFocusableInTouchMode(true);
        mEditTextContent.setCursorVisible(true);
        mEditTextContent.requestFocus();

    }

    /**
     * Enables view mode. Changes mMode to disable and disables edit mode.
     */
    private void enableViewMode() {
        disableEditMode();
        mViewTitle.setText(mTitle);
        mEditTextContent.setText(mContent);
    }

    /**
     * Enables edit mode and content interaction
     */
    private void enableEditMode() {
        mMode = EDIT_MODE_ENABLED;
        mEditTitle.setText(mTitle);
        enableContentInteraction();
        mBackArrowContainer.setVisibility(View.GONE);
        mCheckContainer.setVisibility(View.VISIBLE);
        mViewTitle.setVisibility(View.GONE);
        mEditTitle.setVisibility(View.VISIBLE);
    }

    /**
     * Disables edit mode and content interaction
     */
    private void disableEditMode() {
        disableContentInteraction();
        mMode = EDIT_MODE_DISABLED;
        mBackArrowContainer.setVisibility(View.VISIBLE);
        mCheckContainer.setVisibility(View.GONE);
        mViewTitle.setVisibility(View.VISIBLE);
        mEditTitle.setVisibility(View.GONE);
    }

    /**
     * Updates the view title text view. Identifies whether a new note needs to be saved
     * or an old one updated based on the "mID"
     */
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
            mID = mAddEditNoteViewModel.insert(note);
            Log.d(TAG, "saveNote: NEW NOTE CREATED WITH ID: " + mID);
            Toast.makeText(this, "New Note Created: " + mID, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if(mID != null){
            outState.putLong("id", mID);
        }
        outState.putInt("mode", mMode);
        if(mMode == EDIT_MODE_ENABLED){
            outState.putString("editing_title", mEditTitle.getText().toString());
        }
        if(mID != null && mMode == EDIT_MODE_DISABLED){
            outState.putString("title", mTitle);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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
                enableViewMode();
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

