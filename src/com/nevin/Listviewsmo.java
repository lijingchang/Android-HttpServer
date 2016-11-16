package com.nevin;

import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Listviewsmo extends ListView implements OnKeyListener {

	private int itemsCount;

	private int itemHeight;

	private ListAdapter adapter;

	private int scrollDuration = 1000;

	private boolean isScrollTop;
	
    private Timer timer;

	private OnScrollBottomListener onScrollBottomListener;

	private OnScrollTopListener onScrollTopListener;
	

	public Listviewsmo(Context context) {
		this(context, null);
	}

	public Listviewsmo(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnKeyListener(this);
		this.setSmoothScrollbarEnabled(true);
		
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (adapter != null) {
			//��ȡÿ��item �ĸ߶ȣ���ΪҪ���û����ķ�����ÿ�λ����ľ������item �ĸ߶�
			itemHeight = this.getChildAt(0).getHeight();
		}

	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		this.adapter = adapter;
		//��ȡlistview  item��count��һ��Ҫ����adapter��ã�����ͨ��listView����ΪlistView�Ƕ�̬���ɾ�����ӵģ����Դ�ӡһ�±ȽϿ���
		itemsCount = adapter.getCount();
		
	}

	/**
	 * ���ù��������Ĺ���ʱ��
	 * 
	 * @param scrollDutation
	 */
	public void setScrollDuration(int scrollDutation) {
		this.scrollDuration = scrollDutation;

	}
	

	@SuppressLint("NewApi")
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP) {
			return false;
		}
		//��ȡ��ǰ��ѡ�е�״̬
		int currentItemPosition = this.getSelectedItemPosition();

		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {

			//��ʱ�����ڶ�����ʱ��
			if (currentItemPosition == itemsCount - 2) {
				//���listView�����һ���ɼ�Item�ǵ����ڶ���item�������ǵ�����һ��itemͬʱtimer��Ϊ�գ���ʱҪ����һ�Σ��������һ��item��ȡ����
				if(this.getLastVisiblePosition() == itemsCount - 2 || (this.getLastVisiblePosition() == itemsCount - 1 && timer != null)){
				
					this.smoothScrollBy(itemHeight, scrollDuration);
					
					if(timer == null){
						smoothScrollToBottom();
					}else{
						timer.cancel();
						timer = null;
						//�ӳ�һ�£��������һ��item���selected״̬������û�ж�����̫ͻأ
						smoothScrollToBottom();
					}
					
//					this.smoothScrollToPositionFromTop(itemsCount - 1, 0, scrollDuration);
//					this.setSelection(itemsCount - 1);
				}
				return false;
			} else if (currentItemPosition == itemsCount - 1) {
				//�������һ��item��selectionItem��������ص���������Ҫ�ڹ�����
				if (onScrollBottomListener != null) {
					onScrollBottomListener.onScrollBottom();
				}
				return true;
			} else {
				//���м�����״̬��ʱ�򣬹���һ��item�ľ���
				this.smoothScrollBy(itemHeight, scrollDuration);
				return false;
			}
		}
		
		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			if (currentItemPosition == 1) {
				
				if(this.getChildAt(0).isFocusable() == false){
					this.smoothScrollBy(-itemHeight, scrollDuration);
				}
				return false;
			} else if (currentItemPosition == 0) {
				
				if (onScrollTopListener != null) {
					onScrollTopListener.onScrollTop();
				}
				
				return isScrollTop;
			} else {
				this.smoothScrollBy(-itemHeight, scrollDuration);
				return false;
			}
		}

		return false;
	}
	

	private void smoothScrollToBottom(){
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				Listviewsmo.this.post(new Runnable() {
					
					@Override
					public void run() {
						Listviewsmo.this.setSelection(Listviewsmo.this.getLastVisiblePosition());
						
					}
				});
				
			}
		}, scrollDuration / 3);
	}


	/**
	 * ���������ײ���ʱ��ļ���
	 */
	public interface OnScrollBottomListener {
		void onScrollBottom();
	}

	public void setOnScrollBottomListener(
			OnScrollBottomListener onScrollBottomListener) {
		
		this.onScrollBottomListener = onScrollBottomListener;
	}

	/**
	 * ��������������ʱ��ļ���
	 */
	public interface OnScrollTopListener {
		void onScrollTop();
	}

	public void setOnScrollTopListener(OnScrollTopListener onScrollTopListener) {
		isScrollTop = true;
		this.onScrollTopListener = onScrollTopListener;
	}





}