package com.duckduckgo.mobile.android.views;

import com.duckduckgo.mobile.android.download.Holder;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.tasks.CacheTask;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.AdapterView;

public class MainFeedListView extends ListView implements android.widget.AdapterView.OnItemClickListener, android.widget.AdapterView.OnItemLongClickListener {

	private OnMainFeedItemSelectedListener listener;
	private OnMainFeedItemLongClickListener longClickListener;
	
	private boolean isAfterRenderRun = false;
	private CacheTask afterRenderTask = null;
	
	public MainFeedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.setOnItemClickListener(this);
		this.setOnItemLongClickListener(this);
	}
	
	public void setOnMainFeedItemSelectedListener(OnMainFeedItemSelectedListener listener) {
		this.listener = listener;
	}
	
	public void setOnMainFeedItemLongClickListener(OnMainFeedItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
	}
	
	/**
	 * A task (Runnable) to run after at least a view is rendered can be set
	 * @param task
	 */
	public void setAfterRenderTask(CacheTask task) {
		afterRenderTask = task;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		FeedObject obj = (FeedObject) getAdapter().getItem(position);
		
		if (obj != null) {
			if (listener != null) {
				listener.onMainFeedItemSelected(obj);
			}
		}
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		FeedObject obj = (FeedObject) getAdapter().getItem(position);

		if (obj != null) {
			if (longClickListener != null) {
				longClickListener.onMainFeedItemLongClick(obj);
			}
		}
		return true;
	}

	public interface OnMainFeedItemSelectedListener {
		public void onMainFeedItemSelected(FeedObject feedObject);
	}

	public interface OnMainFeedItemLongClickListener {
		public void onMainFeedItemLongClick(FeedObject feedObject);
	}

	public void setSelectionById(String id) {
		int itemCount = getCount();
		for(int i=0; i < itemCount ; ++i) {
			if(((FeedObject) getItemAtPosition(i)).getId().equals(id)) {
				setSelection(i);
				break;
			}
		}
	}
	
	/**
	 * Find the item given by its ID and return its position in the list
	 * @param id item ID
	 * @return item position in the list
	 */
	public int getSelectionPosById(String id) {
		int itemCount = getCount();
		for(int i=0; i < itemCount ; ++i) {
			if(((FeedObject) getItemAtPosition(i)).getId().equals(id)) {
				return i;
			}
		}
		
		return -1;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
				
		if(afterRenderTask != null && !isAfterRenderRun) {
			afterRenderTask.run();
			isAfterRenderRun = true;
		}
	}
	
	public void enableAfterRender() {
		isAfterRenderRun = false;
	}
}
