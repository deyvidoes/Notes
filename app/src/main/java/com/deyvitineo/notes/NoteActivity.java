package com.deyvitineo.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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

import com.deyvitineo.notes.models.Note;
import com.deyvitineo.notes.persistance.NoteRepository;
import com.deyvitineo.notes.util.Utility;

public class NoteActivity extends AppCompatActivity implements View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        View.OnClickListener {
    private static final String TAG = "NoteActivity";

    private static final int EDIT_MODE_ENABLED = 1;
    private static final int EDIT_MODE_DISABLED = 0;
    private static final String DEFAULT_TITLE = "New Note";


    //UI Components
    private LinedEditText mLinedEditText;
    private EditText mEditTitle;
    private TextView mViewTitle;
    private RelativeLayout mCheckContainer, mBackArrowContainer;
    private ImageButton mCheck, mBackArrow;

    //vars
    private boolean mIsNewNote;
    private Note mInitialNote;
    private GestureDetector mGestureDetector;
    private int mMode;
    private NoteRepository mNoteRepository;
    private Note mFinalNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mNoteRepository = new NoteRepository(this);
        setupWidgets();

        if(getIncomingIntent()){
            //Working on a new note (EDIT MODE)
            setNewNoteProperties();
            enableEditMode();

        } else{
            //Working on an old note (VIEW MODE)
            setNoteProperties();
            disableContentInteraction();
        }

        setListeners();

    }

    private void setupWidgets(){
        mLinedEditText = findViewById(R.id.note_content);
        mEditTitle = findViewById(R.id.note_edit_title);
        mViewTitle = findViewById(R.id.note_view_title);
        mCheckContainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
        mCheck = findViewById(R.id.toolbar_check);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);

    }

    private void setListeners(){
        mLinedEditText.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(this, this);
        mViewTitle.setOnClickListener(this);
        mCheck.setOnClickListener(this);
        mBackArrow.setOnClickListener(this);
    }

    private boolean getIncomingIntent(){
        if(getIntent().hasExtra("selected_note")){
            mInitialNote = getIntent().getParcelableExtra("selected_note");

            mFinalNote = new Note();
            mFinalNote.setTitle(mInitialNote.getTitle());
            mFinalNote.setContent(mInitialNote.getContent());
            mFinalNote.setTimestamp(mInitialNote.getTimestamp());
            mFinalNote.setId(mInitialNote.getId());

            mMode = EDIT_MODE_DISABLED;
            mIsNewNote = false;
            return false;
        }
        mMode = EDIT_MODE_ENABLED;
        mIsNewNote = true;
        return true;
    }

    private void saveChanges(){
        if(mIsNewNote){
            saveNewNote();
        } else{
            updateNote();
        }
    }

    private void saveNewNote(){
        mNoteRepository.insertNoteTask(mFinalNote);
    }

    private void updateNote(){
        mNoteRepository.updateNote(mFinalNote);
    }

    private void disableContentInteraction(){
        mLinedEditText.setKeyListener(null);
        mLinedEditText.setFocusable(false);
        mLinedEditText.setFocusableInTouchMode(false);
        mLinedEditText.setCursorVisible(false);
        mLinedEditText.clearFocus();
    }

    private void enableContentInteraction(){
        mLinedEditText.setKeyListener(new EditText(this).getKeyListener());
        mLinedEditText.setFocusable(true);
        mLinedEditText.setFocusableInTouchMode(true);
        mLinedEditText.setCursorVisible(true);
        mLinedEditText.requestFocus();
    }

    private void enableEditMode(){
        mBackArrowContainer.setVisibility(View.GONE);
        mCheckContainer.setVisibility(View.VISIBLE);
        mViewTitle.setVisibility(View.GONE);
        mEditTitle.setVisibility(View.VISIBLE);

        mMode = EDIT_MODE_ENABLED;
        enableContentInteraction();
    }

    private void disableEditMode(){
        mBackArrowContainer.setVisibility(View.VISIBLE);
        mCheckContainer.setVisibility(View.GONE);
        mViewTitle.setVisibility(View.VISIBLE);
        mEditTitle.setVisibility(View.GONE);

        mMode = EDIT_MODE_DISABLED;
        disableContentInteraction();

        String temp = mLinedEditText.getText().toString();
        temp = temp.replace("\n", "");
        temp = temp.replace(" ", "");

        if(temp.length() > 0){
            mFinalNote.setTitle(mEditTitle.getText().toString());
            mFinalNote.setContent(mLinedEditText.getText().toString());
            String timeStamp = Utility.getCurrentTimestamp();
            timeStamp = timeStamp.replace("-", " ");
            mFinalNote.setTimestamp(timeStamp);

            if(!mFinalNote.getContent().equals(mInitialNote.getContent()) ||
                    !mFinalNote.getTitle().equals(mInitialNote.getTitle())){
                saveChanges();
                mViewTitle.setText(mFinalNote.getTitle());
            }
        } else{
            Toast.makeText(this, "Cannot create a note without content.", Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private void hideSoftKeyboard(){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if(view != null){
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setNoteProperties(){
        mViewTitle.setText(mInitialNote.getTitle());
        mEditTitle.setText(mInitialNote.getTitle());
        mLinedEditText.setText(mInitialNote.getContent());

    }

    private void setNewNoteProperties(){
        mViewTitle.setText(DEFAULT_TITLE);
        mEditTitle.setText(DEFAULT_TITLE);

        mInitialNote = new Note();
        mFinalNote = new Note();
        mInitialNote.setTitle(DEFAULT_TITLE);
        mFinalNote.setTitle(DEFAULT_TITLE);
    }

    //Method for implementing touch listeners
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.toolbar_check:
                hideSoftKeyboard();
                disableEditMode();
                break;
            case R.id.note_view_title:
                enableEditMode();
                mEditTitle.requestFocus();
                mEditTitle.setSelection(mEditTitle.length());
                mEditTitleShowKeyboard();
                break;
            case R.id.toolbar_back_arrow:
                finish(); //calls on destroy method
                break;
        }
    }

    private void mEditTitleShowKeyboard(){
        InputMethodManager imm =  (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditTitle, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onBackPressed() {
        if(mMode == EDIT_MODE_ENABLED){
            onClick(mCheck);
        } else{
            super.onBackPressed();
        }
    }

    //Didn't seem to work/do anything.
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt("mode", mMode);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        mMode = savedInstanceState.getInt("mode");
//
//        if(mMode == EDIT_MODE_ENABLED){
//            enableEditMode();
//        }
//    }

    //Allows edit mode to still be enabled if it was when the user walked away
    @Override
    protected void onResume() {
        super.onResume();
        if(mMode == EDIT_MODE_ENABLED){
            enableEditMode();
        }
    }

    /* Implements auto save feature. New note is set to false to avoid
     * duplicates of new notes.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if(mMode == EDIT_MODE_ENABLED){
            disableEditMode();
            mIsNewNote = false;
            mMode = EDIT_MODE_ENABLED;
        }
    }
}
