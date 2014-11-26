package ru.nsu.sumaneev.android.diary;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class NoteEditor extends Activity implements OnClickListener {

	private static final String TAG = "diaryTag";
	
	private DiaryDatabase db;
	
	private EditText noteTitleEditor;
	private EditText noteBodyEditor;
	
	private Button submitButton;
	
	private Long rowId;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_editor);
		
		
		noteTitleEditor = (EditText) findViewById(R.id.noteEditorTitle);
		noteBodyEditor = (EditText) findViewById(R.id.noteEditorBody);
		
		submitButton = (Button) findViewById(R.id.noteModificationSubmitButton);
		submitButton.setOnClickListener(this);
		
		
		db = new DiaryDatabase(this);
		db.open();
		
		
		//	check if another Id was sent
		Bundle bundle = getIntent().getExtras();
		
		rowId = ( null == bundle ) ? null : bundle.getLong(DiaryDatabase.COLUMN_ID);
		
		fillFields();	
		
	}
	/*
	@Override
	protected void onDestroy() {
		
		db.close();
		
	}
	*/

	private void fillFields() {
		
		if (null != rowId) {
			
			Cursor c = db.fetchNote(rowId);
			
			noteTitleEditor.setText(c.getString(c.getColumnIndex(DiaryDatabase.COLUMN_TITLE)));
			noteBodyEditor.setText(c.getString(c.getColumnIndex(DiaryDatabase.COLUMN_NOTE)));
			
		}
		
	}
	
	private void saveState() {
		
		String title = noteTitleEditor.getText().toString();
		String body = noteBodyEditor.getText().toString();
		
		if (null == rowId) {
			//	create
			
			if (db.addNote(title, body) <= 0) {
				Log.d(TAG, "editor: can not create new note");
			}
			else {
				Log.d(TAG, "editor: created");
			}
			
		}
		else {
			//	update
			
			if (!db.updateNote(rowId, title, body)) {
				Log.d(TAG, "editor: can not update note");
			}
			else {
				Log.d(TAG, "editor: updated");
			}
			
		}
		
	}
	
	private void onFinish() {
		
		finish();
		
		this.overridePendingTransition(R.anim.note_editor_end_anim, R.anim.main_activity_start);
		
	}

	@Override
	public void onClick(View v) {
		
		Log.d(TAG, "editor: on click");
		
		saveState();
		
		Log.d(TAG, "editor: saved");
		
		onFinish();
		
	}
	
	@Override
	public void onBackPressed() {

		onFinish();
	}
	
	
	
}
