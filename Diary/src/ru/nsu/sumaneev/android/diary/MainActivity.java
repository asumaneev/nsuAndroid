package ru.nsu.sumaneev.android.diary;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends ListActivity implements OnClickListener {

	
	//private static final String TAG = "diaryTag";
	
	private static final int ACTIVITY_CREATE = 1;
	private static final int ACTIVITY_MODIFY = 2;
	
	
	//	context menu's id
	//private static final int CM_MODIFY_ID = 0;
	private static final int CM_DELETE_ID = 1;
	

	private EditText searchEditText;
	private Button searctButton;
	
	private DiaryDatabase db;
	
	//private Cursor cursor;
	//private SimpleCursorAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		searchEditText = (EditText) findViewById(R.id.searchEditText);
		searctButton = (Button) findViewById(R.id.searchSubmitButton);
		
		searctButton.setOnClickListener(this);
		
		db = new DiaryDatabase(this);
		
		db.open();
		
		fillData();
		
		registerForContextMenu(getListView());
		
	}

	@SuppressWarnings("deprecation")
	private void fillData() {
		
		Cursor cursor = db.fetchAllNotes();
		startManagingCursor(cursor);
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				this, 
				R.layout.list_row, 
				cursor, 
				new String[]{DiaryDatabase.COLUMN_TITLE}, 
				new int[]{R.id.titleTextView}, 
				0);
		
		setListAdapter(adapter);
	}
	
	@SuppressWarnings("deprecation")
	private void fillData(String pattern) {

		if (pattern.isEmpty()) {
			return;
		}
		
		Cursor cursor = db.fetchNote(pattern);
		startManagingCursor(cursor);
		
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				this, 
				R.layout.list_row, 
				cursor, 
				new String[]{DiaryDatabase.COLUMN_TITLE}, 
				new int[]{R.id.titleTextView}, 
				0);
		
		setListAdapter(adapter);
		
	}
	
	private void changeOnNoteEditor() {
		
		this.overridePendingTransition(R.anim.main_activity_end, R.anim.note_editor_start_anim);
		
	}
	
	
	/*
	 * *******************************************************************
	 * 
	 *				START OPTIONS MENU METHODS
	 * 
	 * *******************************************************************
	 */
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
		case R.id.menuNewNote:
			
			Intent intent = new Intent(this, NoteEditor.class);
			
			startActivityForResult(intent, ACTIVITY_CREATE);
			
			changeOnNoteEditor();
			
			return true;
		
		case R.id.menuExit:
			
			finish();
		
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/*
	 * *******************************************************************
	 * 
	 *				END OPTIONS MENU METHODS
	 * 
	 * *******************************************************************
	 */
	
	/*
	 * *******************************************************************
	 * 
	 *				START CONTEXT MENU METHODS
	 * 
	 * *******************************************************************
	 */
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		
		//menu.add(1, CM_MODIFY_ID, 10, R.string.modify_note_title);
		menu.add(1, CM_DELETE_ID, 20, R.string.delete_title);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		
		switch (item.getItemId()) {

		case CM_DELETE_ID:
			
			db.deleteNote(info.id);
			
			fillData();
			
			return true;
		}
		
		return super.onContextItemSelected(item);
	}
	
	/*
	 * *******************************************************************
	 * 
	 *				END CONTEXT MENU METHODS
	 * 
	 * *******************************************************************
	 */
	
	/*
	 * *******************************************************************
	 * 
	 *				START LIST ITEM METHODS
	 * 
	 * *******************************************************************
	 */
	
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
 
		super.onListItemClick(l, v, position, id);		
		
		Intent intent = new Intent(this, NoteEditor.class);
		
		intent.putExtra(DiaryDatabase.COLUMN_ID, id);
		
		startActivityForResult(intent, ACTIVITY_MODIFY);
		
		changeOnNoteEditor();
		
	}
	
	 
	/*
	 * *******************************************************************
	 * 
	 *				END LIST ITEM METHODS
	 * 
	 * *******************************************************************
	 */
	 
	/*
	 * *******************************************************************
	 * 
	 *				START ON CLICK METHODS
	 * 
	 * *******************************************************************
	 */
	
 
	@Override
	public void onClick(View v) {
	
		
		switch (v.getId()) {
		
		case R.id.searchSubmitButton:
			
			
			String pattern = searchEditText.getText().toString();
			
			fillData(pattern);
			
			break;
		
		}
		
	}

	
	/*
	 * *******************************************************************
	 * 
	 *				END ON CLICK METHODS
	 * 
	 * *******************************************************************
	 */
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		super.onActivityResult(requestCode, resultCode, intent);;
		
		fillData();

	}
	
 
}
